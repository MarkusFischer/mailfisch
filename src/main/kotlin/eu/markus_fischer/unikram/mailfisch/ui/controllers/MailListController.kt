package eu.markus_fischer.unikram.mailfisch.ui.controllers

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.MailSummary
import eu.markus_fischer.unikram.mailfisch.data.mailstore.MailFolder
import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.data.mailstore.buildMailFoldersOutOfList
import eu.markus_fischer.unikram.mailfisch.data.mailstore.buildReversePath
import eu.markus_fischer.unikram.mailfisch.protocols.IMAPFlags
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver
import eu.markus_fischer.unikram.mailfisch.protocols.transformFlagListToInt
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.io.File
import java.util.*
import javax.mail.internet.MimeUtility


class MailShow() {
    val fromProperty = SimpleStringProperty(this, "from", "")
    var from by fromProperty

    val subjectProperty = SimpleStringProperty(this, "subject", "")
    var subject by subjectProperty

    val toProperty = SimpleStringProperty(this, "to", "")
    var to by toProperty

    val ccProperty = SimpleStringProperty(this, "cc", "")
    var cc by ccProperty

    val contentProperty = SimpleStringProperty(this, "content", "")
    var content by contentProperty

    var uuid : UUID = UUID.randomUUID()

    var attachmentList = mutableListOf<File>().observable()
}

class MailListController : Controller() {

    override val configPath = app.configBasePath.resolve("app.config")

    var mailbox_list : MailFolder = buildMailFoldersOutOfList(config.string("address") ?: "", SQLiteMailstore().getMailboxes())

    var mail_summary_list = SQLiteMailstore().getMailSummarys("inbox").observable()


    var current_mail_folder : MailFolder = mailbox_list

    val showed_mail : MailShow = MailShow()

    fun update_mail_summary_list(folder : MailFolder) {
        val path = buildReversePath(folder)
        mail_summary_list.clear()
        mail_summary_list.addAll(SQLiteMailstore().getMailSummarys(path))

    }

    fun update_mail(uuid: UUID) {
        val mail = SQLiteMailstore().getMIMEMail(uuid)
        showed_mail.from = mail.getHeader("from").value.toString()
        showed_mail.to = mail.getHeader("to").value.toString()
        showed_mail.subject = MimeUtility.decodeText(mail.getHeader("subject").value.toString())
        showed_mail.content = mail.getTextContent()
        showed_mail.uuid = uuid
        showed_mail.attachmentList.clear()
        showed_mail.attachmentList.addAll(mail.getAttachments())
    }

    fun setSeenState(uuid: UUID, unseen: Boolean) {
        if (current_mail_folder == mailbox_list) {
            for (child in mailbox_list.childrens) {
                if (child.name == "inbox") {
                    current_mail_folder = child
                    break
                }
            }
        }
        val mail = SQLiteMailstore().getMail(uuid)
        val flags = SQLiteMailstore().getFlags(uuid).toMutableList()
        if (unseen && flags.contains(IMAPFlags.SEEN)) {
            flags.remove(IMAPFlags.SEEN)
        } else if (!unseen && !flags.contains(IMAPFlags.SEEN)) {
            flags.add(IMAPFlags.SEEN)
        }
        SQLiteMailstore().modifyMail(uuid, mail, transformFlagListToInt(flags), current_mail_folder.name)
    }

    fun retriveNewMails() {
        val security_method = when (config.string("pop3security")) {
            "SSL" -> Account.POP3Security.SSL
            "STARTTLS" -> Account.POP3Security.STARTTLS
            else -> Account.POP3Security.None
        }
        val rec = POP3Receiver(hostname = config.string("pop3server"),
                port = config.int("pop3port") ?: 0,
                use_ssl = security_method == Account.POP3Security.SSL)
        if (rec.connect()) {
            if (security_method == Account.POP3Security.STARTTLS) {
                rec.useSTARTTLS()
            }
            if (rec.authenticate(config.string("pop3user"), config.string("pop3password"))) {
                rec.storeNewMails(SQLiteMailstore())
            }
            rec.quit()
        }
        if (current_mail_folder == mailbox_list) {
            for (child in mailbox_list.childrens) {
                if (child.name == "inbox") {
                    current_mail_folder = child
                    break
                }
            }
        }
        update_mail_summary_list(current_mail_folder)
    }

    fun delete_mail(uuid: UUID) {
        SQLiteMailstore().deleteMail(uuid)
        update_mail_summary_list(current_mail_folder)
        showed_mail.from = ""
        showed_mail.to = ""
        showed_mail.subject = ""
        showed_mail.content = ""
        showed_mail.uuid = UUID.randomUUID()
        showed_mail.attachmentList.clear()

    }
}