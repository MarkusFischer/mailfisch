package eu.markus_fischer.unikram.mailfisch.ui.views

import eu.markus_fischer.unikram.mailfisch.data.MIMEMail
import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.MessageID
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueAddressList
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueDate
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueMessageIdList
import eu.markus_fischer.unikram.mailfisch.ui.controllers.NewMailController
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.SelectionMode
import javafx.util.StringConverter
import javafx.util.converter.DefaultStringConverter
import sun.java2d.pipe.SpanShapeRenderer
import tornadofx.*
import java.util.*
import javax.swing.GroupLayout

class NewMailDialog : Fragment("New Mail") {
    private val controller : NewMailController by inject()

    override val configPath = app.configBasePath.resolve("app.config")

    enum class Type {
        New,
        Reply,
        Reply_all,
        Forward,
        Edit
    }

    val related_mail: Mail by param()
    val type : Type by param()


    private val fromProperty = SimpleStringProperty()
    private val toProperty = SimpleStringProperty()
    private val ccProperty = SimpleStringProperty()
    private val bccProperty = SimpleStringProperty()
    private val subjectProperty = SimpleStringProperty()
    private val textProperty = SimpleStringProperty()

    private val attachmentList = mutableListOf<String>().observable()

    override val root = borderpane {
        center = vbox {
            hbox {
                button("Send").action {
                    //Build mail object
                    val mail = MIMEMail()
                    mail.addHeader("subject", subjectProperty.value)
                    mail.addHeader("from", fromProperty.value)
                    mail.addHeader("to", toProperty.value)
                    mail.addHeader("cc", ccProperty.value ?: "")
                    mail.addHeader("bcc", bccProperty.value ?: "")
                    mail.addHeader("date", HeaderValueDate())
                    val smtp_from = if (mail.hasHeader("sender")) (mail.getHeader("sender").value as HeaderValueAddressList).address_list[0].getMailboxes()[0]
                                            else (mail.getHeader("from").value as HeaderValueAddressList).address_list[0].mailbox

                    mail.addHeader("message-id", HeaderValueMessageIdList(mutableListOf(MessageID(smtp_from)), true))
                    if (type == Type.Reply_all || type == Type.Reply) {
                        mail.addHeader("in-reply-to", HeaderValueMessageIdList(mutableListOf((related_mail.getHeader("message-id").value as HeaderValueMessageIdList).msgid_list[0])))
                        val reference_list = mutableListOf((related_mail.getHeader("message-id").value as HeaderValueMessageIdList).msgid_list[0])
                        if (related_mail.hasHeader("references")) {
                            reference_list.addAll((related_mail.getHeader("references").value as HeaderValueMessageIdList).msgid_list)
                        }
                        mail.addHeader("references", HeaderValueMessageIdList(reference_list))
                    }
                    mail.text = textProperty.value
                    for (attachment in attachmentList) {
                        mail.addAttachment(attachment)
                    }
                    controller.sendMail(mail)
                    close()
                }
                button("Save").action {
                    val mail = MIMEMail()
                    mail.addHeader("subject", subjectProperty.value)
                    mail.addHeader("from", fromProperty.value)
                    mail.addHeader("to", toProperty.value)
                    mail.addHeader("cc", ccProperty.value ?: "")
                    mail.addHeader("bcc", bccProperty.value ?: "")
                    mail.addHeader("date", HeaderValueDate())
                    val smtp_from = if (mail.hasHeader("sender")) (mail.getHeader("sender").value as HeaderValueAddressList).address_list[0].getMailboxes()[0]
                    else (mail.getHeader("from").value as HeaderValueAddressList).address_list[0].mailbox

                    mail.addHeader("message-id", HeaderValueMessageIdList(mutableListOf(MessageID(smtp_from)), true))
                    if (type == Type.Reply_all || type == Type.Reply) {
                        mail.addHeader("in-reply-to", HeaderValueMessageIdList(mutableListOf((related_mail.getHeader("message-id").value as HeaderValueMessageIdList).msgid_list[0])))
                        val reference_list = mutableListOf((related_mail.getHeader("message-id").value as HeaderValueMessageIdList).msgid_list[0])
                        reference_list.addAll((related_mail.getHeader("references").value as HeaderValueMessageIdList).msgid_list)
                        mail.addHeader("references", HeaderValueMessageIdList(reference_list))
                    }
                    mail.text = textProperty.value
                    for (attachment in attachmentList) {
                        mail.addAttachment(attachment)
                    }
                    controller.saveMail(mail)
                    close()
                }
                button("Attach"){}.action{
                    val files = chooseFile("Attach file...", emptyArray(), mode=FileChooserMode.Multi)
                    for (file in files) {
                        attachmentList.add(file.absolutePath)
                    }
                }
            }
            hbox {
                useMaxWidth = true
                form {
                    prefWidth=440.0
                    fieldset {
                        paddingRight = 0
                        paddingTop = 0
                        paddingBottom = 0
                        useMaxWidth = true
                        spacing = 0.0
                        field("From") {
                            textfield() {
                                fromProperty.value = "\"${config.string("displayname")}\" <${config.string("address")}>"
                                isEditable = false
                            }.bind(fromProperty, converter = DefaultStringConverter())
                        }
                        field("To") {
                            textfield() {
                                if (type == Type.Reply || type == Type.Reply_all) {
                                    toProperty.value = related_mail.getHeader("From").value.toString()
                                } else if (type == Type.Edit) {
                                    toProperty.value = related_mail.getHeader("To").value.toString()
                                }
                            }.bind(toProperty, converter = DefaultStringConverter())
                        }
                        field("Cc") {
                            textfield() {
                                if (type == Type.Reply_all) {
                                    if (related_mail.hasHeader("Sender")) {
                                        ccProperty.value = related_mail.getHeader("Sender").value.toString()
                                    }
                                    if (related_mail.hasHeader("Cc")) {
                                        if (ccProperty.value != "") {
                                            ccProperty.value += ", "
                                        }
                                        ccProperty.value += related_mail.getHeader("cc").value.toString()
                                    }
                                } else if (type == Type.Edit) {
                                    ccProperty.value = related_mail.getHeader("cc").value.toString()
                                }
                            }.bind(ccProperty, converter = DefaultStringConverter())
                        }
                        field("Bcc") {
                            textfield() {
                                if (type == Type.Edit) {
                                    bccProperty.value = related_mail.getHeader("Bcc").value.toString()
                                }
                            }.bind(bccProperty, converter = DefaultStringConverter())
                        }
                        field("Subject") {
                            textfield() {
                                val related_subject = related_mail.getHeader("subject").value.toString()
                                if (type == Type.Forward) {
                                    if (related_subject.trim().substring(0, 3) != "Fwd")
                                        subjectProperty.value = "Fwd: "
                                    else
                                        subjectProperty.value = ""
                                    subjectProperty.value += related_subject
                                } else if (type == Type.Reply || type == Type.Reply_all) {
                                    if (related_subject.trim().substring(0, 2) != "Re")
                                        subjectProperty.value = "Re: "
                                    else
                                        subjectProperty.value = ""
                                    subjectProperty.value += related_subject
                                } else if (type == Type.Edit) {
                                    subjectProperty.value = related_mail.getHeader("subject").value.toString()
                                }
                            }.bind(subjectProperty, converter = DefaultStringConverter())
                        }
                    }
                }
                listview(attachmentList) {
                    prefHeight=200.0
                    if (type == Type.Edit) {
                        if (related_mail is MIMEMail) {
                            val mimeMail = related_mail as MIMEMail
                            val attachments = mimeMail.getAttachments()
                            for (file in attachments) {
                                attachmentList.add(file.absolutePath)
                            }
                        }
                    }
                    selectionModel.selectionMode = SelectionMode.MULTIPLE
                    contextmenu {
                        item("Delete").action {
                            println("Delete")
                            attachmentList.removeAll(selectionModel.selectedItems)
                        }
                    }
                }
            }
            hbox {
                useMaxWidth=true
                textarea {
                    prefWidth = 700.0
                    prefHeight= 300.0
                    useMaxWidth = true
                    if (type == Type.Forward) {
                        textProperty.value = related_mail.getTextContent()
                    } else if (type == Type.Reply || type == Type.Reply_all) {
                        textProperty.value = ""
                        val temp = related_mail.getTextContent().split('\n')
                        for (line in temp) {
                            textProperty.value += "> $line\n"
                        }
                    } else if (type == Type.Edit) {
                        textProperty.value = related_mail.getTextContent()
                    }
                }.bind(textProperty, converter = DefaultStringConverter())
            }
        }
    }


    init {
        with (root) {
            prefWidth = 700.0
            prefHeight = 500.0
        }
    }
}
