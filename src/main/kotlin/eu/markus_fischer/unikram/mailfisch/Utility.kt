package eu.markus_fischer.unikram.mailfisch

fun removeRFC5322Comments(string_with_comments : String) : String {
    var unquoted_open_bracket = false
    var unquoted_open_bracket_pos = -1
    var inside_quote = false
    var escape_sequence_beginning = false
    var comments_to_remove_pos_list : MutableList<Pair<Int, Int>> = mutableListOf()
    for (i in 0..string_with_comments.length - 1) {
        when(string_with_comments[i]) {
            '\"' -> {
                if (!escape_sequence_beginning)
                    inside_quote = !inside_quote
                else
                    escape_sequence_beginning = false
            }
            '\\' -> {
                if (!escape_sequence_beginning)
                    escape_sequence_beginning = true
                else
                    escape_sequence_beginning = false
            }
            '(' -> {
                if (!escape_sequence_beginning) {
                    if (!inside_quote) {
                        unquoted_open_bracket = true
                        unquoted_open_bracket_pos = i
                    }
                } else {
                    escape_sequence_beginning = false
                }
            }
            ')' -> {
                if (!escape_sequence_beginning) {
                    if (!inside_quote) {
                        if (unquoted_open_bracket) {
                            comments_to_remove_pos_list.add(Pair(unquoted_open_bracket_pos, i))
                            unquoted_open_bracket = false
                        } else {
                            //TODO what to do with unvalid comment
                        }
                    }
                } else {
                    escape_sequence_beginning = false
                }
            }
            else -> {
                if (escape_sequence_beginning)
                    escape_sequence_beginning = false
            }
        }
    }
    var offset = 0
    var result = string_with_comments
    for ((begin, end) in comments_to_remove_pos_list) {
        result = result.removeRange(begin - offset, end - offset + 1)
        offset += end - begin + 1
    }
    return result
}

fun isCharInsideString(raw_string : String,
                       character : Char,
                       ignoreQuotes : Boolean = false,
                       ignoreSquareBrackets : Boolean = false,
                       ignoreAngleBrackets : Boolean = false,
                       ignoreRFCComments : Boolean = true) : Boolean {
    val working_string = if (ignoreRFCComments) removeRFC5322Comments(raw_string) else raw_string
    var insideQuote = false
    var insideSquareBrackets = false
    var insideAngleBrackets = false
    var escapeSequence = false
    for (char in working_string) {
        when (char) {
            '<', '>' -> {
                if (!escapeSequence) {
                    insideAngleBrackets = !insideAngleBrackets
                }
                escapeSequence = false
            }
            '[', ']' -> {
                if (!escapeSequence) {
                    insideSquareBrackets = !insideSquareBrackets
                }
                escapeSequence = false
            }
            '"' -> {
                if (!escapeSequence) {
                    insideQuote = !insideQuote
                }
                escapeSequence = false
            }
            character -> {
                if (!escapeSequence) {
                    if (!(insideQuote || insideSquareBrackets || insideAngleBrackets)) {
                        return true
                    }
                }
                escapeSequence = false
            }
            '\\' -> {
                escapeSequence = !escapeSequence
            }
            else -> {
                escapeSequence = false
            }
        }
    }
    return false
}