package me.dzikimlecz.coffeepot
import daemon.startDaemon
import daemon.stopDaemon
import tornadofx.launch
import me.dzikimlecz.coffeepot.gui.MainView
import tornadofx.App

fun main(args: Array<String>) = launch<CoffeePot>(args)

class CoffeePot : App(MainView::class) {
    override fun init() {
        startDaemon()
    }

    override fun stop() {
        stopDaemon()
    }
}