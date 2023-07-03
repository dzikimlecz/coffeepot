package gui

import tornadofx.View
import kotlin.reflect.KClass

enum class Views(val mode: String, val view: KClass<out View>) {
    CLOCK("Clock", ClockView::class),
    WEATHER("Weather", WeatherView::class),
    TRANSIT("Transit", TransitView::class),
    ;
}