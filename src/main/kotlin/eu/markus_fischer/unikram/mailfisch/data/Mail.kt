package eu.markus_fischer.unikram.mailfisch.data

import java.nio.charset.Charset

open class Mail (private var headers : MutableMap<String, Header> = mutableMapOf(),
                 var raw_header : String = "",
                 var raw_content : String = ""){

    constructor(raw_mail : String) : this() {
        val splitted_mail = raw_mail.split(Regex("(?m)^$"), limit = 2)
        val unfolded_header = unfold(splitted_mail[0])
        //subject = Regex("Subject[\t ]*:(([\t \\p{Print}]*[\t ]*)|((\n*\r*([^\t\n\r]\n*\r*)*)|[\t ]*)*)\n").find(unfolded_header)?.value?.split(':')?.get(1) ?: ""
        for (line in unfolded_header.trim().lines()) {
            val (key, value) = line.split(':', limit=2)
            addHeader(key.trim(), value)
        }
        raw_header = splitted_mail[0]
        raw_content = splitted_mail[1]
        if (!headers.containsKey("Date") || !headers.containsKey("From")) {
            throw IllegalArgumentException("received no e-mail that is compilant to RFC5322")
        }

    }

    protected fun isPureASCII(str : String) : Boolean = Charset.forName("US-ASCII").newEncoder().canEncode(str)

    fun addHeader(name : String, value : String) {
        if (isPureASCII(name) && isPureASCII(value)) {
            headers.put(name, Header(name, HeaderValueString(value)))
        } else {
            throw IllegalArgumentException("The header was not encoded in US-ASCII!")
        }
    }

    fun getAllHeaderKeys() = headers.keys.toString()

    fun getHeader(name : String) : Header = headers.get(name) ?: Header(name, HeaderValueString(""))

    private fun unfold(entry : String) : String = entry.replace(Regex("((([\t ]*\n)?[\t ]+)|([\t ]+(\n[\t ]+)))"), " ")

    override fun toString(): String {
        return "Header: ${unfold(raw_header)}" +
                "Content: $raw_content"
    }
}
