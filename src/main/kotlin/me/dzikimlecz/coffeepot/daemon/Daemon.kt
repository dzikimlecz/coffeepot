package me.dzikimlecz.coffeepot.daemon

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import me.dzikimlecz.coffeepot.Resources
import tornadofx.Rest
import tornadofx.RestException
import tornadofx.find
import java.io.IOException
import java.nio.file.Path
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.TimeUnit.MINUTES

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
        }
    }
    with(executor) {
        scheduleAtFixedRate(
            {
                time.set(
                    LocalTime.now().format(
                        DateTimeFormatter.ofPattern(
                            "HH:mm"
                        )
                    )
                )
            },
            0, 5,
            SECONDS
        )
        scheduleAtFixedRate(
            {
                getWeather(Resources.location)
                fetchWeatherImage(
                    Resources.location,
                    Resources.weatherImagePath
                )
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

val timeProperty: ReadOnlyStringProperty
    get() = time

private val time = SimpleStringProperty()

///////////////////////////////////////////////////////////////////////////
// WEATHER
///////////////////////////////////////////////////////////////////////////

val weatherProperty: ReadOnlyStringProperty
    get() = weather

val weatherImageProperty: ReadOnlyStringProperty
    get() = weatherImage

private val weather = SimpleStringProperty()
private val weatherImage = SimpleStringProperty()
private val http = find<Rest>()

fun fetchWeatherImage(location: String, savePath: String = "w.png") {
    val response: Rest.Response
    try {
        response = http.get("https://v2.wttr.in/$location.png")
    } catch (e: Exception) {
        failFetchingWeather(location, e)
    }
    if (!response.ok()) {
        failFetchingWeather(location)
    }
    with(Path.of(savePath).toFile()) {
        if (!canWrite()) {
            createNewFile()
            setWritable(true)
        }
        if (canWrite()) {
            writeBytes(response.bytes())
        } else {
            throw IOException("Cannot write to $savePath")
        }
    }
}

fun getWeather(location: String) {
    val response: Rest.Response
    try {
        response = http.get("https://wttr.in/$location?format=3")
    } catch (e: RestException) {
        failFetchingWeather(location, e)
    }
    if (!response.ok()) {
        failFetchingWeather(location)
    }
    weather.set(response.text())
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




