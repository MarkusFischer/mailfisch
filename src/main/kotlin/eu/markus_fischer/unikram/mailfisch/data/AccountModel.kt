package eu.markus_fischer.unikram.mailfisch.data

import tornadofx.*

class AccountModel : ItemViewModel<Account>() {
    val displayname = bind(Account::displaynameProperty, autocommit = true)
    val emailaddress = bind(Account::emailaddressProperty, autocommit=true)
    val default_username = bind(Account::default_usernameProperty, autocommit=true)
    val default_password = bind(Account::default_passwordProperty, autocommit=true)
    val pop3_server = bind(Account::pop3_serverProperty, autocommit=true)
    val pop3_server_port = bind(Account::pop3_server_portProperty, autocommit=true)
    val pop3_security = bind(Account::pop3_securityProperty, autocommit=true)
    val pop3_override_credentials = bind(Account::pop3_override_credentialsProperty, autocommit=true)
    val pop3_username = bind(Account::pop3_usernameProperty, autocommit=true)
    val pop3_password = bind(Account::pop3_passwordProperty, autocommit=true)
    val smtp_server = bind(Account::smtp_serverProperty, autocommit=true)
    val smtp_server_port = bind(Account::smtp_server_portProperty, autocommit=true)
    val smtp_security = bind(Account::smtp_securityProperty, autocommit=true)
    val smtp_override_credentials = bind(Account::smtp_override_credentialsProperty, autocommit=true)
    val smtp_username = bind(Account::smtp_usernameProperty, autocommit=true)
    val smtp_password = bind(Account::smtp_passwordProperty, autocommit=true)
}