package com.jar.app.core_utils.data

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Returns the ordinal string for an integer.
 *
 * @param n The integer to convert.
 * @return The ordinal string (e.g., "1st", "2nd", "3rd", "4th", etc.) for the given integer.
 */
private fun getOrdinal(n: Int) = when {
    n in 11..13 -> "${n}th"
    n % 10 == 1 -> "${n}st"
    n % 10 == 2 -> "${n}nd"
    n % 10 == 3 -> "${n}rd"
    else -> "${n}th"
}

/**
 * Converts a date string from "dd-MM-yyyy" format to a custom format.
 *
 * The custom format is "{day ordinal} {month abbreviation} '{last two digits of year}".
 * For example, "1st Jan '20" for January 1, 2020.
 *
 * @param inputDateString The input date string, which must be in "dd-MM-yyyy" format.
 * @return The input date string converted to the custom format.
 */
fun convertDateFormat(inputDateString: String): String {
    if (inputDateString.isBlank()) return ""
    val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    // parse the input date string
    val date = inputDateFormat.parse(inputDateString)

    // get day of month with ordinal
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val dayWithOrdinal = getOrdinal(dayFormat.format(date).toInt())

    // output date format
    val outputDateFormat = SimpleDateFormat("MMM ''yy", Locale.getDefault())

    // format the date to the output format
    return dayWithOrdinal + " " + outputDateFormat.format(date)
}
