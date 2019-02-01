package eu.markus_fischer.unikram.mailfisch

import eu.markus_fischer.unikram.mailfisch.data.mailstore.SQLiteMailstore
import eu.markus_fischer.unikram.mailfisch.data.mailstore.buildMailFoldersOutOfList
import eu.markus_fischer.unikram.mailfisch.protocols.POP3Receiver
import eu.markus_fischer.unikram.mailfisch.protocols.getSettedFlags
import eu.markus_fischer.unikram.mailfisch.ui.MailfischMain
import org.apache.commons.mail.MultiPartEmail
import tornadofx.launch
import tech.blueglacier.parser.CustomContentHandler
import org.apache.james.mime4j.codec.DecodeMonitor
import org.apache.james.mime4j.parser.MimeStreamParser
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder
import org.apache.james.mime4j.stream.BodyDescriptorBuilder
import org.apache.james.mime4j.stream.MimeConfig
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import javax.mail.Session
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


fun main(args : Array<String>) {
    val mailstore = SQLiteMailstore()
    mailstore.initMailStore()

    launch<MailfischMain>(args)
}