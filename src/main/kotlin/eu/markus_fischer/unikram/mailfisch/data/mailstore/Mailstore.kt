package eu.markus_fischer.unikram.mailfisch.data.mailstore

import eu.markus_fischer.unikram.mailfisch.data.HeaderValueAddressList
import eu.markus_fischer.unikram.mailfisch.data.HeaderValueDate
import eu.markus_fischer.unikram.mailfisch.data.HeaderValueString
import eu.markus_fischer.unikram.mailfisch.data.Mail
import java.time.ZonedDateTime
import java.util.*

interface Mailstore {
    fun storeMail(mail : Mail) : UUID
    fun deleteMail(uuid : UUID)
    fun getMail(uuid : UUID) : Mail

    fun getSubject(uuid: UUID) : HeaderValueString
    fun getFrom(uuid: UUID) : HeaderValueAddressList
    fun getTo(uuid: UUID) : HeaderValueAddressList
    fun getDate(uuid: UUID) : HeaderValueDate

    fun isDraft(uuid: UUID) : Boolean
    fun wasSent(uuid: UUID) : Boolean
    fun modifyMail(uuid: UUID, mail: Mail)
    fun moveDraftToSent(uuid: UUID)

    fun isUnread(uuid: UUID) : Boolean
    fun markUnread(unread : Boolean)

    fun getMails(date : ZonedDateTime) : List<Mail>
    fun getMails() : List<Mail>
    fun getMailIds() : List<UUID>

    fun hasMailStored(pop3id : String) : Boolean
    fun getStoredMailsPOPID() : List<String>
}