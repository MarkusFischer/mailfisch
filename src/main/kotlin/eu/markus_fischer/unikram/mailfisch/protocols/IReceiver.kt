package eu.markus_fischer.unikram.mailfisch.protocols

interface IReceiver {
    fun init() : Boolean
    fun isAlive() : Boolean
    fun authenticate(user: String, password: String) : Boolean
    fun getMailCount()
    fun getMail(id: Int)
    fun quit() : Boolean
    fun markMailForDeletion(id: Int)
    fun resetDeletion()
}