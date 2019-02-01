package eu.markus_fischer.unikram.mailfisch.data


import javafx.beans.property.SimpleBooleanProperty
import org.joda.time.DateTime
import java.util.*
import javax.mail.internet.MimeUtility
import tornadofx.*

//class for most important information
class MailSummary (val from : String,
                   val to: String,
                   subject: String,
                   val date: DateTime,
                   val uuid: UUID,
                   unseen: Boolean){

    val unseenProperty = SimpleBooleanProperty(unseen)
    var unseen by unseenProperty

    private var raw_subject = subject
    var subject = ""
        private set
        get() { return MimeUtility.decodeText(raw_subject)
        }
}