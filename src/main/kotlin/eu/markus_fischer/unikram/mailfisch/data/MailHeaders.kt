package eu.markus_fischer.unikram.mailfisch.data

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Header(var name : String, var value : HeaderValue) {
    fun getFoldedHeader() : String = "$name: ${value.getFoldRepresentation(name.length)}"
    override fun toString(): String = "$name: $value"
}

interface HeaderValue {
    fun getFoldRepresentation(header_name_offset : Int = 0) : String
}

class HeaderValueString(var value : String) : HeaderValue {
    override fun toString(): String {
        return value
    }

    override fun getFoldRepresentation(header_name_offset : Int) : String {
        val tokens = value.split(' ')
        var result = ""
        var line_length = if (header_name_offset != 0) header_name_offset + 2 else 0
        for (token in tokens) {
            if (line_length + token.length > 78 && result != "" && header_name_offset != 0) {
                result += "\n $token" //insert folded whitespace
                line_length = token.length + 1
            } else {
                result += "$token "
                line_length += token.length + 1
            }
        }
        return "$result\n"
    }
}

class HeaderValueDate( date : ZonedDateTime) : HeaderValue {
    var date : ZonedDateTime = date
        set(value) {}

    constructor(date_string : String) : this(ZonedDateTime.parse(date_string, DateTimeFormatter.RFC_1123_DATE_TIME))

    override fun toString() = DateTimeFormatter.RFC_1123_DATE_TIME.format(date)

    override fun getFoldRepresentation(header_name_offset: Int): String = toString()

}

class HeaderTokenizer(val raw_header_line : String,
                      val ignore_comments : Boolean = true) {
    private var tokens : MutableList<String> = mutableListOf()
}