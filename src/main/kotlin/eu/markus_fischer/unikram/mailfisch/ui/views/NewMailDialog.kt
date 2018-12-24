package eu.markus_fischer.unikram.mailfisch.ui.views

import tornadofx.*
import javax.swing.GroupLayout

class NewMailDialog : Fragment("New Mail") {
    override val root = borderpane {
        top = menubar {
            menu ("File") { }
            menu ("Edit") {}
            menu ("Extras") {}
        }
        center = vbox {
            hbox {
                button("Send")
                button("Save")
            }

            form {
                fieldset {
                    paddingRight = 0
                    paddingTop = 0
                    paddingBottom = 0
                    useMaxWidth = true
                    spacing = 0.0
                    field("From") {
                        textfield()
                    }
                    field("To") {
                        textfield()
                    }
                    field("Cc") {
                        textfield()
                    }
                    field("Bcc") {
                        textfield()
                    }
                    field("Subject") {
                        textfield()
                    }
                }
            }
            hbox {
                textarea {
                    useMaxWidth = true
                }
            }
        }
    }
}
