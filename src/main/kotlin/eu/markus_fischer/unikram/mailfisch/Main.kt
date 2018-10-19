package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.ReciveProtocol

fun main(args : Array<String>) {
    print("mailfisch v1")
    val test_account: Account = Account(remote_income_server = "localhost",
                                        remote_income_protocol = ReciveProtocol.POP3,
                                        mail_adress = "mf.dev@mail.de",
                                        password = "GanzSicheresPasswort")
    print("Recive mails from" + test_account)
}