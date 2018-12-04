package eu.markus_fischer.unikram.mailfisch.data

enum class ReceiveProtocol(val port: Int) {
    POP3(110),
    POP3S(995),
    IMAP(143),
    IMAPS(993)
}

enum class SendProtocol(val port: Int) {
    SMTP(25),
    SMTPS(465)
}

data class Account(var remote_income_server: String,
                   var remote_income_protocol: ReceiveProtocol,
                   var remote_income_port: Int = remote_income_protocol.port,
                   var use_starttls_income: Boolean = false,
                   var remote_out_server: String,
                   var remote_out_protocol: SendProtocol,
                   var remote_out_port: Int = remote_out_protocol.port,
                   var use_starttls_out: Boolean = false,
                   var mail_adress: String,
                   var user: String = mail_adress,
                   var password: String,
                   var smtp_after_pop : Boolean= true)