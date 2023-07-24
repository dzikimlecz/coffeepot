package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.gui.mainPane
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    init()
    launchApp(args)
}

fun init() {
    System.setProperty("swing.aatext", "true")
    System.setProperty("awt.useSystemAAFontSettings", "on")
    System.setProperty(
        "swing.defaultlaf", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
    startDaemon()
}

val debugOn: Boolean
    get() = debug ?: false

private var debug: Boolean? = null

fun launchApp(args: Array<String>) {
    if (debug != null) {
        throw IllegalStateException("Application has already been started")
    }
    debug = "--debug" in args
    val mainFrame = JFrame("coffeepot")
    with(mainFrame) {
        contentPane = mainPane
        if (debugOn) {
            println("debug")
            maximumSize = Dimension(480, 320)
        }
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                stopDaemon()
                exitProcess(0)
            }
        })
        isVisible = true
    }


}

