package eu.markus_fischer.unikram.mailfisch.data.mailstore

//Temp
data class MailFolder(val name : String, val childrens : MutableList<MailFolder>, val parent : MailFolder?, val delete_safe : Boolean)

fun buildMailFoldersOutOfList(root_name : String, mailbox_list : List<String>) : MailFolder {
    val root = MailFolder(name = root_name,
            childrens = mutableListOf(),
            parent = null,
            delete_safe = true)
    for (mailbox in mailbox_list) {
        val splitted_path = mailbox.split('/')
        var iterator : MailFolder = root
        for (path in splitted_path) {
            var child_found = false
            for (child in iterator.childrens) {
                if (child.name == path) {
                    iterator = child
                    child_found = true
                    break
                }
            }
            if (!child_found) {
                val temp = MailFolder(path, mutableListOf(), parent = iterator, delete_safe = (path in delete_safe_names && iterator.parent == null))
                iterator.childrens.add(temp)
                iterator = temp
            }
        }
    }
    return root
}

fun buildReversePath(folder : MailFolder) : String {
    var entry = folder.name
    var iter : MailFolder? = folder
    while (iter?.parent != null) {
        entry = "${iter!!.parent?.name}/$entry"
        iter = iter.parent
    }
    return entry.split('/', limit=2)[1] //Todo dont remove account for multiuser support
}
