package me.dzikimlecz.coffeepot.daemon

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.Rest
import tornadofx.find
import java.io.IOException
import java.nio.file.Path


private val http = find<Rest>()

fun startDaemon() {

}

fun stopDaemon() {

}

val timeProperty: ReadOnlyStringProperty
    get() = _timeProperty

val weatherProperty: ReadOnlyStringProperty
    get() = _weatherProperty

val weatherImageProperty: ReadOnlyStringProperty
    get() = _weatherImageProperty

private val _timeProperty = SimpleStringProperty("21:37")
private val _weatherProperty = SimpleStringProperty("piździ")
private val _weatherImageProperty = SimpleStringProperty()

fun fetchWeatherImage(location: String, savePath: String = "w.png") {
    val bin: ByteArray
    try {
        bin = http.get("https://v2.wttr.in/$location.png").bytes()
    } catch (e: Exception) {
        throw IOException("Couldn't fetch weather for $location", e)
    }
    val out = Path.of(savePath).toFile()
    if (!out.canWrite()) {
        out.parentFile.mkdirs()
        out.createNewFile()
        out.setWritable(true)
    }
    if (out.canWrite()) {
        out.writeBytes(bin)
    } else {
        throw IOException("Cannot write to $savePath")
    }
}


