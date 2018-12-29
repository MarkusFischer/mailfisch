package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver
import eu.markus_fischer.unikram.mailfisch.ui.MailfischMain
import tornadofx.launch

fun main(args : Array<String>) {
    val mailstore = SQLiteMailstore()
    mailstore.initMailStore()

    val rec = POP3Receiver(hostname = "pop.mail.de",
                           port = 995,
                            use_ssl = true)
    if (rec.connect()) {
        if (rec.authenticate("mf.dev", "GanzSicheresPasswort")) {
            rec.storeNewMails(mailstore)
        }
        rec.quit()
    }
    launch<MailfischMain>(args)
}