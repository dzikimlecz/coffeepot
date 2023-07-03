package me.dzikimlecz.coffeepot.gui


import me.dzikimlecz.coffeepot.timeProperty
import me.dzikimlecz.coffeepot.weatherProperty
import javafx.geometry.Pos
import tornadofx.*

class ClockView : View() {
    override val root = vbox {
        addClass(Stylesheets.clockView)
        alignment = Pos.CENTER
        label(timeProperty) {
            addClass(Stylesheets.clock)
        }
        label(weatherProperty) {
            addClass(Stylesheets.weatherLine)
        }
    }
}