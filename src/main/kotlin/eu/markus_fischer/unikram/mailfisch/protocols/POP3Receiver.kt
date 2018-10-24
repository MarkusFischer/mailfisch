package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.network.Session
import java.io.*

class POP3Receiver (val session: Session) : IReceiver {

    private var input_stream : BufferedReader? = null
    private var output_stream : PrintStream? = null
    private var mail_marked_for_deletion = false

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

    override fun isAlive(): Boolean = sendCommand("NOOP").first

    override fun authenticate(user: String, password: String) : Boolean {
        if (sendCommand("USER", arg1=user).first) {
            return sendCommand("PASS", arg1=password).first
        } else {
            return false
        }
    }

    override fun getMailCount() : Pair<Boolean, Int> {
        val result = sendCommand("LIST", multiple_return_values = true)
        if (result.first) {
            val splitted_status_line = result.second[1].split(' ')
            return Pair(result.first, splitted_status_line[1].toInt())
        } else {
            return Pair(result.first, -1)
        }
    }

    override fun getMail(id: Int) : Pair<Boolean, String> {
        val mail = sendCommand("RETR", arg1 = id.toString(), multiple_return_values = true)
        if (mail.first) {
            var result = ""
            for (line in mail.second.subList(1, mail.second.size)) {
                result += line
                result += "\n"
            }
            return Pair(true, result)
        } else {
            return Pair(false, "")
        }
    }

    override fun quit() : Boolean {
        if (sendCommand("QUIT").first) {
            mail_marked_for_deletion = false
            return true
        } else {
            return false
        }
    }

    override fun markMailForDeletion(id: Int) : Boolean {
        if (sendCommand("DELE", arg1 = id.toString()).first) {
            mail_marked_for_deletion = true
            return true
        } else {
            return false
        }
    }

    override fun resetDeletion() : Boolean {
        if (sendCommand("RSET").first) {
            mail_marked_for_deletion = false
            return true
        } else {
            return false
        }
    }
}