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
