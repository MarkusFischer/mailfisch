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

val delete_safe_names = listOf<String>("inbox", "trash", "draft")

/*val tempfolders = listOf( MailFolder("Inbox", "mf.dev@mail.de"),
                                        MailFolder("Drafts", "mf.dev@mail.de"),
                                        MailFolder("Foobar", "mf.dev@mail.de"))*/