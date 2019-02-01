package eu.markus_fischer.unikram.mailfisch.ui

import eu.markus_fischer.unikram.mailfisch.ui.views.MainWindow
import javafx.stage.Stage
import tornadofx.*

class MailfischMain: App(MainWindow::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.isMaximized = true
    }
}