package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.ReceiveProtocol
import eu.markus_fischer.unikram.mailfisch.network.Session
import eu.markus_fischer.unikram.mailfisch.protocols.IReceiver
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver

fun main(args : Array<String>) {
    println("mailfisch v1")
    val test_account = Account(remote_income_server = "localhost",
                                remote_income_protocol = ReceiveProtocol.POP3,
                                mail_adress = "mf.dev@mail.de",
                                user = "mf.dev",
                                password = "GanzSicheresPasswort")
    println("Recive mails from" + test_account)
    val income_session = Session(test_account.remote_income_server, test_account.remote_income_port)
    println("Connecting... ")
    val success = income_session.establish_connection()
    println("Success: " + success)
    if (success) {
        val receiver : IReceiver = POP3Receiver(session = income_session)
        if (receiver.init()) {
            println("Successfull init")
            if (receiver.authenticate(test_account.user, test_account.password)) {
                println("Successfully authenticated!")
                val mail = receiver.getMail(46).second
                println("Mail 46: $mail")
                println("Headers in mail 46: ${mail.getAllHeaderKeys()}")
            } else {
                println("Something went wrong during authentication")
            }
            receiver.quit()
        }
    }
}