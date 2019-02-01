package eu.markus_fischer.unikram.mailfisch.ui.views


import eu.markus_fischer.unikram.mailfisch.data.mailstore.MailFolder
import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.ui.controllers.MailListController
import tornadofx.*
import eu.markus_fischer.unikram.mailfisch.data.MailSummary
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.text.FontWeight
import javafx.stage.StageStyle
import java.util.*


class MailListView : View("MailListView") {

    val controller : MailListController by inject()

    override val root : BorderPane = borderpane {
        top = hbox {
            button("Retrieve new mails").action { runAsync { controller.retriveNewMails() } }
        }
        center= borderpane{
                top=tableview(controller.mail_summary_list) {
                    isEditable = true
                    prefHeight = 400.0
                    column("Unseen", MailSummary::unseenProperty).makeEditable()
                    readonlyColumn("From", MailSummary::from).cellFormat {
                        text = it
                        if (this.rowItem.unseen) {
                            style {
                                fontWeight = FontWeight.BOLD
                            }
                        } else {
                            style {
                                fontWeight = FontWeight.NORMAL
                            }
                        }
                    }
                    /*readonlyColumn("To", MailSummary::to).cellFormat {
                        text = it
                        if (this.rowItem.unseen) {
                            style {
                                fontWeight = FontWeight.BOLD
                            }
                        } else {
                            style {
                                fontWeight = FontWeight.NORMAL
                            }
                        }
                    }*/
                    readonlyColumn("Date", MailSummary::date).cellFormat {
                        text = it.toString()
                        if (this.rowItem.unseen) {
                            style {
                                fontWeight = FontWeight.BOLD
                            }
                        } else {
                            style {
                                fontWeight = FontWeight.NORMAL
                            }
                        }
                    }
                    readonlyColumn("Subject", MailSummary::subject).cellFormat {
                        text = it
                        if (this.rowItem.unseen) {
                            style {
                                fontWeight = FontWeight.BOLD
                            }
                        } else {
                            style {
                                fontWeight = FontWeight.NORMAL
                            }
                        }
                    }
                    onSelectionChange {
                        if (it != null) {
                            it.unseen = false
                            controller.setSeenState(it.uuid, it.unseen)
                            controller.update_mail(it.uuid)
                            refresh()
                        }
                    }
                    onEditCommit {
                        controller.setSeenState(it.uuid, it.unseen)
                        refresh()
                    }
                    onUserDelete {
                        print("Delete...")
                    }
                    contextmenu {
                        item("Delete...").action {
                            var del = false
                            confirmation("Confirm delete", "Do you really want delete this mail forever?", ButtonType.APPLY, ButtonType.CANCEL,
                                    actionFn = { if (!it.buttonData.isCancelButton()) del = true})
                            if (del) {
                                controller.delete_mail(selectedItem?.uuid ?: UUID.randomUUID())
                            }
                        }
                    }
                }
        }
    }

    init {
        update_treeview(controller.mailbox_list)
        update_mail_view(controller.mailbox_list)
    }

    fun update_treeview(folder : MailFolder) {
        root.left =  treeview<MailFolder> {
            prefHeight = 300.0
            useMaxHeight = true
            root = TreeItem(folder)
            root.isExpanded = true
            cellFormat { text = it.name }
            onUserSelect {
                controller.update_mail_summary_list(it)
                controller.current_mail_folder = it
                update_mail_view(it)
            }
            populate {
                it.value.childrens
            }
        }
    }

    fun update_mail_view(folder : MailFolder) {
        print("update_mail_view")
        root.bottom = vbox {
            useMaxWidth = true
            hbox {
                form {
                    fieldset {
                        paddingRight = 0
                        paddingTop = 0
                        paddingBottom = 0
                        useMaxWidth = true
                        spacing = 0.0
                        field("From") {
                            label(controller.showed_mail.fromProperty)
                        }
                        field("To") {
                            label(controller.showed_mail.toProperty)
                        }
                        field("Subject") {
                            label(controller.showed_mail.subjectProperty)
                        }
                        field() {
                            if (folder.name == "draft") {
                                button("Edit") {}.action {
                                    find<NewMailDialog>(mapOf(NewMailDialog::type to NewMailDialog.Type.Edit, NewMailDialog::related_mail to SQLiteMailstore().getMIMEMail(controller.showed_mail.uuid))).openWindow(stageStyle = StageStyle.UTILITY)
                                    controller.delete_mail(controller.showed_mail.uuid)
                                }
                                button("Delete") {}.action {
                                    var del = false
                                    confirmation("Confirm delete", "Do you really want delete this mail forever?", ButtonType.APPLY, ButtonType.CANCEL,
                                            actionFn = { if (!it.buttonData.isCancelButton()) del = true})
                                    if (del) {
                                        controller.delete_mail(controller.showed_mail.uuid)
                                    }
                                }
                            } else {
                                button("Answer") {}.action {
                                    find<NewMailDialog>(mapOf(NewMailDialog::type to NewMailDialog.Type.Reply, NewMailDialog::related_mail to SQLiteMailstore().getMIMEMail(controller.showed_mail.uuid))).openWindow(stageStyle = StageStyle.UTILITY)
                                }
                                button("Answer all") {}.action {
                                    find<NewMailDialog>(mapOf(NewMailDialog::type to NewMailDialog.Type.Reply_all, NewMailDialog::related_mail to SQLiteMailstore().getMIMEMail(controller.showed_mail.uuid))).openWindow(stageStyle = StageStyle.UTILITY)
                                }
                                button("Forward") {}.action {
                                    find<NewMailDialog>(mapOf(NewMailDialog::type to NewMailDialog.Type.Forward, NewMailDialog::related_mail to SQLiteMailstore().getMIMEMail(controller.showed_mail.uuid))).openWindow(stageStyle = StageStyle.UTILITY)
                                }
                                button("Delete") {}.action {
                                    var del = false
                                    confirmation("Confirm delete", "Do you really want delete this mail forever?", ButtonType.APPLY, ButtonType.CANCEL,
                                            actionFn = { if (!it.buttonData.isCancelButton()) del = true})
                                    if (del) {
                                        controller.delete_mail(controller.showed_mail.uuid)
                                    }
                                }
                            }
                        }
                    }
                }
                listview(controller.showed_mail.attachmentList) {
                    prefHeight = 100.0
                    selectionModel.selectionMode = SelectionMode.SINGLE
                    contextmenu {
                        item("Save...").action {
                            val selectItem = selectionModel.selectedItem
                            val file_name = selectItem.name
                            val newPath = chooseFile("Save $file_name", emptyArray(), mode = FileChooserMode.Save,
                                    op = {
                                        this.initialFileName = file_name
                                    })
                            selectItem.copyTo(newPath[0], true)
                        }
                    }
                }
            }
            textarea(controller.showed_mail.contentProperty) {
                isEditable=false
            }
        }

    }
}
