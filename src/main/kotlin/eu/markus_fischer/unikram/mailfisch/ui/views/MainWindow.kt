package eu.markus_fischer.unikram.mailfisch.ui.views

import javafx.stage.StageStyle
import tornadofx.*

class MainWindow : View("Mailfisch - Hauptfenster") {
    override val root = borderpane {
        top = menubar {
            menu("File") {
                menu("New") {
                    item("Mail", "Shortcut+N").action { find<NewMailDialog>().openWindow(stageStyle = StageStyle.DECORATED) }
                }
                separator()
                item("Print")
                item("Quit", "Shortcut+Q").action{ println("Quit!")}
            }
            menu("Edit") {
                item("Options")
                item("Account config <temp>", "Shortcut+K").action { find<AccountCreateWizard>().openWindow(stageStyle = StageStyle.DECORATED) }
            }
            menu("Help") {
                item("About")
                item("Manual")
            }
        }
    }

    init {
        with (root) {
            prefWidth = 800.0
            prefHeight = 600.0
        }
    }

}
