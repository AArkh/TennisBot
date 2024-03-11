package tennis.bot.mobile.utils

import android.content.Context
import android.database.Cursor
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.WorkerThread
import tennis.bot.mobile.onboarding.location.LocationRepository
import java.io.File
import java.util.Locale

const val DEFAULT_DATE_TIME = "0001-01-01T00:00:00Z"

fun convertDateAndTime(dateString: String): String? {
	val formats = listOf(
		"yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
		"yyyy-MM-dd'T'hh:mm:ss'Z'",
		"dd/MM/yy"
	)
	if (dateString == DEFAULT_DATE_TIME) return null

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
	return null
}

@WorkerThread
suspend fun convertLocationIntToString(
	locationRepository: LocationRepository, countryInt: Int?, cityInt: Int?, districtInt: Int?): Triple<String?, String?, String?> {
	var countryString: String? = null
	var cityString: String? = null
	var districtString: String? = null
	runCatching {
		countryString = locationRepository.getLocations().find { it.id == countryInt }!!.countryName
	}.onFailure {
		Log.d("1234567", "recordLocationValues: country error")
	}
	runCatching {
		cityString = locationRepository.getLocations().find { it.id == countryInt }
			?.cities!!.find { it.id == cityInt }!!.name
	}.onFailure {
		Log.d("1234567", "recordLocationValues: city error")
	}
	runCatching {
		districtString = locationRepository.getLocations().find { it.id == countryInt }
			?.cities!!.find { it.id == cityInt }
			?.districts!!.find { it.id == districtInt }!!.title
	}.onFailure {
		Log.d("1234567", "recordLocationValues: district error")
	}

	return Triple(countryString, cityString, districtString)
}

@WorkerThread
suspend fun convertLocationStringToInt(
	locationRepository: LocationRepository, countryString: String?, cityString: String?, districtString: String?): Triple<Int?, Int?, Int?> {
	var countryInt: Int? = null
	var cityInt: Int? = null
	var districtInt: Int? = null
	runCatching {
		countryInt = locationRepository.getLocations().find { it.countryName == countryString }!!.id
	}.onFailure {
		Log.d("1234567", "recordLocationValues: country error")
	}
	runCatching {
		cityInt = locationRepository.getLocations().find { it.countryName == countryString }
			?.cities!!.find { it.name == cityString }!!.id
	}.onFailure {
		Log.d("1234567", "recordLocationValues: city error")
	}
	runCatching {
		districtInt = locationRepository.getLocations().find { it.countryName == countryString }
			?.cities!!.find { it.name == cityString }
			?.districts!!.find { it.title == districtString }!!.id
	}.onFailure {
		Log.d("1234567", "recordLocationValues: district error")
	}

	return Triple(countryInt, cityInt, districtInt)
}

fun uriToFile(context: Context, uri: Uri): File? {
	val projection = arrayOf(MediaStore.Images.Media.DATA)
	val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
	val columnIndex: Int? = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
	cursor?.moveToFirst()
	val filePath: String? = columnIndex?.let { cursor.getString(it) }
	cursor?.close()
	return filePath?.let { File(it) }
}