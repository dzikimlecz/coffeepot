package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.gui.mainPane
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.UIManager
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    init()
    launchApp(args)
}

fun init() {
    try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
    } catch (_: Exception) {}
    startDaemon()
}

fun launchApp(args: Array<String>) {
    val mainFrame = JFrame("coffeepot")
    with(mainFrame) {
        contentPane = mainPane
        if ("--debug" in args) {
            maximumSize = Dimension(480, 320)
        }
        contentPane = mainPane
        if ("--debug" in args) {
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

