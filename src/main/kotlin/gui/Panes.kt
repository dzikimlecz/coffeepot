package me.dzikimlecz.coffeepot.gui

import me.dzikimlecz.coffeepot.MutableObservable
import me.dzikimlecz.coffeepot.gui.Panes.CLOCK
import me.dzikimlecz.coffeepot.timeProperty
import me.dzikimlecz.coffeepot.weatherImageProperty
import me.dzikimlecz.coffeepot.weatherProperty
import java.awt.*
import java.awt.BorderLayout.CENTER
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.OverlayLayout

private val currentPane = MutableObservable(CLOCK)

val mainPane: JPanel by lazy {
    JPanel().apply {
        layout = BorderLayout()
        add(modeSelector, BorderLayout.SOUTH)
        add(currentPane.value.pane(), CENTER)
        currentPane.registerListener { _, new ->
            removeAll()
            timeProperty.dropListeners()
            weatherProperty.dropListeners()
            weatherImageProperty.dropListeners()
            add(modeSelector, BorderLayout.SOUTH)
            add(new.pane(), CENTER)
            validate()
        }
    }
}

val modeSelector: JPanel by lazy {
    JPanel().apply {
        layout = FlowLayout()
        background = Color.BLACK
        for (pane in Panes.values()) {
            add(
                JButton(pane.title).apply {
                    addActionListener { currentPane.value = pane }
                    font = Font("Sans", Font.PLAIN, 20)
                    background = Color.BLACK
                    foreground = Color.WHITE
                }
            )
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
        background = Color.BLACK
        layout = GridBagLayout()
        val timeLabel = JLabel(timeProperty.value)
        timeProperty.registerListener { _, new ->
            timeLabel.text = new
            invalidate()
        }
        timeLabel.font = Font("Raleway", Font.BOLD, 80)
        timeLabel.foreground = Color.WHITE
        add(timeLabel, GridBagConstraints().apply { gridy = 0 })
        val weatherLabel = JLabel(weatherProperty.value)
        weatherProperty.registerListener { _, new ->
            weatherLabel.text = new
            invalidate()
        }
        weatherLabel.font = Font("", Font.PLAIN, 30)
        weatherLabel.foreground = Color.WHITE
        add(weatherLabel, GridBagConstraints().apply { gridy = 1 })
    }

val weatherPane: JPanel
    get() = JPanel().apply {
        layout = OverlayLayout(this)
        val icon = ImageIcon(weatherImageProperty.value)
        weatherImageProperty.registerListener { _, new ->
            icon.image = new
            invalidate()
        }
        val jLabel = JLabel(icon).apply {
            autoscrolls = true
            addMouseMotionListener(object : MouseMotionListener {
                override fun mouseDragged(e: MouseEvent?) {
                    if (e != null) {
                        scrollRectToVisible(
                            Rectangle(
                                e.x, e.y,
                                1, 1
                            )
                        )
                    }
                }

                override fun mouseMoved(e: MouseEvent?) {}
            })
        }
        add(JScrollPane(jLabel).apply {

        })
    }

val transitPane: JPanel
    get() = JPanel().apply {
        layout = BorderLayout()
        add(JLabel("T"), CENTER)
    }
