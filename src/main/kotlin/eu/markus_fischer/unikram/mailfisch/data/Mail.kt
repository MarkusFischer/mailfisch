package eu.markus_fischer.unikram.mailfisch.data

open class Mail (val orig_date : String,
                 val from : String,
                 val sender : String,
                 val reply_to : String,
                 val to : String,
                 val cc : String,
                 val bcc : String,
                 val message_id : String,
                 val in_reply_to : String,
                 val references : String,
                 val subject : String,
                 val comments : String,
                 val keywords : String,
                 val optional_field : List<String>,
                 val original_header : String,
                 val content : String){

    override fun toString(): String {
        return "Subject: $subject \n " +
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
    println(fws_free_header)
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
