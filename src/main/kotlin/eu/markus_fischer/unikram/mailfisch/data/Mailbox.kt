package eu.markus_fischer.unikram.mailfisch.data

import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments


class Mailbox (var display_name : String,
               local_part : String,
               domain : String) {

    var local_part : String = local_part
        set(v) {}

    var domain : String = domain
        set(v) {}

    constructor(mailbox_string : String) : this("", "", "") {
        val working_string = removeRFC5322Comments(mailbox_string).replace("\n", "")
        var escape_sequence_beginning = false
        var inside_quote = false
        var split_pos_local_global = -1
        var name_adr = false
        var angel_adr_begin_pos = -1
        for (i in 1..working_string.length - 1) {
            when (working_string[i]) {
                '\"' -> {
                    if (!escape_sequence_beginning)
                        inside_quote = !inside_quote
                    else
                        escape_sequence_beginning = false
                }
                '\\' -> {
                    if (!escape_sequence_beginning)
                        escape_sequence_beginning = true
                    else
                        escape_sequence_beginning = false
                }
                '<' -> {
                    if (!escape_sequence_beginning) {
                        if (!inside_quote) {
                            name_adr = true
                            angel_adr_begin_pos = i
                        }
                    } else {
                        escape_sequence_beginning = false
                    }
                }
                '@' -> {
                    if (!escape_sequence_beginning) {
                        if (!inside_quote) {
                            split_pos_local_global = i
                        }
                    } else {
                        escape_sequence_beginning = false
                    }
                }
                else -> {
                    if (escape_sequence_beginning)
                        escape_sequence_beginning = false
                }
            }
        }
        domain = mailbox_string.substring(split_pos_local_global + 1, mailbox_string.length)
        if (name_adr) {
            display_name = mailbox_string.substring(0, angel_adr_begin_pos + 1)
            local_part = mailbox_string.substring(angel_adr_begin_pos + 1, split_pos_local_global)
        } else {
            local_part = mailbox_string.substring(0, split_pos_local_global)
        }
    }

    private fun isVaildLocalPart(local : Boolean) : Boolean {
        return true
    }

    private fun isValidDomain(domain : Boolean) : Boolean {
        return true
    }

    override fun toString(): String = "$display_name<${getMailAddress()}>"

    fun getMailAddress() : String = "$local_part@$domain"

    override fun hashCode(): Int {
        var result = display_name.hashCode()
        result = 31 * result + local_part.hashCode()
        result = 31 * result + domain.hashCode()
        return result
    }

}

