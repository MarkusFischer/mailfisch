package eu.markus_fischer.unikram.mailfisch.data

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import tornadofx.getValue
import tornadofx.setValue


class Account(displayname: String? = null,
              emailaddress: String? = null,
              default_username: String? = null,
              default_password: String? = null,
              pop3_server: String? = null,
              pop3_server_port: Int = 0,
              pop3_security: POP3Security? = POP3Security.None,
              pop3_override_credentials: Boolean = false,
              pop3_username: String? = default_username,
              pop3_password: String? = default_password,
              smtp_server: String? = null,
              smtp_server_port: Int = 0,
              smtp_security: SMTPSecurity? = null,
              smtp_override_credentials: Boolean = false,
              smtp_username: String? = default_username,
              smtp_password: String? = default_password) {

    enum class POP3Security {
        None,
        SSL,
        STARTTLS
    }

    enum class SMTPSecurity {
        None,
        SSL,
        STARTTLS
    }

    val displaynameProperty = SimpleStringProperty(this, "displayname", displayname)
    var displayname by displaynameProperty

    val emailaddressProperty = SimpleStringProperty(this, "emailaddress", emailaddress)
    var emailaddress by emailaddressProperty

    val default_usernameProperty = SimpleStringProperty(this, "default_username", default_username)
    var default_username by default_usernameProperty

    val default_passwordProperty = SimpleStringProperty(this, "default_password", default_password)
    var default_password by default_passwordProperty

    val pop3_serverProperty = SimpleStringProperty(this, "pop3_server", pop3_server)
    var pop3_server by pop3_serverProperty

    val pop3_server_portProperty = SimpleIntegerProperty(this, "pop3_server_port", pop3_server_port)
    var pop3_server_port by pop3_server_portProperty

    val pop3_securityProperty = SimpleObjectProperty<POP3Security>(pop3_security)
    var pop3_security by pop3_securityProperty

    val pop3_override_credentialsProperty = SimpleBooleanProperty(this, "pop3_override_credentials", pop3_override_credentials)
    var pop3_override_credentials by pop3_override_credentialsProperty

    val pop3_usernameProperty = SimpleStringProperty(this, "pop3_username", pop3_username)
    var pop3_username by pop3_usernameProperty

    val pop3_passwordProperty = SimpleStringProperty(this, "pop3_password", pop3_password)
    var pop3_password by pop3_passwordProperty

    val smtp_serverProperty = SimpleStringProperty(this, "smtp_server", smtp_server)
    var smtp_server by smtp_serverProperty

    val smtp_server_portProperty = SimpleIntegerProperty(this, "smtp_server_port", smtp_server_port)
    var smtp_server_port by smtp_server_portProperty

    val smtp_securityProperty = SimpleObjectProperty<SMTPSecurity>(smtp_security)
    var smtp_security by smtp_securityProperty

    val smtp_override_credentialsProperty = SimpleBooleanProperty(this, "smtp_override_credentials", smtp_override_credentials)
    var smtp_override_credentials by smtp_override_credentialsProperty

    val smtp_usernameProperty = SimpleStringProperty(this, "smtp_username", smtp_username)
    var smtp_username by smtp_usernameProperty

    val smtp_passwordProperty = SimpleStringProperty(this, "smtp_password", smtp_password)
    var smtp_password by smtp_passwordProperty
}

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













