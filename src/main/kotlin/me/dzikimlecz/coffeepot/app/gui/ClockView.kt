package me.dzikimlecz.coffeepot.app.gui


import javafx.geometry.Pos
import tornadofx.*

class ClockView : View() {
    override val root = vbox {
        alignment = Pos.CENTER
        label("clock") {

        }
        label("weather") {

        }
    }
}