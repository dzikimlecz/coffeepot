package me.dzikimlecz.coffeepot.gui

import javafx.beans.property.SimpleObjectProperty
import tornadofx.*

class MainView : View() {
    private val viewProperty = SimpleObjectProperty(Views.CLOCK)
    var view: Views by viewProperty

    override val root = borderpane {
        center(view.view)
        bottom(ModeSelector::class)
    }

    init {
        title = "coffeepot"
        viewProperty.onChange {
            root.center(it?.view
                ?: throw Error("MainView's central pane set to null"))
        }
    }

}