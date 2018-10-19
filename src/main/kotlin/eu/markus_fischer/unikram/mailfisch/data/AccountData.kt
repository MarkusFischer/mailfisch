package eu.markus_fischer.unikram.mailfisch.data

enum class ReciveProtocol(val port: Int) {
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
                   var remote_income_port: Int,
                   var remote_income_protocol: ReciveProtocol,
                   var use_starttls_income: Boolean,
                   var mail_adress: String,
                   var password: String)