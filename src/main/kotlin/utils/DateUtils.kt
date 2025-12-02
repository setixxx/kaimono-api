package setixx.software.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun dateParse(
    date: String?,
): LocalDate? {
    return date?.let {

        try {
            if (date.isBlank()){
                return null
            }
            LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
        } catch (e: Exception) {
            throw IllegalArgumentException("Incorrect Birthday date format")
        }
    }
}
