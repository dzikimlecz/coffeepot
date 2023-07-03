package gui


import daemon.timeProperty
import daemon.weatherProperty
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