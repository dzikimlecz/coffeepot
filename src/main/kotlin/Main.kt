package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.gui.mainPane
import java.awt.Dimension
import javax.swing.JFrame

fun main(args: Array<String>) {
    startDaemon()
    launchApp(args)

}

fun launchApp(args: Array<String>) {
    val mainFrame = JFrame("coffeepot")
    mainFrame.contentPane = mainPane
    if ("--debug" in args) {
        mainFrame.maximumSize = Dimension(480, 320)
    }
    mainFrame.isVisible = true
}

