package me.dzikimlecz.coffeepot.gui


import me.dzikimlecz.coffeepot.timeProperty
import me.dzikimlecz.coffeepot.weatherProperty
import javafx.geometry.Pos
import tornadofx.*

class ClockView : View() {
    override val root = vbox {
        alignment = Pos.CENTER
        label(timeProperty) {

        }
        label(weatherProperty) {

        }
    }
}