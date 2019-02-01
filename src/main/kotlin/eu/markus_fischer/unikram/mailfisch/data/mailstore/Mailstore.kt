package eu.markus_fischer.unikram.mailfisch.data.mailstore

import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueAddressList
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueDate
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueString
import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.protocols.IMAPFlags
import java.time.ZonedDateTime
import java.util.*


interface Mailstore {

    fun initMailStore()

    fun storeMail(mail: Mail, serverid: String = "", flag: Int = 0, mailbox: String = "") : UUID
    fun deleteMail(uuid : UUID)
    fun getMail(uuid : UUID) : Mail

    //TODO decide if this is really necessary -> could be retrieved out of mail
    fun getSubject(uuid: UUID) : HeaderValueString
    fun getFrom(uuid: UUID) : HeaderValueAddressList
    fun getTo(uuid: UUID) : HeaderValueAddressList
    fun getDate(uuid: UUID) : HeaderValueDate

    fun getFlags(uuid: UUID) : List<IMAPFlags>
    fun modifyMail(uuid: UUID, mail: Mail = Mail(mutableMapOf(), ""), flag: Int = 0, mailbox: String = "")



    //TODO filter for more fields
    fun getMails(date : ZonedDateTime, mailbox: String = "") : List<UUID>
    fun getMails(flags: Int = 0, mailbox: String = "") : List<UUID>
    fun getMails(mailbox: String = "") : List<UUID>

    fun getStoredMailsServerIds() : List<String>

    fun getMailboxes() : List<String>
}

//Temp
data class MailFolder(val name : String, val childrens : MutableList<MailFolder>, val parent : MailFolder?, val delete_safe : Boolean)

val delete_safe_names = listOf<String>("inbox", "trash", "draft")


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

/*val tempfolders = listOf( MailFolder("Inbox", "mf.dev@mail.de"),
                                        MailFolder("Drafts", "mf.dev@mail.de"),
                                        MailFolder("Foobar", "mf.dev@mail.de"))*/