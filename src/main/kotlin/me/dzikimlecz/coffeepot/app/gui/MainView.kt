package me.dzikimlecz.coffeepot.app.gui

import tornadofx.*

class MainView : View() {
    override val root = borderpane {
        center(ClockView::class)
        bottom(ModeSelector::class)
    }
}