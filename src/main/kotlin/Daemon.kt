package me.dzikimlecz.coffeepot

import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.MINUTES
import java.util.concurrent.TimeUnit.SECONDS

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

            },
            0, 5,
            SECONDS
        )
        scheduleAtFixedRate(
            {
                getWeather(Resources.location)
                fetchWeatherImage(
                    Resources.location
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



///////////////////////////////////////////////////////////////////////////
// WEATHER
///////////////////////////////////////////////////////////////////////////



fun fetchWeatherImage(location: String) {

}

fun getWeather(location: String) {

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




