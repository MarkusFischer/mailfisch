package eu.markus_fischer.unikram.mailfisch.data

import eu.markus_fischer.unikram.mailfisch.data.addresses.Address
import eu.markus_fischer.unikram.mailfisch.data.addresses.Mailbox
import eu.markus_fischer.unikram.mailfisch.getCharPositions
import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Header(var name : String, var value : HeaderValue) {
    fun getFoldedHeader() : String = "$name: ${value.getFoldRepresentation(name.length + 2)}"
    override fun toString(): String = "$name: $value"
}

interface HeaderValue {
    fun getFoldRepresentation(header_name_offset : Int = 0) : String
}

class HeaderValueString(var value : String) : HeaderValue {
    override fun toString(): String {
        return value
    }

    override fun getFoldRepresentation(header_name_offset : Int) : String {
        val tokens = value.split(' ')
        var result = ""
        var line_length = if (header_name_offset != 0) header_name_offset + 2 else 0
        for (token in tokens) {
            if (line_length + token.length > 78 && result != "" && header_name_offset != 0) {
                result += "\n $token" //insert folded whitespace
                line_length = token.length + 1
            } else {
                result += "$token "
                line_length += token.length + 1
            }
        }
        return "$result\n"
    }
}

class HeaderValueDate(var date : ZonedDateTime) : HeaderValue {

    constructor(date_string : String) : this(ZonedDateTime.parse(removeRFC5322Comments(date_string).trim(), DateTimeFormatter.RFC_1123_DATE_TIME))

    override fun toString(): String = DateTimeFormatter.RFC_1123_DATE_TIME.format(date)

    override fun getFoldRepresentation(header_name_offset: Int): String = toString()

}

//TODO make sure, list contains only one Type
class HeaderValueAddressList(var address_list : MutableList<Address>, val single_mailbox: Boolean = false) : HeaderValue {
    constructor(address_list_string : String, single_address: Boolean = false) : this(mutableListOf(), single_address) {
        var working_address_list_string = removeRFC5322Comments(address_list_string)
        if (single_address) {
            address_list.add(Address(working_address_list_string))
        } else {
            var remaining_string = working_address_list_string
            val charPositions = getCharPositions(working_address_list_string, ',', true, true, true)
            if (charPositions.isNotEmpty()) {
                for (pos in charPositions) {
                    address_list.add(Address(remaining_string.substring(0, pos)))
                    remaining_string = remaining_string.substring(pos +1 )
                }
            } else {
                if (remaining_string != "") {
                    address_list.add(Address(remaining_string))
                }
            }

        }
    }

    override fun toString() : String {
        if (address_list.size >= 1) {
            var result = "${address_list[0]}"
            if (!single_mailbox) {
                for (i in 1..address_list.size - 1) {
                    result += ",${address_list[i]}"
                }
            }
            return result
        } else {
            return "" //TODO throw exception?
        }
    }
    //TODO own implementation
    override fun getFoldRepresentation(header_name_offset: Int): String = HeaderValueString(toString()).getFoldRepresentation(header_name_offset)
}
