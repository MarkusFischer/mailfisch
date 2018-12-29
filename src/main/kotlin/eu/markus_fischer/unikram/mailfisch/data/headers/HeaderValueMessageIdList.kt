package eu.markus_fischer.unikram.mailfisch.data.headers

import eu.markus_fischer.unikram.mailfisch.data.MessageID
import eu.markus_fischer.unikram.mailfisch.getCharPositions
import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments

//TODO make sure, list contains only one Type
class HeaderValueMessageIdList(var msgid_list : MutableList<MessageID>, val single_id : Boolean = false) : HeaderValue {

    constructor(id_list_string : String, single_id: Boolean = false) : this(mutableListOf(), single_id) {
        val working_address_list_string = removeRFC5322Comments(id_list_string)
        if (single_id) {
            msgid_list.add(MessageID(working_address_list_string))
        } else {
            val charPositions = getCharPositions(working_address_list_string, ',', true, true, true)
            if (charPositions.isNotEmpty()) {
                var offset = 0
                for (pos in charPositions) {
                    msgid_list.add(MessageID(working_address_list_string.substring(offset, pos).trim()))
                    offset = pos + 1
                }
                msgid_list.add(MessageID(working_address_list_string.substring(offset).trim()))
            } else {
                if (working_address_list_string != "") {
                    msgid_list.add(MessageID(working_address_list_string))
                }
            }

        }
    }

    override fun toString(): String {
        if (msgid_list.size >= 1) {
            var result = "${msgid_list[0]}"
            if (!single_id) {
                for (i in 1..msgid_list.size - 1) {
                    result += ",${msgid_list[i]}"
                }
            }
            return result
        } else {
            return "" //TODO throw exception?
        }
    }

    override fun getFoldRepresentation(header_name_offset: Int): String  = HeaderValueString(toString()).getFoldRepresentation(header_name_offset)
}