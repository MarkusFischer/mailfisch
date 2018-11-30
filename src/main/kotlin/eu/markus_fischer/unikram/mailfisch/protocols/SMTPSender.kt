package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.SendProtocol
import eu.markus_fischer.unikram.mailfisch.data.addresses.Address
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.lang.Exception
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class SMTPSender(var hostname: String,
                 var port: Int = SendProtocol.SMTPS.port,
                 var use_ssl: Boolean = true) : ISender {

    private var socket : Socket? = null
    private var use_tls : Boolean = false
    private var connected : Boolean = false

    private var input_stream : BufferedReader? = null
    private var output_stream : PrintStream? = null

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
        val pop3_status_indicator = input_stream?.readLine()?.substring(0, 3)
        when(pop3_status_indicator) {
            "220" -> return true
            "554" -> return false //todo do something
            else -> return false
        }
    }

    private fun sendCommand(command : String, arg1 : String = "") : Pair<Int, List<String>> = Pair(42, listOf())

    override fun isAlive(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authenticate(user: String, password: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendMail(mail: Mail): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun sendMail(from: Address, to: Address, raw_mail: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun quit(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSupportedFeatures(): List<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}