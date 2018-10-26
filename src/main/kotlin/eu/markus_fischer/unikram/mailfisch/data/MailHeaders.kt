package eu.markus_fischer.unikram.mailfisch.data

class Header(var name : String, var value : String) {
    private var folded_representation : String = ""

    fun fold() {

    }
}

class HeaderTokenizer(val raw_header_line : String,
                      val ignore_comments : Boolean = true) {
    private var tokens : MutableList<String> = mutableListOf()
}