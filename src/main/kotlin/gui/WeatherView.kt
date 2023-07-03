package me.dzikimlecz.coffeepot.gui

import javafx.scene.control.ScrollPane
import me.dzikimlecz.coffeepot.weatherImageProperty
import tornadofx.View
import tornadofx.imageview
import tornadofx.scrollpane
import tornadofx.stackpane

class WeatherView : View() {
    override val root = stackpane {
        scrollpane {
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER

            imageview(weatherImageProperty) {

            }
        }
    }
}