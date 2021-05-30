package me.singleneuron.common.data

data class EXIFStrings(var dateExist: Boolean = false, var strings: Array<String?> = emptyArray(), var dateString: String = "") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EXIFStrings

        if (dateExist != other.dateExist) return false
        if (!strings.contentEquals(other.strings)) return false
        if (dateString != other.dateString) return false

        return true
    }

    override fun hashCode(): Int {
        var result = dateExist.hashCode()
        result = 31 * result + strings.contentHashCode()
        result = 31 * result + dateString.hashCode()
        return result
    }
}