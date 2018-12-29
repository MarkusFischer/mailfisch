package eu.markus_fischer.unikram.mailfisch.protocols

enum class IMAPFlags(val bitmask : Int) {
    NONE(0x00),
    SEEN(0x01),
    ANSWERED(0x02),
    FLAGGED(0x04),
    DELETED(0x08),
    DRAFT(0x10),
    RECENT(0x20);
}

fun isFlagSet(flagvalue : Int, flag : IMAPFlags) : Boolean {
    return (flagvalue and flag.bitmask) != 0x00
}

fun toggleFlag(flagvalue: Int, flag: IMAPFlags) : Int {
    return (flagvalue xor flag.bitmask)
}

fun getSettedFlags(flagvalue: Int) : List<IMAPFlags> {
    val settedFlags = mutableListOf<IMAPFlags>()
    for (i in 0..IMAPFlags.values().size - 1) {
        settedFlags.add((flagvalue and (1 shl i)) as IMAPFlags)
    }
    return settedFlags.toList()
}

class IMAPReceiver {
}