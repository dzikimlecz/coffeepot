package me.dzikimlecz.coffeepot
import tornadofx.launch
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
}