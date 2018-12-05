package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.ReceiveProtocol
import eu.markus_fischer.unikram.mailfisch.data.SendProtocol
import eu.markus_fischer.unikram.mailfisch.data.addresses.Mailbox
import eu.markus_fischer.unikram.mailfisch.network.SSLSession
import eu.markus_fischer.unikram.mailfisch.network.Session
import eu.markus_fischer.unikram.mailfisch.protocols.IReceiver
import eu.markus_fischer.unikram.mailfisch.protocols.ISender
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver
import eu.markus_fischer.unikram.mailfisch.protocols.SMTPSender
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/*
fun main(args: Array<String>) {
    var running = true
    while (running) {
        print("Insert: ")
        val input = readLine()!!
        if (input == "q") {
            running = false
        } else {
            println("In: $input out: ${Mailbox(input).toString()}")
        }
    }
}
*/
fun main(args : Array<String>) {
    /*println("mailfisch v1")
    println("simple pop3 client")
    print("Please enter the remote hostname: ")
    val hostname = readLine()!!
    print("Please enter the remote port: ")
    val port = readLine()!!.toInt()
    print("Please enter your username: ")
    val user = readLine()!!
    print("Please enter your passwort: ")
    val password = readLine()!!
    print("Use ssl? (y) or (n): ")
    val use_ssl = readLine()!!
    val test_account = Account(remote_income_server = hostname,
                                remote_income_protocol = if (use_ssl == "y") ReceiveProtocol.POP3S else ReceiveProtocol.POP3,
                                remote_income_port = port,
                                mail_adress = "mf.dev@mail.de",
                                user = user,
                                password = password)
    println("Create session")
    val income_session = if (test_account.remote_income_protocol == ReceiveProtocol.POP3S)
                                SSLSession(test_account.remote_income_server, test_account.remote_income_port) else
                                Session(test_account.remote_income_server, test_account.remote_income_port)

    println("Connecting... ")*/
    println("mailfisch v2")
    println("simple smtp client")
    print("Please enter the remote hostname: ")
    val hostname = readLine()!!
    print("Please enter the remote port: ")
    val port = readLine()!!.toInt()
    print("Please enter your username: ")
    val user = readLine()!!
    print("Please enter your passwort: ")
    val password = readLine()!!
    print("Use ssl (ssl) starttls (starttls) or nothing (n)?: ")
    val use_ssl = readLine()!!
    print("Please enter your mail address: ")
    val mail_addr = readLine()!!
    val test_account = Account(remote_income_server = "pop.mail.de",
                                remote_income_protocol = ReceiveProtocol.POP3S,
                                remote_income_port = 995,
                                remote_out_server = hostname,
                                remote_out_port = port,
                                remote_out_protocol = if (use_ssl == "ssl") SendProtocol.SMTPS else SendProtocol.SMTP,
                                use_starttls_out = if (use_ssl == "starttls") true else false,
                                mail_adress = mail_addr,
                                user = user,
                                password = password)
    /*val test_account = Account(remote_income_server = "pop.mail.de",
            remote_income_protocol = ReceiveProtocol.POP3S,
            remote_income_port = 995,
            remote_out_server = "server.inc.li",
            remote_out_port = 587,
            remote_out_protocol = SendProtocol.SMTP,
            use_starttls_out = true,
            mail_adress = "igel@inc.li",
            user = "igel@inc.li",
            password = "j9rbb3gFCc")*/
    val sender : ISender = SMTPSender(hostname = test_account.remote_out_server,
                                        port = test_account.remote_out_port,
                                        use_ssl = test_account.remote_out_protocol == SendProtocol.SMTPS,
                                        use_starttls =  test_account.use_starttls_out)
    println("Try to connect...")
    if (sender.connect()) {
        println("Connected!")
        println("Init...")
        if (sender.init()) {
            println("Init worked!")
            println("Supported Features: ${sender.getSupportedFeatures()}")
            println("Authenticate...")
            if (sender.authenticate(test_account.user, test_account.password)) {
                var running = true
                while (running) {
                    println("Which action do you want to perform?")
                    print("Enter (s)end or (q)uit: ")
                    val action = readLine()!!
                    when (action) {
                        "q" -> {
                            running = false
                            sender.quit()
                        }
                        "s" -> {
                            print("Subject: ")
                            var subject = readLine()!!
                            print("To (comma seperated): ")
                            var to = readLine()!!
                            var content = ""
                            println("Content (finish with \\n.\\n): ")
                            var lines_read = 0
                            var run_read = true
                            while (run_read) {
                                var line = readLine()
                                if (line == ".") {
                                    run_read = false
                                } else {
                                    content += "$line\n"
                                    lines_read++
                                }
                            }
                            if (lines_read >= 1) {
                                var mail = Mail()
                                mail.raw_content = content
                                mail.addHeader("subject", subject)
                                mail.addHeader("to", to)
                                mail.addHeader("from", test_account.mail_adress)
                                println("Sending...")
                                sender.sendMail(mail)
                            } else {
                                println("Please enter more then one line content!")
                            }
                        }
                    }
                }
            }
        }
        println("Quit...")
        sender.quit()
    }
}