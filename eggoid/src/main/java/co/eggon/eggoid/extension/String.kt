package co.eggon.eggoid.extension

fun String?.capitalizeFully(): String? {
    this?.toLowerCase()?.toCharArray()?.let {
        if (it.isNotEmpty()) {
            var capitalizeNext = true
            for (i in 0..it.size - 1) {
                val ch = it[i]
                if (ch == ' ') {
                    capitalizeNext = true
                } else if (capitalizeNext) {
                    it[i] = Character.toTitleCase(ch)
                    capitalizeNext = false
                }
            }
        }
        return String(it)
    }
    return null
}