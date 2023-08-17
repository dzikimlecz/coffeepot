package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.Resources.appDirectory
import me.dzikimlecz.coffeepot.transit.sha256
import me.dzikimlecz.coffeepot.transit.unzipTo
import me.dzikimlecz.coffeepot.transit.writeTo
import me.dzikimlecz.libgtfskt.FeedProcessor
import me.dzikimlecz.libgtfskt.UpcomingService
import me.dzikimlecz.libgtfskt.feedProcessor
import me.dzikimlecz.libgtfskt.getFeed
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.*
import java.util.stream.Collectors
import javax.imageio.ImageIO

///////////////////////////////////////////////////////////////////////////
// DAEMON
///////////////////////////////////////////////////////////////////////////

private val http = OkHttpClient()

fun startDaemon() {
    if (::executor.isInitialized && !executor.isShutdown) {
        throw IllegalStateException("Daemon has already been started.")
    }
    executor = Executors.newScheduledThreadPool(2) {
        Thread(it).apply {
            name = "coffeepotDaemon"
            isDaemon = true
            uncaughtExceptionHandler =
                Thread.UncaughtExceptionHandler { _, e ->
                    System.err.println(
                        "${e::class.simpleName} thrown on daemon"
                    )
                    System.err.println("Message: ${e.message}")
                }
        }
    }
    // try to read current feed.
    try {
        readTransitFeed()
    } catch(e: Exception) {
        println(e.message)
    }
    with(executor) {
        // update time every 5 seconds.
        scheduleAtFixedRate(
            {
                val timeStr = LocalTime.now().format(
                    DateTimeFormatter.ofPattern(
                        "HH:mm"
                    )
                )
                time.value = timeStr
            },
            0, 5,
            SECONDS
        )
        // check weather every half an hour.
        scheduleAtFixedRate(
            {
                try {
                    getWeather(Resources.location)
                    fetchWeatherImage(
                        Resources.location
                    )
                } catch (e: IOException) {
                    println(e.message)
                }
            },
            0, 30,
            MINUTES
        )
        // check for feed updates every day.
        scheduleAtFixedRate(
            {
                try {
                    val feedUpdated = fetchTransitFeed()
                    if (feedUpdated) {
                        readTransitFeed()
                        getUpcomingServices()
                    }
                } catch (e: Exception) {
                    println(e.message)
                }
            },
            1, 1,
            DAYS
        )
        // Check for upcoming services every 2 minutes
        scheduleAtFixedRate(
            {
                try {
                    getUpcomingServices()
                } catch (e: Exception) {
                    println(e.message)
                }
            },
            0, 2,
            MINUTES
        )
    }
}

fun stopDaemon() {
    if (!::executor.isInitialized || executor.isShutdown) {
        throw IllegalStateException("Daemon isn't running.")
    }
    executor.shutdown()
}

private lateinit var executor: ScheduledExecutorService

///////////////////////////////////////////////////////////////////////////
// TIME
///////////////////////////////////////////////////////////////////////////

private val time = MutableObservable("")

val timeProperty: Observable<String> = time

///////////////////////////////////////////////////////////////////////////
// WEATHER
///////////////////////////////////////////////////////////////////////////


val weatherProperty: Observable<String>
    get() = weather
private val weather = MutableObservable("Zimno jest")

val weatherImageProperty: Observable<BufferedImage>
    get() = weatherImage
private val weatherImage =
    MutableObservable<BufferedImage>(ImageIO.read(object {}.javaClass
        .classLoader.getResource(Resources.weatherImagePath)))

fun fetchWeatherImage(location: String) {
    val url = "https://v2.wttr.in/$location.png"
    val response = get(url) { e, code ->
        failFetchingWeather(location, url, e, code)
    }
    val stream = response.body?.byteStream() ?: failFetchingWeather(location, url)
    val img = ImageIO.read(stream)
    response.close()
    weatherImage.value = img
}

fun getWeather(location: String) {
    val url = "https://www.wttr.in/$location?format=3"
    val response: Response = get(url) { e, code ->
        failFetchingWeather(location, url, e,  code)
    }
    val theWeather = response.body?.string() ?: failFetchingWeather(location, url)
    response.close()
    weather.value = theWeather
}

private fun failFetchingWeather(
    location: String,
    url: String,
    e: Exception? = null,
    code: Int? = null
): Nothing = failFetching(
    "Couldn't fetch weather for $location",
    url,
    code,
    e
)


///////////////////////////////////////////////////////////////////////////
// Transit
///////////////////////////////////////////////////////////////////////////


val servicesProperty: ObservableList<UpcomingService>
    get() = services
private val services: ObservableMutableList<UpcomingService> =
    observableMutableListOf()

fun fetchTransitFeed(): Boolean {
    val newArchive = downloadTransitFeed()
    val currentArchive = File(appDirectory, "archive/transit.zip")
    val feedChanged = sha256(currentArchive) != sha256(newArchive)
    if (feedChanged) {
        try {
            newArchive unzipTo File(appDirectory, ".cache")
            newArchive writeTo currentArchive
            newArchive.delete()
        } catch (e: Exception) {
            throw IOException("Could not save GTFS feed.", e)
        }
    }
    return feedChanged
}

private fun downloadTransitFeed(): File {
    val url = Resources.transitFeedUrl
    val response = get(url) { e, code ->
        failFetching("Failed fetching GTFS feed", url, code, e)
    }
    val responseStream = response.body?.byteStream() ?: failFetchingWeather(
        "Failed fetching GTFS feed", url, code = response.code)
    val file = File(appDirectory, "archive/new.zip")
    responseStream writeTo file
    response.close()
    return file
}

private lateinit var feedReader: FeedProcessor

private fun readTransitFeed() {
    val feed = getFeed(File(appDirectory, ".cache"))
    feedReader = feedProcessor(feed)
    services.clear()
}

private fun getUpcomingServices(): List<UpcomingService> {
    if (!::feedReader.isInitialized) {
        throw IllegalStateException("No feed has been read.")
    }
    val upcomingServices = Resources.trackedStops.stream()
        .map(feedReader::getUpcomingServicesFor)
        .flatMap(List<UpcomingService>::stream)
        .collect(Collectors.toList())
    services.retainAll(upcomingServices)
    val newServices = (upcomingServices - services)
        .sortedBy(UpcomingService::departure)
    services += newServices
    return newServices
}

///////////////////////////////////////////////////////////////////////////
// UTIL
///////////////////////////////////////////////////////////////////////////

private fun get(url: String, onFail: (e: Exception?, code: Int?) -> Nothing): Response {
    val request = Request.Builder()
        .url(url)
        .build()
    val response: Response
    try {
        response = http.newCall(request).execute()
    } catch (e: Exception) {
        onFail(e, null)
    }
    if (!response.isSuccessful) {
        onFail(null, response.code)
    }
    return response
}

private fun failFetching(
    msg: String,
    url: String,
    code: Int? = null,
    cause: Exception? = null,
): Nothing {
    val message = buildString {
        append(msg)
        append("; ")
        if (code != null) {
            append("Status: ")
            append(code)
            append("; ")
        }
        append("URL: ")
        append(url)
        if (cause != null) {
            append("; Caused by: ")
            append(cause.javaClass.name)
        }
        append('.')
    }

    throw if (cause != null) IOException(message, cause) else IOException(message)
}