package eu.markus_fischer.unikram.mailfisch.data

import eu.markus_fischer.unikram.mailfisch.data.addresses.Mailbox
import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments
import java.util.*

class MessageID (lpart : String,
                 gpart : String) {
    var local_part : String = lpart
        private set

    var global_part : String = gpart
        private set

    constructor(msgid : String) : this("", "") {
        val working_string = removeRFC5322Comments(msgid)
        val (lpart, gpart)= removeRFC5322Comments(msgid).substring(1, working_string.length).split('@', limit=2)
        local_part = lpart
        global_part = gpart
    }

    constructor(mailbox : Mailbox) : this(UUID.randomUUID().toString(), mailbox.domain)

    override fun toString(): String = "<$local_part@$global_part>"
}