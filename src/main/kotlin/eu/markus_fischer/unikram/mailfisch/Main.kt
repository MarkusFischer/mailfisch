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
    val test_account = Account(remote_income_server = "pop.mail.de",
                                remote_income_protocol = ReceiveProtocol.POP3S,
                                remote_income_port = 995,
                                remote_out_server = "smtp.mail.de",
                                remote_out_port = 587,
                                remote_out_protocol = SendProtocol.SMTP,
                                use_starttls_out = true,
                                mail_adress = "mf.dev@mail.de",
                                user = "mf.dev",
                                password = "GanzSicheresPasswort")
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
                                        use_ssl = false)
    println("Try to connect...")
    val testmail : Mail = Mail()
    testmail.raw_content="Das ist eine tolle Testmail!"
    testmail.addHeader("from", test_account.mail_adress)
    testmail.addHeader("to", "ich@markus-fischer.eu")
    testmail.addHeader("cc", "markus.fischer@uni-jena.de")
    testmail.addHeader("bcc", "foto@markus-fischer.eu")
    testmail.addHeader("subject", "Testmail")
    //testmail.addHeader("date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()))
    if (sender.connect()) {
        println("Connected!")
        println("Init...")
        if (sender.init()) {
            println("Init worked!")
            println("Supported Features: ${sender.getSupportedFeatures()}")
            println("Authenticate...")
            sender.authenticate(test_account.user, test_account.password)
            println("Send mail...")
            sender.sendMail(testmail)
        }
        println("Quit...")
        sender.quit()
    }
}