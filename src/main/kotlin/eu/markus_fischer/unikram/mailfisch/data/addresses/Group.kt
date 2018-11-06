package eu.markus_fischer.unikram.mailfisch.data.addresses

class Group(var displayname : String,
            var mailbox_list : MutableList<Mailbox>) {

    constructor(group_string : String) : this ("", mutableListOf()) {
        var (display, remaining_content) = group_string.split(':', limit = 2)
        displayname = display
        remaining_content = remaining_content.trim()
        if (remaining_content != ";") {
            val mailbox_strings = remaining_content.substring(0, remaining_content.length - 1).split(',')
            for (mailbox_string in mailbox_strings) {
                mailbox_list.add(Mailbox(mailbox_string))
            }
        }
    }

    override fun toString(): String {
        var result = "$displayname:"
        for (mailbox in mailbox_list) {
            result += mailbox
        }
        return "$result;"
    }
}