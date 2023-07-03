package me.dzikimlecz.coffeepot.gui

import me.dzikimlecz.coffeepot.weatherImageProperty
import tornadofx.*
class WeatherView : View() {
    override val root = stackpane {
        scrollpane {
            imageview(weatherImageProperty) {

            }
        }
    }
}