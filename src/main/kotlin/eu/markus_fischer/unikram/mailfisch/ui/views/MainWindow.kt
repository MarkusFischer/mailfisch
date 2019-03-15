package eu.markus_fischer.unikram.mailfisch.ui.views

import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.mailstore.Mailstore
import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import javafx.stage.StageStyle
import tornadofx.*
import java.lang.reflect.Type

class MainWindow : View("Mailfisch - Hauptfenster") {

    override val root = borderpane {
        top = vbox {
            menubar {
                menu("File") {
                    menu("New") {
                        item("Mail", "Shortcut+N").action { find<NewMailDialog>(mapOf(NewMailDialog::related_mail to Mail(), NewMailDialog::type to NewMailDialog.Type.New)).openWindow(stageStyle = StageStyle.UTILITY) }
                    }
                    separator()
                    item("Quit", "Shortcut+Q").action { close() }
                }
                menu("Edit") {
                    item("Account config ", "Shortcut+K").action { find<AccountCreateWizard>().openWindow(stageStyle = StageStyle.DECORATED) }
                }
            }
        }
        center<MailListView>()
    }

    init {

    }

}
