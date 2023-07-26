package me.dzikimlecz.coffeepot.gui

import me.dzikimlecz.coffeepot.servicesProperty
import me.dzikimlecz.coffeepot.transit.Service
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.time.format.DateTimeFormatter
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.View.Y_AXIS

val transitPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(servicesContainer, CENTER)
    }

val servicesContainer: JPanel
    get() = JPanel().apply {
        layout = GridBagLayout()
        var maxIndex = addServices()
        servicesProperty.registerOnCleared {
            removeAll()
            invalidate()
        }
        servicesProperty.registerOnElementRemoved {
            removeAll()
            maxIndex = addServices()
            invalidate()
        }
        servicesProperty.registerOnElementAdded {
            add(servicePane(it), GridBagConstraints().apply {
                gridx = 0
                ipadx = 10
                ipady = 5
                gridy = ++maxIndex
            })
            invalidate()
        }
    }

private fun JPanel.addServices(): Int {
    for ((i, service) in servicesProperty.withIndex()) {
        add(servicePane(service), GridBagConstraints().apply {
            gridx = 0
            ipadx = 10
            ipady = 5
            gridy = i
        })
    }
    return servicesProperty.lastIndex
}


fun servicePane(service: Service) = JPanel().apply {
    layout = FlowLayout()

    val lineLabel = JLabel(service.line)
    val directionLabel = JLabel(service.direction)
    val departureLabel = JLabel(
        service.departure.format(DateTimeFormatter.ofPattern("HH:mm"))
    )
    val stopLabel = JLabel("(${service.stop})")
    add(lineLabel)
    add(directionLabel)
    add(departureLabel)
    add(stopLabel)
    components.forEach {
        if (it is JLabel) {
            it.font = Font("Raleway", Font.BOLD, 20)
        }
    }
}


