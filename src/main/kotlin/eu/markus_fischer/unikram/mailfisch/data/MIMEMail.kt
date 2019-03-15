package eu.markus_fischer.unikram.mailfisch.data

import eu.markus_fischer.unikram.mailfisch.data.headers.Header
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.james.mime4j.codec.DecodeMonitor
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder
import org.apache.james.mime4j.parser.MimeStreamParser
import org.apache.james.mime4j.stream.MimeConfig
import tech.blueglacier.parser.CustomContentHandler
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Multipart
import javax.mail.Session
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimeUtility
import javax.mail.Part


class MIMEMail : Mail {

    private var attachment_list : MutableList<File> = mutableListOf()
    var text : String = ""

    constructor(headers : MutableMap<String, Header> = mutableMapOf(),
                raw_header : String = "",
                raw_content : String = "") : super(headers, raw_header, raw_content) {
    }

    constructor(raw_mail : String) : super(raw_mail) {
        val contentHandler = CustomContentHandler()

        val mime4jParserConfig = MimeConfig.DEFAULT
        val bodyDescriptorBuilder = DefaultBodyDescriptorBuilder()
        val mime4jParser = MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder)
        mime4jParser.isContentDecoding = true
        mime4jParser.setContentHandler(contentHandler)

        val mailIn =  ByteArrayInputStream(raw_mail.toByteArray(StandardCharsets.UTF_8))
        mime4jParser.parse(mailIn)

        val email = contentHandler.email

        if (email.plainTextEmailBody == null) {
            text = raw_content.trim()
        } else {
            val text_ba = ByteArray(email.plainTextEmailBody.attachmentSize)
            email.plainTextEmailBody.`is`.read(text_ba, 0, email.plainTextEmailBody.attachmentSize)
            text = String(text_ba).trim()
        }


        val temp_folder = System.getProperty("java.io.tmpdir")

        //Attach HTML body as attachment to make it visible
        if (email.htmlEmailBody != null) {
            val ba = ByteArray(email.htmlEmailBody.attachmentSize)
            email.htmlEmailBody.`is`.read(ba, 0, email.htmlEmailBody.attachmentSize)
            val path = temp_folder + "content.html"
            val file = File(path)
            val os = FileOutputStream(file)
            os.write(ba)
            os.close()
            attachment_list.add(file)
        }

        for (attachment in email.attachments) {
            val ba = ByteArray(attachment.attachmentSize)
            attachment.`is`.read(ba, 0, attachment.attachmentSize)
            val path = temp_folder + attachment.attachmentName
            val file = File(path)
            val os = FileOutputStream(file)
            os.write(ba)
            os.close()
            attachment_list.add(file)
        }
    }

    override fun prepareToSend(): String {
        val message = MimeMessage(Session.getInstance(System.getProperties()))
        val content = MimeBodyPart()
        content.setText(text)
        val multipart = MimeMultipart()
        multipart.addBodyPart(content)
        for (file in attachment_list) {
            val attachment = MimeBodyPart()
            val source = FileDataSource(file)
            attachment.dataHandler = DataHandler(source)
            attachment.fileName = file.name
            multipart.addBodyPart(attachment)
        }
        val out_stream = ByteArrayOutputStream()
        message.setContent(multipart)
        message.writeTo(out_stream)
        val splitted_temp_mail = out_stream.toString().split(Regex("(?m)^$"), limit = 2)
        addHeader("MIME-Version", message.getHeader("MIME-Version")[0])
        addHeader("Content-Type", message.getHeader("Content-Type")[0])
        raw_content = splitted_temp_mail[1].trim()
        return super.prepareToSend()
    }

    override fun getTextContent(): String {
        return removeDots(text)
    }

    fun addAttachment(path : String) {
        attachment_list.add(File(path))
    }


    fun getAttachments() : List<File> {
        return attachment_list
    }

    override fun addHeader(name: String, value: String) {
        val encoded_value = MimeUtility.encodeText(value)
        super.addHeader(name, encoded_value)
    }

    override fun prepareToSave() {
        val message = MimeMessage(Session.getInstance(System.getProperties()))
        val content = MimeBodyPart()
        content.setText(text)
        val multipart = MimeMultipart()
        multipart.addBodyPart(content)
        for (file in attachment_list) {
            val attachment = MimeBodyPart()
            val source = FileDataSource(file)
            attachment.dataHandler = DataHandler(source)
            attachment.fileName = file.name
            multipart.addBodyPart(attachment)
        }
        val out_stream = ByteArrayOutputStream()
        message.setContent(multipart)
        message.writeTo(out_stream)
        val splitted_temp_mail = out_stream.toString().split(Regex("(?m)^$"), limit = 2)
        addHeader("MIME-Version", message.getHeader("MIME-Version")[0])
        addHeader("Content-Type", message.getHeader("Content-Type")[0])
        raw_content = splitted_temp_mail[1].trim()
        super.prepareToSave()
    }
}