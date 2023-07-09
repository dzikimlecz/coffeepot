package me.dzikimlecz.coffeepot.gui

import me.dzikimlecz.coffeepot.MutableObservable
import me.dzikimlecz.coffeepot.gui.Panes.CLOCK
import me.dzikimlecz.coffeepot.timeProperty
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.View.X_AXIS

private val currentPane = MutableObservable(CLOCK)

val mainPane: JPanel by lazy {
    JPanel().apply {
        layout = BorderLayout()
        add(modeSelector, BorderLayout.SOUTH)
        add(clockPane, CENTER)
        currentPane.registerListener { old, new ->
            remove(old.pane())
            add(new.pane(), CENTER)
            validate()
        }
    }
}

val modeSelector: JPanel by lazy {
    JPanel().apply {
        layout = BoxLayout(this, X_AXIS)
        for (pane in Panes.values()) {
            add(JButton(pane.title).apply {
                this.addActionListener {
                    currentPane.value = pane
                }
            })
        }
    }
}



enum class Panes(val title: String, val pane: () -> JPanel) {
    CLOCK("Clock", { clockPane }),
    WEATHER("Weather", { weatherPane }),
    TRANSIT("Transit", { transitPane }),
    ;
}

val clockPane: JPanel
    get() = JPanel().apply {
        layout = GridBagLayout()
        val timeLabel = JLabel(timeProperty.value)
        timeProperty.registerListener { _, new ->
            timeLabel.text = new
            validate()
        }
        timeLabel.font = Font("Raleway", Font.BOLD, 80)
        add(timeLabel, GridBagConstraints().apply { gridy = 0 })
        val weatherLabel = JLabel("Zimno jest")
        weatherLabel.font = Font("Raleway", Font.PLAIN, 30)
        add(weatherLabel, GridBagConstraints().apply { gridy = 1 })
    }

val weatherPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JLabel("W"), CENTER)
    }

val transitPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JLabel("T"), CENTER)
    }
