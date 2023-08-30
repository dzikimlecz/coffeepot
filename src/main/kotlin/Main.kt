package me.dzikimlecz.coffeepot

import me.dzikimlecz.coffeepot.gui.mainPane
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileNotFoundException
import javax.swing.JFrame
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    init()
    launchApp(args)
}

fun init() {
    System.setProperty(
        "swing.aatext", "true")
    System.setProperty(
        "awt.useSystemAAFontSettings", "on")
    System.setProperty(
        "swing.defaultlaf", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
    startDaemon()
}

val debugOn: Boolean
    get() = debug ?: false

val configFile
    get() = config

private var config: File? = null
private var debug: Boolean? = null

fun launchApp(args: Array<String>) {
    if (debug != null) {
        throw IllegalStateException("Application has already been started")
    }
    debug = "--debug" in args
    val configFileSet = "--config" in args || "-c" in args
    if (configFileSet) {
        var pathIx = args.indexOf("--config") + 1
        if (pathIx == 0) {
            pathIx = args.indexOf("-c") + 1
        }
        require(pathIx < args.size) {
            "Empty config path!"
        }
        val configFile = File(args[pathIx])
        if (!configFile.exists()) {
            throw FileNotFoundException("File ${args[pathIx]} not found.")
        }
        require(configFile.isFile && configFile.canRead()) {
            "Can't read from ${args[pathIx]}"
        }
        config = configFile
    }
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

