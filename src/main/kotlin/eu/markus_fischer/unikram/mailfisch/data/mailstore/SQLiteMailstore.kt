package eu.markus_fischer.unikram.mailfisch.data.mailstore

import eu.markus_fischer.unikram.mailfisch.data.MIMEMail
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueAddressList
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueDate
import eu.markus_fischer.unikram.mailfisch.data.headers.HeaderValueString
import eu.markus_fischer.unikram.mailfisch.data.Mail
import eu.markus_fischer.unikram.mailfisch.data.MailSummary
import eu.markus_fischer.unikram.mailfisch.dateTimeToZonedDateTime
import eu.markus_fischer.unikram.mailfisch.protocols.IMAPFlags
import eu.markus_fischer.unikram.mailfisch.protocols.getSettedFlags
import eu.markus_fischer.unikram.mailfisch.protocols.isFlagSet
import eu.markus_fischer.unikram.mailfisch.zonedDateTimeToDateTime
import org.jetbrains.exposed.dao.IdTable
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.dao.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.sql.Connection
import java.time.ZonedDateTime
import java.util.*


object Mails : UUIDTable() {
    val raw_header: Column<String> = text("raw_header")
    val content: Column<String> = text("content")
    val from: Column<String> = text("from")
    val to: Column<String> = text("to")
    val cc: Column<String> = text("cc")
    val subject: Column<String> = text("subject")
    val date: Column<DateTime> = date("date")
    val header_msg_id: Column<String> = text("header_msg_id")
    val references: Column<String> = text("references")
    val server_id: Column<String> = text("server_id")
    val flags: Column<Int> = integer("flags")
    val mailbox: Column<String> = text("mailbox")
}

object Mailboxes : IntIdTable() {
    val mailbox: Column<String> = text("mailbox")
}



//TODO single mailstore for each account
class SQLiteMailstore : Mailstore {

    /*constructor() {
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }*/

    companion object {
        var connected = false
    }

    override fun getMailboxes(): List<String> {
        return transaction {
            Mailboxes.selectAll().withDistinct().map { it[Mailboxes.mailbox] }
        }
    }

    override fun initMailStore() {
        if (!connected) {
            Database.connect("jdbc:sqlite:data/data.db", "org.sqlite.JDBC")
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        }
        transaction {
            SchemaUtils.create(Mails)
            SchemaUtils.create(Mailboxes)
        }
        val mailboxes = getMailboxes()
        if (!mailboxes.contains("inbox")) {
            transaction {
                Mailboxes.insert {
                    it[mailbox] = "inbox" //inbox mailbox is predefined in imap standard
                }
            }
        }
        if (!mailboxes.contains("draft")) {
            transaction {
                Mailboxes.insert {
                    it[mailbox] = "draft"
                }
            }
        }
        if (!mailboxes.contains("sent")) {
            transaction {
                Mailboxes.insert {
                    it[mailbox] = "sent"
                }
            }
        }
    }

    override fun storeMail(mail: Mail, serverid: String, flag: Int, mailbox: String): UUID {
        var exisiting_mailbox = false
        val mailboxes = getMailboxes()
        if (mailbox.toLowerCase() != "inbox") {
            exisiting_mailbox = mailbox in mailboxes
        } else {
            exisiting_mailbox = true
        }
        if (!exisiting_mailbox) {
            transaction {
                Mailboxes.insert {
                    it[Mailboxes.mailbox] = mailbox
                }
            }
        }
        return transaction {
            (Mails.insert {
                it[raw_header] = mail.raw_header
                it[content] = mail.raw_content
                it[from] = mail.getHeader("from").value.toString()
                it[to] = mail.getHeader("to").value.toString()
                it[cc] = mail.getHeader("cc").value.toString()
                it[subject] = mail.getHeader("subject").value.toString()
                it[date] = zonedDateTimeToDateTime((mail.getHeader("date").value as HeaderValueDate).date)
                it[header_msg_id] = mail.getHeader("message-id").value.toString()
                it[references] = mail.getHeader("references").value.toString()
                it[server_id] = serverid
                it[flags] = flag
                it[Mails.mailbox] = mailbox
            } get Mails.id)?.value ?: UUID.randomUUID() //TODO predefined uuid for error
        }
    }

    override fun deleteMail(uuid: UUID) {
        return transaction {
            Mails.deleteWhere { Mails.id eq uuid }
        }
    }

    override fun getMail(uuid: UUID): Mail {
        val temp_list = mutableListOf<Mail>()
        transaction {
            Mails.slice(Mails.raw_header, Mails.content).select{ Mails.id eq uuid}.first {
                val header = it[Mails.raw_header]
                val content = it[Mails.content]
                temp_list.add(Mail("$header\n\n$content"))
            }
        }
        return temp_list[0]
    }

    override fun getSubject(uuid: UUID): HeaderValueString {
        return transaction {
            HeaderValueString(Mails.slice(Mails.subject).select { Mails.id eq uuid }.withDistinct().map {
                it[Mails.subject]
            }[0])
        }
    }

    override fun getFrom(uuid: UUID): HeaderValueAddressList {
        return transaction {
            HeaderValueAddressList(Mails.slice(Mails.from).select { Mails.id eq uuid }.withDistinct().map {
                it[Mails.from]
            }[0])
        }
    }

    override fun getTo(uuid: UUID): HeaderValueAddressList {
        return transaction {
            HeaderValueAddressList(Mails.slice(Mails.to).select { Mails.id eq uuid }.withDistinct().map {
                it[Mails.to]
            }[0])
        }
    }

    override fun getDate(uuid: UUID): HeaderValueDate {
        return transaction {
            HeaderValueDate(dateTimeToZonedDateTime(Mails.slice(Mails.date).select { Mails.id eq uuid }.withDistinct().map {
                it[Mails.date]
            }[0]))
        }
    }

    override fun getFlags(uuid: UUID): List<IMAPFlags> {
        var res = mutableListOf<List<IMAPFlags>>()
        transaction {
            Mails.slice(Mails.flags).select { Mails.id eq uuid }.forEach {
                val flag_int = it[Mails.flags]
                res.add(getSettedFlags(flag_int))
            }
        }
        return res[0]
    }


    override fun modifyMail(uuid: UUID, mail: Mail, flag: Int, mailbox: String) {
        transaction {
            Mails.update({ Mails.id eq uuid }) {
                it[raw_header] = mail.raw_header
                it[content] = mail.raw_content
                it[from] = mail.getHeader("from").value.toString()
                it[to] = mail.getHeader("to").value.toString()
                it[cc] = mail.getHeader("cc").value.toString()
                it[subject] = mail.getHeader("subject").value.toString()
                it[date] = zonedDateTimeToDateTime((mail.getHeader("date").value as HeaderValueDate).date)
                it[header_msg_id] = mail.getHeader("message-id").value.toString()
                it[references] = mail.getHeader("references").value.toString()
                it[flags] = flag
                it[Mails.mailbox] = mailbox
            }
        }
    }

    override fun getMails(date: ZonedDateTime, mailbox: String): List<UUID> {
        val results = transaction {
            if (mailbox == "") {
                Mails.slice(Mails.id).select { (Mails.date eq zonedDateTimeToDateTime(date)) }
            } else {
                Mails.slice(Mails.id).select { (Mails.date eq zonedDateTimeToDateTime(date)) and (Mails.mailbox eq mailbox) }
            }.withDistinct().map {
                it[Mails.id]
            }
        }
        val uuids : MutableList<UUID> = mutableListOf()
        for (result in results) {
            uuids.add(result.value)
        }
        return uuids
    }

    override fun getMails(flags: Int, mailbox: String ): List<UUID> {
        val results = transaction {
            if (mailbox == "") {
                Mails.slice(Mails.id).select { Mails.flags eq flags }
            } else {
                Mails.slice(Mails.id).select { Mails.flags eq flags and (Mails.mailbox eq mailbox) }
            }.withDistinct().map {
                it[Mails.id]
            }
        }
        val uuids : MutableList<UUID> = mutableListOf()
        for (result in results) {
            uuids.add(result.value)
        }
        return uuids
    }

    override fun getMails(mailbox: String): List<UUID> {
        val results = transaction {
            if (mailbox == "") {
                Mails.slice(Mails.id).selectAll()
            } else {
                Mails.slice(Mails.id).select { Mails.mailbox eq mailbox }
            }.
            withDistinct().map {
                it[Mails.id]
            }
        }
        val uuids : MutableList<UUID> = mutableListOf()
        for (result in results) {
            uuids.add(result.value)
        }
        return uuids
    }


    override fun getStoredMailsServerIds(): List<String> {
        return transaction {
            Mails.slice(Mails.server_id).selectAll().withDistinct().map {
                it[Mails.server_id]
            }
        }
    }

    fun getMailSummarys(mailbox: String) : List<MailSummary> {
        val temp = mutableListOf<MailSummary>()
        val result  = transaction {
            if (mailbox == "") {
                Mails.selectAll() //TODO slice
            } else {
                Mails.select { Mails.mailbox eq mailbox }
            }.forEach {
                val id = it[Mails.id].value
                val from = it[Mails.from]
                val to = it[Mails.to]
                val subject = it[Mails.subject]
                val date = it[Mails.date]
                val unseen = !isFlagSet(it[Mails.flags], IMAPFlags.SEEN)
                temp.add(MailSummary(from, to, subject, date, id, unseen))
            }
        }
        return temp
    }

    fun getMIMEMail(uuid: UUID): MIMEMail {
        val temp_list = mutableListOf<MIMEMail>()
        transaction {
            Mails.slice(Mails.raw_header, Mails.content).select{ Mails.id eq uuid}.first {
                val header = it[Mails.raw_header]
                val content = it[Mails.content]
                temp_list.add(MIMEMail("$header\n\n$content"))
            }
        }
        return temp_list[0]
    }
}