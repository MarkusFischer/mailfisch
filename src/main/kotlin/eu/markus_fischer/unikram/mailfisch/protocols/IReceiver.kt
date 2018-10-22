package eu.markus_fischer.unikram.mailfisch.protocols

interface IReceiver {
    fun init() : Boolean
    fun isAlive() : Boolean
    fun authenticate(user: String, password: String) : Boolean
    fun getMailCount() : Pair<Boolean, Int>
    fun getMail(id: Int) : Pair<Boolean, String> //TODO replace String with mail datatype
    fun quit() : Boolean
    fun markMailForDeletion(id: Int) : Boolean
    fun resetDeletion() : Boolean
}