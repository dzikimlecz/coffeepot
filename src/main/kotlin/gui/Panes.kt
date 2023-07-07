package me.dzikimlecz.coffeepot.gui

import java.awt.BorderLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.text.View.X_AXIS


val mainPane: JPanel by lazy {
    JPanel().apply {
        layout = BorderLayout()
        add(modeSelector, BorderLayout.SOUTH)
        add(clockPane, BorderLayout.CENTER)
    }
}

val modeSelector: JPanel by lazy {
    JPanel().apply {
        layout = BoxLayout(this, X_AXIS)
        for (pane in Panes.values()) {
            add(JButton(pane.title).apply {
                this.addActionListener {

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
        layout = BorderLayout()
        add(JLabel("C"), BorderLayout.CENTER)
    }

val weatherPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JLabel("W"), BorderLayout.CENTER)
    }

val transitPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JLabel("T"), BorderLayout.CENTER)
    }
