package me.dzikimlecz.coffeepot.daemon

import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleStringProperty
import java.util.concurrent.ScheduledExecutorService



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




