package me.dzikimlecz.coffeepot


import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.awt.image.BufferedImage
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS
import javax.imageio.ImageIO

///////////////////////////////////////////////////////////////////////////
// DAEMON
///////////////////////////////////////////////////////////////////////////

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
    with(executor) {
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

private val http = OkHttpClient()

val weatherProperty: Observable<String>
    get() = weather
private val weather = MutableObservable("Zimno jest")

val weatherImageProperty: Observable<BufferedImage>
    get() = weatherImage
private val weatherImage =
    MutableObservable<BufferedImage>(ImageIO.read(object {}.javaClass
        .classLoader.getResource("w.png")))

fun fetchWeatherImage(location: String) {
    val request = Request.Builder()
        .url("https://v2.wttr.in/$location.png")
        .build()
    val response: Response
    try {
        response = http.newCall(request).execute()
    } catch (e: Exception) {
        failFetchingWeather(location, e)
    }
    if (response.code != 200) {
        failFetchingWeather(location)
    }
    val stream = response.body?.byteStream() ?: failFetchingWeather(location)
    val img = ImageIO.read(stream)
    weatherImage.value = img
}

fun getWeather(location: String) {
    val request = Request.Builder()
        .url("https://www.wttr.in/$location?format=3")
        .build()
    val response: Response
    try {
        response = http.newCall(request).execute()
    } catch (e: Exception) {
        failFetchingWeather(location, e)
    }
    if (response.code != 200) {
        failFetchingWeather(location)
    }
    val weather = response.body?.string() ?: failFetchingWeather(location)
    me.dzikimlecz.coffeepot.weather.value = weather
}

private fun failFetchingWeather(
    location: String,
    e: Exception? = null
): Nothing {
    if (e != null) {
        throw IOException("Couldn't fetch weather for $location", e)
    }
    throw IOException("Couldn't fetch weather for $location")
}




