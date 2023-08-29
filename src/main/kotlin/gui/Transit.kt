package me.dzikimlecz.coffeepot.gui

import me.dzikimlecz.coffeepot.servicesProperty
import me.dzikimlecz.libgtfskt.UpcomingService
import java.awt.*

import java.awt.BorderLayout.CENTER
import java.time.format.DateTimeFormatter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.OverlayLayout

val transitPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JScrollPane(servicesContainer), CENTER)
    }

val servicesContainer: JPanel
    get() = JPanel().apply {
        layout = GridBagLayout()
        addServices()
        servicesProperty.registerOnCleared {
            removeAll()
            invalidate()
        }
        servicesProperty.registerOnElementRemoved {
            startUpdate()
        }
        servicesProperty.registerOnElementAdded {
            startUpdate()
        }
    }

private fun JPanel.addServices(): Int {
    var i = 0
    val groupedByStop = servicesProperty.groupBy {
        it.stop.code to it.stop.name
    }
    for ((stop, services) in groupedByStop) {
        val (code, name) = stop
        val stopLabel = JLabel("$name ($code)").apply {
            font = raleway.deriveFont(30f)
        }
        add(stopLabel, GridBagConstraints().apply {
            gridx = 0
            ipadx = 5
            ipady = 5
            gridy = i
            weighty = 3.0
        })
        i += 3
        for (service in services) {
            add(servicePane(service), GridBagConstraints().apply {
                gridx = 0
                ipadx = 10
                ipady = 5
                gridy = i++
            })
        }
    }
    return i
}

fun servicePane(service: UpcomingService) =
    JPanel().apply {
        layout = FlowLayout()
        val color =
            try { Color.decode("#${service.line.color}") }
            catch(_: Exception) { Color.DARK_GRAY }
        val name = service.line.shortName.ifEmpty { service.line.longName }
        val lineSection = buildString {
            append(
                service.departure
                    .format(DateTimeFormatter.ofPattern("HH:mm"))
            )
            append(" — ")
            append(name)
        }

        add(ColorDot(color))
        add(JLabel(lineSection).apply {  font = raleway })
        add(JLabel(" ➟ ").apply {
            font = font.deriveFont(raleway.size.toFloat())
        })
        add(JLabel(service.direction).apply { font = raleway })
    }

private val raleway = Font("Raleway", Font.BOLD, 25)

private class ColorDot(val color: Color) : JPanel() {
    override fun paint(g: Graphics?) {
        super.paint(g)
        if (g != null) {
            with(g) {
                color = this@ColorDot.color
                fillOval(0, 0, 20, 20)
                color = Color.WHITE
                drawOval(1, 1, 20, 20)
            }
        }
    }

    init {
        layout = OverlayLayout(this)
        add(JLabel(" ".repeat(4)).apply {
            font = raleway
        })
    }
}

private val updater = Executors.newScheduledThreadPool(1)
private var updateRunning = false

private fun JPanel.startUpdate() {
    if (updateRunning) return
    updateRunning = true
    updater.schedule( {
        updateRunning = false
        removeAll()
        addServices()
        invalidate()
    }, 500, TimeUnit.MILLISECONDS )
}




