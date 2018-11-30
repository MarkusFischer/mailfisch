package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.ReceiveProtocol
import eu.markus_fischer.unikram.mailfisch.network.Session
import java.io.*
import java.lang.Exception
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class POP3Receiver(var hostname: String,
                   var port: Int = ReceiveProtocol.POP3S.port,
                   var use_ssl: Boolean = true) : IReceiver {

    private var socket : Socket? = null
    private var use_tls : Boolean = false
    private var connected : Boolean = false

    private var input_stream : BufferedReader? = null
    private var output_stream : PrintStream? = null
    private var mail_marked_for_deletion = false


    override fun connect() : Boolean {
        if (connected) {
            quit()
        } else {
            if (use_ssl) {
                try {
                    socket = SSLSocketFactory.getDefault().createSocket(hostname, port)
                    (socket as SSLSocket).startHandshake()
                    connected = true
                    use_tls = true
                } catch (e : Exception) {
                    e.printStackTrace()
                    connected = false
                }
            } else {
                try {
                    socket = Socket(hostname, port)
                    connected = true
                    use_tls = false
                } catch (e : Exception) {
                    e.printStackTrace()
                    connected = false
                }
            }
        }
        input_stream = BufferedReader(InputStreamReader(socket?.getInputStream()))
        output_stream = PrintStream(socket?.getOutputStream())
        val pop3_status_indicator = input_stream?.readLine()?.substring(0, 4)
        if (pop3_status_indicator == "+OK ") {
            return true
        }
        connected = false
        return false
    }

    private fun sendCommand(command : String, arg1 : String = "", arg2 : String = "", multiple_return_values : Boolean = false) : Pair<Boolean, List<String>>{
        if (connected) {
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
        } else {
            return Pair(false, listOf())
        }
    }

    fun useSTARTTLS() : Boolean {
        if (sendCommand("STLS").first) {
            try {
                socket = (SSLSocketFactory.getDefault() as SSLSocketFactory).createSocket(socket, socket?.getInetAddress()?.getHostAddress(),
                        socket?.getPort() ?: 0, true)
                (socket as SSLSocket).startHandshake()
                BufferedReader(InputStreamReader(socket?.getInputStream()))
                output_stream = PrintStream(socket?.getOutputStream())
                connected = true
                use_tls = true
                return true
            } catch (e : Exception) {
                e.printStackTrace()
                connected = false
            }
        }

        return false
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
            val splitted_status_line = result.second[0].split(' ')
            return Pair(result.first, splitted_status_line[1].toInt())
        } else {
            return Pair(result.first, -1)
        }
    }

    override fun getMail(id: Int) : Pair<Boolean, Mail> {
        val mail = sendCommand("RETR", arg1 = id.toString(), multiple_return_values = true)
        if (mail.first) {
            var result = ""
            for (line in mail.second.subList(1, mail.second.size)) {
                result += line
                result += "\n"
            }
            return Pair(true, Mail(result))
        } else {
            return Pair(false, Mail())
        }
    }

    override fun quit() : Boolean {
        if (connected && sendCommand("QUIT").first) {
            mail_marked_for_deletion = false
            connected = false
            //TODO work around to fix hanging quit when quit was called twice
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