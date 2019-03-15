package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.addresses.Mailbox

interface ISender {
    fun connect() : Boolean
    fun init() : Boolean
    fun isAlive() : Boolean
    fun authenticate(user: String, password: String) : Boolean
    fun sendMail(mail : Mail) : Boolean
    fun sendMail(from : Mailbox, to : List<Mailbox>, raw_mail : String)
    fun quit()
    fun getSupportedFeatures() : List<String>
}