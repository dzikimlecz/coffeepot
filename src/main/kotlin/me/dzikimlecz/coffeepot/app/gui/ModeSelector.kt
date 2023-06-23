package me.dzikimlecz.coffeepot.app.gui

import javafx.scene.layout.Priority
import tornadofx.*
class ModeSelector : View() {
    override val root = hbox {
        button("Clock") {
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            prefHeight = 45.0
            maxHeight = prefHeight
        }
        button("Weather") {
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            prefHeight = 45.0
            maxHeight = prefHeight
        }
        button("Transit") {
            hgrow = Priority.ALWAYS
            maxWidth = Double.POSITIVE_INFINITY
            prefHeight = 45.0
            maxHeight = prefHeight
        }
    }

}