package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.Mail

interface IReceiver {
    fun connect() : Boolean
    fun isAlive() : Boolean
    fun authenticate(user: String, password: String) : Boolean
    fun getMailCount() : Pair<Boolean, Int>
    fun getMail(id: Int) : Pair<Boolean, Mail>
    fun quit() : Boolean
    fun markMailForDeletion(id: Int) : Boolean
    fun resetDeletion() : Boolean
}