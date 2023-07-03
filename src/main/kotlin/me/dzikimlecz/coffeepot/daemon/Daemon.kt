package me.dzikimlecz.coffeepot.daemon

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Rest
import tornadofx.RestException
import tornadofx.find
import java.io.IOException
import java.nio.file.Path

///////////////////////////////////////////////////////////////////////////
// DAEMON
///////////////////////////////////////////////////////////////////////////

fun startDaemon() {

}

fun stopDaemon() {

}


///////////////////////////////////////////////////////////////////////////
// TIME
///////////////////////////////////////////////////////////////////////////

val timeProperty: ReadOnlyStringProperty
    get() = time

private val time = SimpleStringProperty("21:37")

///////////////////////////////////////////////////////////////////////////
// WEATHER
///////////////////////////////////////////////////////////////////////////

val weatherProperty: ReadOnlyStringProperty
    get() = weather

val weatherImageProperty: ReadOnlyStringProperty
    get() = weatherImage

private val weather = SimpleStringProperty("piździ")
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




