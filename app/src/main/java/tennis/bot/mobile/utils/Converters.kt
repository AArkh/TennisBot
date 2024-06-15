package tennis.bot.mobile.utils

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

const val DEFAULT_DATE_TIME = "0001-01-01T00:00:00Z"
const val CONTENT_LINK = "http://bugz.su:9000/publiccontent/"
const val DEFAULT_PICS_PREFIX = "https://s3.aeza.cloud/perfect-wine/DEV/avatars/"
val dateFormats = listOf(
	"yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
	"yyyy-MM-dd'T'hh:mm:ss'Z'",
	"dd/MM/yy"
)

data class FormattedDate(
	val time: String,
	val day: Int,
	val dayOfWeek: String,
	val month: String
)

fun convertDateAndTime(dateString: String, formatPattern: String? = null): String? {
	if (dateString == DEFAULT_DATE_TIME) return null

	for (format in dateFormats) {
		try {
			val dateTimeFormatter = SimpleDateFormat(format, Locale.getDefault())
			val timeStampMs = dateTimeFormatter.parse(dateString)
			val someOtherFormatter = SimpleDateFormat(formatPattern ?: "d MMMM yyyy", Locale.getDefault())
			return someOtherFormatter.format(timeStampMs) ?: ""
		} catch (e: Exception) {
			// If parsing fails, try the next format
		}
	}
	return null
}

fun formatDateForMatchPostItem(timestampString: String): FormattedDate {
	val formattedDateString = convertDateAndTime(timestampString, "d MMMM yyyy HH:mm")
		?: throw IllegalArgumentException("Invalid timestamp format")

	val dateFormat = SimpleDateFormat("d MMMM yyyy HH:mm", Locale.getDefault())
	val date = dateFormat.parse(formattedDateString) ?: throw IllegalArgumentException("Invalid date format")

	val calendar = Calendar.getInstance().apply { time = date }

	val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

	return FormattedDate(
		time = timeFormat.format(date),
		day = calendar.get(Calendar.DAY_OF_MONTH),
		dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date),
		month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date)
	)
}

fun formatDateForFeed(dateString: String, context: Context): String {
	val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.getDefault())
	val timeStampMs = dateTimeFormatter.parse(dateString).time

	val currentTime = System.currentTimeMillis()
	val diffInMillis = currentTime - timeStampMs

	val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
	val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
	val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

	return when {
		days > 365 -> context.getString(R.string.year_ago, (days / 365))
		days > 30 -> context.resources.getQuantityString(R.plurals.minutes_ago, (days/30).toInt(), (days/30).toInt())
		days > 0 -> context.resources.getQuantityString(R.plurals.days_ago, days.toInt(),  days.toInt())
		hours > 0 -> context.resources.getQuantityString(R.plurals.hours_ago, hours.toInt(), hours.toInt())
		minutes > 0 -> context.resources.getQuantityString(R.plurals.minutes_ago, minutes.toInt(), minutes.toInt())
		else -> context.getString(R.string.just_now)
	}
}

fun TextView.formRatingChange(difference: String) {
	if (difference.contains("-")) {
		backgroundTintList = ContextCompat.getColorStateList(context, R.color.tb_bland_red)
		setTextColor(ContextCompat.getColor(context, R.color.tb_red))
		val drawable: Drawable? = AppCompatResources.getDrawable(context, R.drawable.vector_down)
		setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
	} else {
		backgroundTintList = ContextCompat.getColorStateList(context, R.color.tb_bland_green)
		setTextColor(ContextCompat.getColor(context, R.color.tb_primary_green))
		val drawable: Drawable? = AppCompatResources.getDrawable(context, R.drawable.vector_up)
		setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
	}
	text = difference.trimStart('-')
}



fun buildImageRequest(context: Context, imageUrl: String?): Any? { // loads a null before an actual profile pic. should we tweak the behavior to change that?
	var result: Any? = null

	if (imageUrl == null) {
		result = R.drawable.null_placeholder
	} else if (imageUrl.contains("default")) {
		val resourceId = if (imageUrl.contains("https")) {
			getDefaultDrawableResourceId(context,
				imageUrl.removeSuffix(".png").removePrefix(DEFAULT_PICS_PREFIX))
		} else {
			getDefaultDrawableResourceId(context, imageUrl.removeSuffix(".png"))
		}

		if (resourceId != null) result = resourceId
	} else if(imageUrl.contains("pics") || imageUrl.contains("movies")) {
		result = if (imageUrl.contains("https")) {
			imageUrl
		} else {
			CONTENT_LINK + imageUrl
		}
	} else {
		result = if (imageUrl.contains("https")) {
			imageUrl
		} else {
			AccountPageAdapter.IMAGES_LINK + imageUrl
		}
	}

	return result
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