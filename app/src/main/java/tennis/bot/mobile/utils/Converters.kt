package tennis.bot.mobile.utils

import android.icu.text.SimpleDateFormat
import java.util.Locale

fun convertDateAndTime(dateString: String): String {
	val formats = listOf(
		"yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
		"yyyy-MM-dd'T'hh:mm:ss'Z'"
	)

	for (format in formats) {
		try {
			val dateTimeFormatter = SimpleDateFormat(format, Locale.getDefault())
			val timeStampMs = dateTimeFormatter.parse(dateString)
			val someOtherFormatter = SimpleDateFormat("d MMMM yyyy", Locale("ru", "RU"))
			return someOtherFormatter.format(timeStampMs) ?: ""
		} catch (e: Exception) {
			// If parsing fails, try the next format
		}
	}

	return ""
}