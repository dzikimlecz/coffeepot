package gui

import javafx.scene.layout.Priority
import tornadofx.*
class ModeSelector : View() {
    val mainView: MainView by inject()
    override val root = hbox {
        for (view in Views.values()) {
            button(view.mode) {
                hgrow = Priority.ALWAYS
                maxWidth = Double.POSITIVE_INFINITY
                prefHeight = 45.0
                maxHeight = prefHeight
                action {
                    mainView.view = view
                }
            }
        }
    }
}