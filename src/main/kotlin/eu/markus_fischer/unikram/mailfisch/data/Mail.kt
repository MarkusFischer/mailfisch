package eu.markus_fischer.unikram.mailfisch.data

open class Mail (var orig_date : String = "",
                 var from : String = "",
                 var sender : String = "",
                 var reply_to : String = "",
                 var to : String = "",
                 var cc : String = "",
                 var bcc : String = "",
                 var message_id : String = "",
                 var in_reply_to : String = "",
                 var references : String = "",
                 var subject : String = "",
                 var comments : String = "",
                 var keywords : String = "",
                 var optional_field : List<String> = listOf(),
                 var original_header : String = "",
                 var content : String = ""){

    constructor(raw_mail : String) : this() {
        val splitted_mail = raw_mail.split(Regex("(?m)^$"), limit = 2)
        //parse header keys
        val unfolded_header = unfold(splitted_mail[0])
        subject = Regex("Subject[\t ]*:(([\t \\p{Print}]*[\t ]*)|((\n*\r*([^\t\n\r]\n*\r*)*)|[\t ]*)*)\n").find(unfolded_header)?.value?.split(':')?.get(1) ?: ""
        //val subject = Regex("Subject[\t ]*:(((((([\t ]*\r\n)?[\t ]+)|([\t ]+(\r\n[\t ]+)))?\\p{Print})*[\t ]*)|((\n*\r*((\\x00|([\\x01-\\x08]|\\x0b|\\x0c|[\\x0e-\\x1f]|\\x7f)|\\p{Print})\n*\r*)*)|((([\t ]*\r\n)?[\t ]+)|([\t ]+(\r\n[\t ]+))))*)\r\n").find(splitted_mail[0])
        original_header = splitted_mail[0]
        content = splitted_mail[1]
    }

    private fun unfold(entry : String) : String = entry.replace(Regex("((([\t ]*\n)?[\t ]+)|([\t ]+(\n[\t ]+)))"), " ")

    override fun toString(): String {
        return "Subject: $subject \n" +
                "From: $sender \n" +
                "Date: $orig_date \n" +
                "Content: $content"
    }
}

fun parse_mail(raw_mail : String) : Mail {
    if (raw_mail == "") {
        return Mail(orig_date = "",
                from = "",
                sender = "",
                reply_to = "",
                to = "",
                cc = "",
                bcc = "",
                message_id = "",
                in_reply_to = "",
                references = "",
                subject = "",
                comments = "",
                keywords = "",
                optional_field = listOf(),
                original_header = "",
                content = "")
    }
    val splitted_mail = raw_mail.split(Regex("(?m)^$"), limit = 2)
    //parse header keys
    val fws_free_header = remove_folded_whitespaces(splitted_mail[0])
    //val subject = Regex("Subject[\t ]*:(((((([\t ]*\r\n)?[\t ]+)|([\t ]+(\r\n[\t ]+)))?\\p{Print})*[\t ]*)|((\n*\r*((\\x00|([\\x01-\\x08]|\\x0b|\\x0c|[\\x0e-\\x1f]|\\x7f)|\\p{Print})\n*\r*)*)|((([\t ]*\r\n)?[\t ]+)|([\t ]+(\r\n[\t ]+))))*)\r\n").find(splitted_mail[0])
    print("matched")
    return Mail(orig_date = "",
                from = "",
                sender = "",
                reply_to = "",
                to = "",
                cc = "",
                bcc = "",
                message_id = "",
                in_reply_to = "",
                references = "",
                subject = "",
                comments = "",
                keywords = "",
                optional_field = listOf(),
                original_header = splitted_mail[0],
                content = splitted_mail[1])
}

fun remove_folded_whitespaces(header : String) : String = header.replace(Regex("((([\t ]*\n)?[\t ]+)|([\t ]+(\n[\t ]+)))"), " ")
