package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.ui.MailfischMain
import tornadofx.launch


fun main(args : Array<String>) {
    val mailstore = SQLiteMailstore()
    mailstore.initMailStore()

    launch<MailfischMain>(args)
}