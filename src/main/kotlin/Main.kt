package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.gui.mainPane
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    startDaemon()
    launchApp(args)

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

