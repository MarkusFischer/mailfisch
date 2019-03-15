package eu.markus_fischer.unikram.mailfisch.ui.views

import eu.markus_fischer.unikram.mailfisch.data.Account
import eu.markus_fischer.unikram.mailfisch.data.AccountModel
import tornadofx.*

class BasicData : View("Basic Data") {
    val account: AccountModel by inject()

    override val root = form {
        fieldset() {
            field("Displayname") {
                textfield(account.displayname).required()
            }
            field("E-Mailaddress") {
                textfield(account.emailaddress).required()
            }
            field("Username") {
                textfield(account.default_username).required()
            }
            field("Password") {
                passwordfield(account.default_password).required()
            }
        }
    }

    override val complete = account.valid(account.displayname,
                                                          account.emailaddress,
                                                          account.default_username,
                                                          account.default_password)

    override val configPath = app.configBasePath.resolve("app.config")

    override fun onSave() {
        with(config) {
            set("displayname", account.displayname.value)
            set("address", account.emailaddress.value)
            set("user", account.default_username.value)
            set("password", account.default_password.value)
            save()
        }
    }

    init {
        if (!config.isEmpty) {
            account.displayname.value = config.string("displayname") ?: ""
            account.emailaddress.value = config.string("address") ?: ""
            account.default_username.value = config.string("user") ?: ""
            account.default_password.value = config.string("password") ?: ""
        }
    }

}

class POP3Data : View("POP3 Data") {
    val account: AccountModel by inject()

    override val root = form {
        fieldset("Serverdata") {
            field("Server") {
                textfield(account.pop3_server).required()
            }
            field("Port") {
                textfield(account.pop3_server_port) {
                    filterInput { it.controlNewText.isInt() }
                    required()
                }
            }
            field("Security") {
                account.pop3_security.value = Account.POP3Security.None
                combobox(account.pop3_security, Account.POP3Security.values().toList()).required()
            }

            field() {
                checkbox("Override Credentials?", account.pop3_override_credentials) {
                    action {
                        if (isSelected) {
                            account.pop3_username.value = account.default_username.value
                            account.pop3_password.value = account.default_password.value
                        }
                    }
                }
            }
            field("Username") {
                visibleWhen { account.pop3_override_credentials }
                textfield(account.pop3_username){
                    required()
                }
            }
            field("Password") {
                visibleWhen { account.pop3_override_credentials }
                passwordfield(account.pop3_password).required()
            }
        }
    }

    override val complete = account.valid(account.pop3_server,
            account.pop3_server_port,
            account.pop3_security)

    override val configPath = app.configBasePath.resolve("app.config")

    override fun onSave() {
        with(config) {
            set("pop3server", account.pop3_server.value)
            set("pop3port", account.pop3_server_port.value.toString())
            if (account.pop3_override_credentials.value) {
                set("pop3user", account.pop3_username.value)
                set("pop3password", account.pop3_password.value)
            } else {
                set("pop3user", account.default_username.value)
                set("pop3password", account.default_password.value)
            }
            set("pop3security", account.pop3_security.value.toString())
            save()
        }
    }

    init {
        if (!config.isEmpty) {
            account.pop3_server.value = config.string("pop3server") ?: ""
            account.pop3_server_port.value = config.int("pop3port") ?: 0
            account.pop3_username.value = config.string("pop3user") ?: ""
            account.pop3_password.value = config.string("pop3password") ?: ""

            when (config.string("pop3security")) {
                "SSL" -> account.pop3_security.value = Account.POP3Security.SSL
                "STARTTLS" -> account.pop3_security.value = Account.POP3Security.STARTTLS
                else -> account.pop3_security.value = Account.POP3Security.None
            }
        }
    }

}

class SMTPData : View("SMTP Data") {
    val account: AccountModel by inject()

    override val root = form {
        fieldset("Serverdata") {
            field("Server") {
                textfield(account.smtp_server).required()
            }
            field("Port") {
                textfield(account.smtp_server_port) {
                    filterInput { it.controlNewText.isInt() }
                    required()
                }
            }
            field("Security") {
                account.smtp_security.value = Account.SMTPSecurity.None
                combobox(account.smtp_security, Account.SMTPSecurity.values().toList()).required()
            }
            field() {
                checkbox("Override Credentials?", account.smtp_override_credentials) {
                    action {
                        if (isSelected) {
                            account.smtp_username.value = account.default_username.value
                            account.smtp_password.value = account.default_password.value
                        }
                    }
                }
            }
            field("Username") {
                visibleWhen { account.smtp_override_credentials }
                textfield(account.smtp_username).required()
            }
            field("Password") {
                visibleWhen { account.smtp_override_credentials }
                passwordfield(account.smtp_password).required()
            }
        }
    }

    override val complete = account.valid(account.smtp_server,
            account.smtp_server_port,
            account.smtp_security)

    override val configPath = app.configBasePath.resolve("app.config")

    override fun onSave() {
        with(config) {
            set("smtpserver", account.smtp_server.value)
            set("smtpport", account.smtp_server_port.value.toString())
            set("smtpsecurity", account.smtp_security.value.toString())
            if (account.smtp_override_credentials.value) {
                set("smtpuser", account.smtp_username.value)
                set("smtppassword", account.smtp_password.value)
            } else {
                set("smtpuser", account.default_username.value)
                set("smtppassword", account.default_password.value)
            }
            save()
        }
    }

    init {
        if (!config.isEmpty) {
            account.smtp_server.value = config.string("smtpserver") ?: ""
            account.smtp_server_port.value = config.int("smtpport") ?: 0
            account.smtp_username.value = config.string("smtpuser") ?: ""
            account.smtp_password.value = config.string("smtppassword") ?: ""

            when (config.string("smtpsecurity")) {
                "SSL" -> account.smtp_security.value = Account.SMTPSecurity.SSL
                "STARTTLS" -> account.smtp_security.value = Account.SMTPSecurity.STARTTLS
                else -> account.smtp_security.value = Account.SMTPSecurity.None
            }
        }
    }
}

class AccountCreateWizard : Wizard("Accountwizard") {
    val account: AccountModel by inject()

    override val canFinish = allPagesComplete
    override val canGoNext = currentPageComplete


    init {
        add(BasicData::class)
        add(POP3Data::class)
        add(SMTPData::class)
    }

}
