package eu.markus_fischer.unikram.mailfisch.protocols

import eu.markus_fischer.unikram.mailfisch.data.*
import eu.markus_fischer.unikram.mailfisch.data.addresses.Address
import eu.markus_fischer.unikram.mailfisch.data.addresses.Mailbox
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.lang.Exception
import java.lang.RuntimeException
import java.net.InetAddress
import java.net.Socket
import java.util.*
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class SMTPSender(var hostname: String,
                 var port: Int = SendProtocol.SMTP.port,
                 var use_ssl: Boolean = false,
                 var use_starttls : Boolean = true,
                 var strict_starttls : Boolean = true,
                 var bcc_as_single_mail : Boolean = false) : ISender {

    private var socket : Socket? = null
    private var use_tls : Boolean = false
    private var connected : Boolean = false

    private var input_stream : BufferedReader? = null
    private var output_stream : PrintStream? = null
    var supported_features : MutableList<String> = mutableListOf()
        private set
    private var esmtp = true

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

    private fun sendCommand(command : String, arg1 : String = "") : Pair<Int, List<String>>{
        if (connected) {
            var cmd = command
            var arg = arg1
            if (arg1 == "") {
                if (command.last() != '\n') {
                    cmd += "\r\n"
                }
            } else if (arg1.last() != '\n') {
                arg += "\r\n"
            }
            output_stream?.print(if (arg1 == "") cmd else "$cmd $arg")
            val command_output : MutableList<String> = mutableListOf()
            var smtp_return = input_stream?.readLine().toString()
            val status = smtp_return.substring(0, 3).toInt()
            command_output.add(smtp_return)
            while (smtp_return[3] == '-') {
                smtp_return = input_stream?.readLine().toString()
                command_output.add(smtp_return)
            }
            return Pair(status, command_output.toList())
        }
        return Pair(-1, listOf())
    }

    override fun init() : Boolean {
        var (status, lines) = sendCommand("EHLO", InetAddress.getLocalHost().getHostName())
        if (status == 502) {
            esmtp = false
            val (s, l) = sendCommand("HELO", InetAddress.getLocalHost().getHostName())
            status = s
            lines = l
        }
        when(status) {
            250 -> {
                    if (lines.size > 1) {
                        for (line in lines.subList(1, lines.lastIndex + 1)) {
                            val feature = line.substring(4)
                            supported_features.add(feature.toLowerCase())
                        }
                    }
            }
            504, 550, 501 -> {
                return false
            }
            else -> {
                throw RuntimeException("Unexpected return code!")
            }
        }
        if (use_starttls) {
            if (esmtp && supported_features.contains("starttls")) {
                startTLS()
                var (s, l) = sendCommand("EHLO", InetAddress.getLocalHost().getHostName())
                when (s) {
                    250 -> {
                        supported_features = mutableListOf()
                        if (l.size > 1) {
                            for (line in l.subList(1, l.lastIndex + 1)) {
                                val feature = line.substring(4)
                                supported_features.add(feature.toLowerCase())
                            }
                        }
                        return true
                    }
                    504, 550, 501, 502 -> {
                        return false
                    }
                    else -> {
                        throw RuntimeException("Unexpected return code!")
                    }
                }
            } else if (strict_starttls) {
                throw RuntimeException("Remote server doesn't support starttls but strict starttls was given!")
            }
        }
        return true
    }

    override fun isAlive(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun authenticate(user: String, password: String): Boolean {
        //TODO better authentication method regnotion
        var login = false
        var auth = false
        for (feature in supported_features) {
            if (feature.contains("auth", true)) {
                auth = true
                //TODO implement login
                //login = feature.contains("login", true)
            }
        }
        if (auth) {
            if (login) {
                return false
            } else {
                //PLAIN must be supported
                //TODO do something with other status codes
                var plain_login = user+'\u0000'+user+'\u0000'+password
                when(sendCommand("auth plain", Base64.getEncoder().encodeToString(plain_login.toByteArray())).first) {
                    235 -> return true
                    432 -> return false //missing password
                    454 -> return false //temporary authentication failed
                    534 -> return false //to weak auth method
                    535 -> return false //authentication credentials invalid
                    500 -> return false //authentication exchange line too long
                    538 -> return false //encryption required
                }
                return true
            }
        } else {
            //SMTP after POP
            //TODO implement SMTP after POP
            return false
        }
    }

    private fun startTLS() {
        val (status, data) = sendCommand("STARTTLS")
        println("$status $data")
        if (status == 220) {
            try {
                socket = (SSLSocketFactory.getDefault() as SSLSocketFactory).createSocket(socket, socket?.getInetAddress()?.getHostAddress(),
                        socket?.getPort() ?: 0, true)
                (socket as SSLSocket).useClientMode = true
                (socket as SSLSocket).startHandshake()
                input_stream = BufferedReader(InputStreamReader(socket?.getInputStream()))
                output_stream = PrintStream(socket?.getOutputStream())
                connected = true
                use_tls = true
            } catch (e : Exception) {
                e.printStackTrace()
                connected = false
            }
        }
    }

    override fun sendMail(mail: Mail): Boolean {
        if (!mail.hasHeader("from") || !mail.hasHeader("to")) {
            return false //no vaild mail
        }
        if ((((mail.getHeader("from").value as HeaderValueAddressList).address_list.size > 1) ||
            ((mail.getHeader("from").value as HeaderValueAddressList).address_list[0].getMailboxes().size > 1)) &&
            !(mail.hasHeader("sender"))) {
            return false //multiple from but no sender given
        }
        val smtp_from = if (mail.hasHeader("sender")) (mail.getHeader("sender") as HeaderValueAddressList).address_list[0].getMailboxes()[0]
                                else (mail.getHeader("from").value as HeaderValueAddressList).address_list[0].mailbox
        val smtp_rcpt_to : MutableList<Mailbox> = mutableListOf()
        val smtp_rcpt_to_bcc : MutableList<Mailbox> = mutableListOf()
        for (address in (mail.getHeader("to").value as HeaderValueAddressList).address_list) {
            for (mailbox in address.getMailboxes()) {
                smtp_rcpt_to.add(mailbox)
            }
        }
        if (mail.hasHeader("cc")) {
            for (address in (mail.getHeader("cc").value as HeaderValueAddressList).address_list) {
                for (mailbox in address.getMailboxes()) {
                    smtp_rcpt_to.add(mailbox)
                }
            }
        }
        if (mail.hasHeader("bcc")) {
            for (address in (mail.getHeader("bcc").value as HeaderValueAddressList).address_list) {
                for (mailbox in address.getMailboxes()) {
                    smtp_rcpt_to_bcc.add(mailbox)
                }
            }
        }
        if (!mail.hasHeader("date")) {
            mail.addHeader("date", HeaderValueDate())
        }
        if (!mail.hasHeader("message-id")) {
            mail.addHeader("message-id", HeaderValueMessageIdList(mutableListOf(MessageID(smtp_from)), true))
        }
        if (!bcc_as_single_mail) {
            sendMail(smtp_from, smtp_rcpt_to.union(smtp_rcpt_to_bcc).toList(), mail.prepareToSend())
        } else {
            val prepared_mail = mail.prepareToSend()
            sendMail(smtp_from, smtp_rcpt_to, prepared_mail)
            for (mailbox in smtp_rcpt_to_bcc) {
                sendMail(smtp_from, listOf(mailbox), prepared_mail)
            }
        }
        return true
    }

    override fun sendMail(from : Mailbox, to : List<Mailbox>, raw_mail : String) {
        if (to.isEmpty() || raw_mail.isBlank()) {
            throw IllegalArgumentException("Parameters shouldn't be empty")
        }
        var (status, lines) = sendCommand("MAIL", "FROM:<${from.getMailAddress()}>")
        if (status == 250) {
            for (mailbox in to) {
                var (stat, lin) = sendCommand("RCPT", "TO:<${mailbox.getMailAddress()}>")
                var error =when (stat) {
                    250, 251 -> false
                    //TODO parse other error codes
                    /*552, 554, 451, 452, 503 -> true //storage or general related errors
                    450, 550 -> true //command rejected for policy reasons*/
                    else -> true
                }
                if (error) {
                    throw RuntimeException(lin.toString())
                }
            }
            var (stat, line) = sendCommand("DATA")
            if (stat == 354) {
                var (s, l) = sendCommand("$raw_mail\r\n.\r\n")
                if (s != 250) {
                    throw RuntimeException("Something went wrong during data transmission. Exception given from Server: ${l.toString()}")
                }
            } else {
                throw RuntimeException("Something went wrong during data transmission. Exception given from Server: ${lines.toString()}")
            }
        } else {
            throw RuntimeException(lines.toString())
        }
    }

    override fun quit() {
        val (status, lines) = sendCommand("QUIT")
        if (status != 221) {
            throw RuntimeException("Fatal! Other SMTP Status code after QUIT!")
        } else {
            socket?.close()
            connected = false
        }
    }

    override fun getSupportedFeatures(): List<String> = supported_features.toList()
}