package eu.markus_fischer.unikram.mailfisch.data.addresses

import eu.markus_fischer.unikram.mailfisch.getCharPositions

class Address {
    var mailbox : Mailbox = Mailbox("", "", "")
        private set

    var group : Group = Group("", mutableListOf())
        private set

    enum class Type {
        none,
        mailbox,
        group
    }
    var address_type : Type = Type.none
        private set

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
        if (getCharPositions(address, ';', true, true).isNotEmpty()) {
            group = Group(address)
            address_type = Type.group
        } else {
            mailbox = Mailbox(address)
            address_type = Type.mailbox
        }
    }

    fun getMailboxes() : List<Mailbox> {
        if (address_type == Type.mailbox) {
            return listOf(mailbox)
        } else if (address_type == Type.group) {
            return group.mailbox_list.toList()
        } else {
            return emptyList()
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
