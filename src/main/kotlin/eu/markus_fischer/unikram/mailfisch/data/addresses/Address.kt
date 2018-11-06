package eu.markus_fischer.unikram.mailfisch.data.addresses

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

    override fun toString(): String {
        return when (address_type) {
            Type.none -> ""
            Type.mailbox -> mailbox.toString()
            Type.group -> mailbox.toString()
        }
    }
}
