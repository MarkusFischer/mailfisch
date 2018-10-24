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
                 val content : String){

    override fun toString(): String {
        return "Subject: $subject \n " +
                "From: $sender \n" +
                "Date: $orig_date \n" +
                "Content: $content"
    }
}

fun parse_mail(raw_mail : String) : Mail {
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
                content = "")
}
