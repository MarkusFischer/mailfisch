package eu.markus_fischer.unikram.mailfisch.data.addresses

import eu.markus_fischer.unikram.mailfisch.isCharInsideString

class Address {
    var mailbox : Mailbox = Mailbox("", "", "")
        private set

    var group : Group = Group("", mutableListOf())
        private set

    //address_type = true -> type as described in rfc: mailbox
    private enum class Type {
        none,
        mailbox,
        group
    }
    private var address_type : Type = Type.none

    constructor(mb : Mailbox) {
        mailbox = mb
        address_type = Type.mailbox
    }

    constructor(grp : Group) {
        group = grp
        address_type = Type.group
    }

    constructor(address : String) {
        var inside_quote = false
        var escaped_sequence = false
        if (isCharInsideString(address, ';', true, true)) {
            group = Group(address)
            address_type = Type.group
        } else {
            mailbox = Mailbox(address)
            address_type = Type.mailbox
        }
    }

    override fun toString(): String {
        return when (address_type) {
            Type.none -> ""
            Type.mailbox -> mailbox.toString()
            Type.group -> mailbox.toString()
        }
    }
}
