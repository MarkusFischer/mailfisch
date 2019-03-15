package eu.markus_fischer.unikram.mailfisch.ui.controllers

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.protocols.SMTPSender
import tornadofx.*

class NewMailController : Controller() {
    override val configPath = app.configBasePath.resolve("app.config")
    fun sendMail(mail: Mail) {
        val security_method = when (config.string("smtpsecurity")) {
            "SSL" -> Account.SMTPSecurity.SSL
            "STARTTLS" -> Account.SMTPSecurity.STARTTLS
            else -> Account.SMTPSecurity.None
        }
        val transmitter = SMTPSender(hostname = config.string("smtpserver"),
                                     port = config.int("smtpport") ?: 0,
                                     use_ssl = security_method == Account.SMTPSecurity.SSL,
                                     use_starttls = security_method == Account.SMTPSecurity.STARTTLS)
        print(mail)
        print("Connecting...")
        transmitter.connect()
        print("Authenticate...")
        transmitter.init()
        transmitter.authenticate(config.string("smtpuser"), config.string("smtppassword"))
        print("Sending...")
        transmitter.sendMail(mail)
        SQLiteMailstore().storeMail(mail, mailbox = "sent")
    }

    fun saveMail(mail: Mail) {
        mail.prepareToSave()
        SQLiteMailstore().storeMail(mail, mailbox = "draft")
    }

}