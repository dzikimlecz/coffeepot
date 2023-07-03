package me.dzikimlecz.coffeepot
import javafx.stage.Stage
import me.dzikimlecz.coffeepot.gui.MainView
import me.dzikimlecz.coffeepot.gui.Stylesheets
import tornadofx.App
import tornadofx.launch

fun main(args: Array<String>) = launch<CoffeePot>(args)

class CoffeePot : App(MainView::class, Stylesheets::class) {
    override fun init() {
        startDaemon()
    }

    override fun stop() {
        stopDaemon()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        if ("--debug" in parameters.unnamed) {
            stage.maxHeight = 320.0
            stage.maxWidth = 480.0
        }
    }
}