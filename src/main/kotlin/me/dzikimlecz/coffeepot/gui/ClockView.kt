package me.dzikimlecz.coffeepot.gui


import javafx.geometry.Pos
import me.dzikimlecz.coffeepot.daemon.*
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