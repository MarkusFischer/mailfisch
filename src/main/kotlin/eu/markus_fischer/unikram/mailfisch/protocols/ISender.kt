package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.addresses.Address

interface ISender {
    fun connect() : Boolean
    fun isAlive() : Boolean
    fun authenticate(user: String, password: String) : Boolean
    fun sendMail(mail : Mail) : Boolean
    fun sendMail(from : Address, to : Address, raw_mail : String)
    fun quit() : Boolean
    fun getSupportedFeatures() : List<String>
}