package eu.markus_fischer.unikram.mailfisch.data

import java.nio.charset.Charset

//TODO use multimap for headers
open class Mail (private var headers : MutableMap<String, Header> = mutableMapOf(),
                 raw_header : String = "",
                 var raw_content : String = ""){
    var raw_header : String = raw_header
        get() {
            if (raw_header == "") {
                var result = ""
                for ((name, header) in headers) {
                    if (!name.equals("bcc", ignoreCase = true)) { //removing bcc header
                        result += "${header.getFoldedHeader()}"
                    }
                }
                return result
            } else {
                return raw_header //todo add headers that were added later
            }
        }

    constructor(raw_mail : String) : this() {
        val splitted_mail = raw_mail.split(Regex("(?m)^$"), limit = 2)
        val unfolded_header = unfold(splitted_mail[0])
        //subject = Regex("Subject[\t ]*:(([\t \\p{Print}]*[\t ]*)|((\n*\r*([^\t\n\r]\n*\r*)*)|[\t ]*)*)\n").find(unfolded_header)?.value?.split(':')?.get(1) ?: ""
        for (line in unfolded_header.trim().lines()) {
            val (key, value) = line.split(':', limit=2)
            addHeader(key.trim(), value.trim())
        }
        raw_header = splitted_mail[0]
        raw_content = splitted_mail[1]
        if (!headers.containsKey("date") || !headers.containsKey("from")) {
            throw IllegalArgumentException("received no e-mail that is compilant to RFC5322")
        }

    }

    protected val ascii_encoder = Charset.forName("US-ASCII").newEncoder()
    protected fun isPureASCII(str : String) : Boolean = ascii_encoder.canEncode(str)

    fun addHeader(name : String, value : String) {
        if (isPureASCII(name) && isPureASCII(value)) {
            var headerValue : HeaderValue = when(name.toLowerCase()) {
                "date", "resent-date" -> HeaderValueDate(value)
                "from", "resent-from", "reply-to", "to", "resent-to", "resent-cc", "cc",
                "resent-bcc", "bcc" -> HeaderValueAddressList(value)
                "sender", "resent-sender" -> HeaderValueAddressList(value, single_address = true)
                "message-id" -> HeaderValueMessageIdList(value, single_id = true)
                "in-reply-to", "references" -> HeaderValueMessageIdList(value)
                else -> HeaderValueString(value)
            }
            headers.put(name.toLowerCase(), Header(name.toLowerCase(), headerValue))
        } else {
            throw IllegalArgumentException("The header was not encoded in US-ASCII!")
        }
    }

    fun addHeader(name : String, value : HeaderValue) {
        if (isPureASCII(name) && isPureASCII(value.toString())) {
            var headerValue : HeaderValue = when(name.toLowerCase()) {
                "date", "resent-date" -> {
                    if (value is HeaderValueDate) {
                        value
                    } else {
                        throw IllegalArgumentException("date and resent-date could be only from type HeaderValueDate")
                    }
                }
                "from", "resent-from", "reply-to", "to", "resent-to", "resent-cc", "cc",
                "resent-bcc", "bcc" -> {
                    if (value is HeaderValueAddressList) value else throw IllegalArgumentException("HeaderValueAddressList required but not given")
                }
                "sender", "resent-sender" -> {
                    if (value is HeaderValueAddressList && value.single_mailbox) value else throw IllegalArgumentException("HeaderValueAddressList with single_mailbox required but not given")
                }
                "message-id" -> {
                    if (value is HeaderValueMessageIdList && value.single_id) value else throw IllegalArgumentException("HeaderValueMessageIdList with single_id required but not given!")
                }
                "in-reply-to", "references" -> {
                    if (value is HeaderValueMessageIdList) value else throw IllegalArgumentException("HeaderValueMessageIdList required but not given!")
                }
                else -> value
            }
            headers.put(name.toLowerCase(), Header(name.toLowerCase(), headerValue))
        } else {
            throw IllegalArgumentException("The header was not encoded in US-ASCII!")
        }
    }

    fun getAllHeaderKeys() = headers.keys.toString()

    fun getHeader(name : String) : Header = headers.get(name.toLowerCase()) ?: Header(name.toLowerCase(), HeaderValueString(""))

    fun hasHeader(name : String) : Boolean = headers.containsKey(name.toLowerCase())

    private fun unfold(entry : String) : String = entry.replace(Regex("((([\t ]*\n)?[\t ]+)|([\t ]+(\n[\t ]+)))"), " ")

    override fun toString(): String {
        return "Header: ${unfold(raw_header)}" +
                "Content: $raw_content"
    }

    fun prepareToSend() : String {
        var result = ""
        for ((name, header) in headers) {
            if (!name.equals("bcc", ignoreCase = true)) { //removing bcc header
                result += "${header.getFoldedHeader()}"
            }
        }
        result += "\n"
        raw_content = raw_content.trim()
        for (line in raw_content.split('\n')) {
            if (line.isNotEmpty() && line[0] == '.'){
                result += '.'
            }
            result += "$line\n"
        }
        return result.replace("\n", "\r\n") //TODO \r\n schon vorhanden behandeln
    }
}
