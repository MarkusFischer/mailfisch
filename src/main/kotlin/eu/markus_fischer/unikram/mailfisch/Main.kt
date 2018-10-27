package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.ReceiveProtocol
import eu.markus_fischer.unikram.mailfisch.network.Session
import eu.markus_fischer.unikram.mailfisch.protocols.IReceiver
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver

fun main(args : Array<String>) {
    println("mailfisch v1")
    println("simple pop3 client")
    print("Please enter the remote hostname: ")
    val hostname = readLine()!!
    print("Please enter the remote port: ")
    val port = readLine()!!.toInt()
    print("Please enter your username: ")
    val user = readLine()!!
    print("Please enter your passwort: ")
    val password = readLine()!!
    val test_account = Account(remote_income_server = hostname,
                                remote_income_protocol = ReceiveProtocol.POP3,
                                remote_income_port = port,
                                mail_adress = "mf.dev@mail.de",
                                user = user,
                                password = password)
    println("Create session")
    val income_session = Session(test_account.remote_income_server, test_account.remote_income_port)
    println("Connecting... ")
    val success = income_session.establish_connection()
    println("Connected? $success")
    if (success) {
        println("Init POP3Receiver...")
        val receiver : IReceiver = POP3Receiver(session = income_session)
        if (receiver.init()) {
            println("Init was successfull!")
            println("Try to authenticate on the remote server...")
            if (receiver.authenticate(test_account.user, test_account.password)) {
                println("Successfully authenticated!")
                var active = true
                while (active) {
                    println("Which action do you want to perform?")
                    print("Enter (l)ist, (r)etrive or (q)uit: ")
                    val action = readLine()!!
                    when (action) {
                        "l" -> {
                            val (suc, count) = receiver.getMailCount()
                            if (suc) {
                                println("There are $count Mails in your inbox:")
                                for (i in 1..count) {
                                    println("$i) Subject: ${receiver.getMail(i).second.getHeader("Subject").value}")
                                }
                            }
                        }
                        "r" -> {
                            print("Which mail do you want to read? ")
                            val mail_id = readLine()!!.toInt()
                            val (suc, mail) = receiver.getMail(mail_id)
                            if (suc) {
                                println("From: ${mail.getHeader("From").value}")
                                println("To: ${mail.getHeader("To").value}")
                                println("Date: ${mail.getHeader("Date").value}")
                                println("Subject: ${mail.getHeader("Subject").value}\n")
                                println("Content: ${mail.raw_content}")
                            } else {
                                println("Could not receive mail $mail_id. Maybe this mail doesn't exist?")
                            }
                        }
                        "q" -> {
                            println("Quiting...")
                            receiver.quit()
                            active = false
                        }
                    }
                }
            } else {
                println("Something went wrong during authentication!")
                println("Maybe you entered the wrong user/password combination?")
            }
            println("Closing receiver...")
            receiver.quit()
            println("Closing session...")
            income_session.close_connection()
        }
    }
}