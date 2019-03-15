package eu.markus_fischer.unikram.mailfisch.data.headers

import eu.markus_fischer.unikram.mailfisch.removeRFC5322Comments
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HeaderValueDate(var date : ZonedDateTime) : HeaderValue {

    constructor(date_string : String) : this(ZonedDateTime.parse(removeRFC5322Comments(date_string).trim(), DateTimeFormatter.RFC_1123_DATE_TIME))

    constructor() : this(ZonedDateTime.now())

    override fun toString(): String = "${DateTimeFormatter.RFC_1123_DATE_TIME.format(date)}\n"

    override fun getFoldRepresentation(header_name_offset: Int): String = toString()

}