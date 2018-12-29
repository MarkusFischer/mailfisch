package eu.markus_fischer.unikram.mailfisch.data.headers

class Header(var name : String, var value : HeaderValue) {
    fun getFoldedHeader() : String = "$name: ${value.getFoldRepresentation(name.length + 2)}"
    override fun toString(): String = "$name: $value"
}
