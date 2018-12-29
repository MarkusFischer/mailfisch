package eu.markus_fischer.unikram.mailfisch.data.headers

interface HeaderValue {
    fun getFoldRepresentation(header_name_offset : Int = 0) : String
}