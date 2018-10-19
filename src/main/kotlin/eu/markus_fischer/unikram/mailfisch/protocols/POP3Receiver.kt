package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.network.Session
import java.io.*

class POP3Receiver (val session: Session) : IReceiver {

    private var input_stream : BufferedReader? = null
    private var output_stream : PrintStream? = null

    private enum class Status{
        OK,
        ERR
    }

    override fun init() : Boolean {
        input_stream = BufferedReader(InputStreamReader(session.socket?.getInputStream()))
        output_stream = PrintStream(session.socket?.getOutputStream())
        val pop3_status_indicator = input_stream?.readLine()?.substring(0, 4)
        if (pop3_status_indicator == "+OK ") {
            return true
        }
        return false
    }

    private fun sendCommand(command : String, arg1 : String = "", arg2 : String = "", multiple_return_values : Boolean = false) : Pair<Boolean, List<String>>{
        output_stream?.println(if (arg2 == "") "$command $arg1" else "$command $arg1 $arg2")
        val pop3_status : MutableList<String> = mutableListOf()
        val status = input_stream?.readLine().toString()
        pop3_status.add(status)
        var success = false
        if (status.substring(0, 3) == "+OK") {
            success = true
            if (multiple_return_values) {
                var last = ""
                while (last != ".") {
                    last = input_stream?.readLine().toString()
                    pop3_status.add(last)
                }
            }
        }
        return Pair(success, pop3_status.toList())
    }

    override fun isAlive(): Boolean {
        return sendCommand("NOOP").first
    }

    override fun authenticate(user: String, password: String) : Boolean {
        if (sendCommand("USER", arg1=user).first) {
            return sendCommand("PASS", arg1=password).first
        } else {
            return false
        }
    }

    override fun getMailCount() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMail(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun quit() : Boolean{
        return sendCommand("QUIT").first
    }

    override fun markMailForDeletion(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun resetDeletion() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}