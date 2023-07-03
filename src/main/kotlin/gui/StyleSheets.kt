package me.dzikimlecz.coffeepot.gui

import javafx.scene.paint.Color
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

class Stylesheets: Stylesheet() {
    companion object {
        // ClockView
        val clockView by cssclass()
        val clock by cssclass()
        val weatherLine by cssclass()
    }

    init {
        clockView {
            backgroundColor += Color.BLACK
        }
        clock {
            textFill = Color.WHITE
            fontSize = 40.px
        }
        weatherLine {
            textFill = Color.WHITE
            fontSize = 30.px
        }

    }
}