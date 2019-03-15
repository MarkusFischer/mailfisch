package eu.markus_fischer.unikram.mailfisch.protocols

enum class IMAPFlags(val bitmask : Int) {
    NONE(0x00),
    SEEN(0x01),
    ANSWERED(0x02),
    FLAGGED(0x04),
    DELETED(0x08),
    DRAFT(0x10),
    RECENT(0x20)
}

fun isFlagSet(flagvalue : Int, flag : IMAPFlags) : Boolean {
    return (flagvalue and flag.bitmask) != 0x00
}

fun toggleFlag(flagvalue: Int, flag: IMAPFlags) : Int {
    return (flagvalue xor flag.bitmask)
}

fun getSettedFlags(flagvalue: Int) : List<IMAPFlags> {
    val settedFlags = mutableListOf<IMAPFlags>()
    val possible_values = IMAPFlags.values()
    for (i in 0..possible_values.size - 1) {
        val flag =flagvalue and (1 shl i)
        when (flag) {
            IMAPFlags.SEEN.bitmask -> {settedFlags.add(IMAPFlags.SEEN)}
            IMAPFlags.RECENT.bitmask -> {settedFlags.add(IMAPFlags.RECENT)}
            IMAPFlags.DELETED.bitmask -> {settedFlags.add(IMAPFlags.DELETED)}
            IMAPFlags.ANSWERED.bitmask -> {settedFlags.add(IMAPFlags.ANSWERED)}
            IMAPFlags.DRAFT.bitmask -> {settedFlags.add(IMAPFlags.DRAFT)}
            IMAPFlags.FLAGGED.bitmask -> {settedFlags.add(IMAPFlags.FLAGGED)}
        }
    }
    return settedFlags.toList()
}

fun transformFlagListToInt(flags : List<IMAPFlags>) : Int {
    var temp = 0x00
    for (flag in flags) {
        temp = toggleFlag(temp, flag)
    }
    return temp
}

class IMAPReceiver {
}