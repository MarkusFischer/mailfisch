package eu.markus_fischer.unikram.mailfisch.data.headers

import eu.markus_fischer.unikram.mailfisch.data.addresses.Address
import eu.markus_fischer.unikram.mailfisch.getCharPositions
import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments

//TODO make sure, list contains only one Type
class HeaderValueAddressList(var address_list : MutableList<Address>, val single_mailbox: Boolean = false) : HeaderValue {
    constructor(address_list_string : String, single_address: Boolean = false) : this(mutableListOf(), single_address) {
        val working_address_list_string = removeRFC5322Comments(address_list_string)
        if (single_address) {
            address_list.add(Address(working_address_list_string))
        } else {
            val charPositions = getCharPositions(working_address_list_string, ',', true, true, true)
            if (charPositions.isNotEmpty()) {
                var offset = 0
                for (pos in charPositions) {
                    address_list.add(Address(working_address_list_string.substring(offset, pos).trim()))
                    offset = pos + 1
                }
                //TODO to be safe: every Address like constructor should trim
                address_list.add(Address(working_address_list_string.substring(offset).trim()))
            } else {
                if (working_address_list_string != "") {
                    address_list.add(Address(working_address_list_string))
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