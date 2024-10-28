package tennis.bot.mobile.utils

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.TextView
import androidx.annotation.WorkerThread
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

const val DEFAULT_DATE_TIME = "0001-01-01T00:00:00Z"
const val CONTENT_LINK = "http://bugz.su:9000/publiccontent/"
val dateFormats = listOf(
	"yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
	"yyyy-MM-dd'T'hh:mm:ss'Z'",
	"dd/MM/yy"
)

data class FormattedDate(
	val time: String,
	val day: String,
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
		day = calendar.get(Calendar.DAY_OF_MONTH).toString(),
		dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(date).toString().replaceFirstChar { it.uppercase() },
		month = SimpleDateFormat("MMMM", Locale.getDefault()).format(date).replaceFirstChar { it.uppercase() }
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

fun getCurrentFormattedDateAndTimeForNetwork(): String {
	val currentDate = Calendar.getInstance().time
	val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//	isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")

	return isoDateFormat.format(currentDate)
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
		val defaultPicsPrefix = imageUrl.substringBefore("default")
		val resourceId = if (imageUrl.contains("https")) {
			getDefaultDrawableResourceId(context,
				imageUrl.removeSuffix(".png").removePrefix(defaultPicsPrefix))
		} else {
			getDefaultDrawableResourceId(context, imageUrl.removeSuffix(".png"))
		}
		if (resourceId != null) result = resourceId

	} else if(imageUrl.contains("pics") || imageUrl.contains("movies") || imageUrl.contains("avatars")) {
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
		FirebaseCrashlytics.getInstance().recordException(it)
	}
	runCatching {
		cityString = locationRepository.getLocations().find { it.id == countryInt }
			?.cities!!.find { it.id == cityInt }!!.name
	}.onFailure {
		FirebaseCrashlytics.getInstance().recordException(it)
	}
	runCatching {
		districtString = locationRepository.getLocations().find { it.id == countryInt }
			?.cities!!.find { it.id == cityInt }
			?.districts!!.find { it.id == districtInt }!!.title
	}.onFailure {
		FirebaseCrashlytics.getInstance().recordException(it)
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
		FirebaseCrashlytics.getInstance().recordException(it)
	}
	runCatching {
		cityInt = locationRepository.getLocations().find { it.countryName == countryString }
			?.cities!!.find { it.name == cityString }!!.id
	}.onFailure {
		FirebaseCrashlytics.getInstance().recordException(it)
	}
	runCatching {
		districtInt = locationRepository.getLocations().find { it.countryName == countryString }
			?.cities!!.find { it.name == cityString }
			?.districts!!.find { it.title == districtString }!!.id
	}.onFailure {
		FirebaseCrashlytics.getInstance().recordException(it)
	}

	return Triple(countryInt, cityInt, districtInt)
}

fun uriToFile(context: Context, uri: Uri): File? {
	val fileName = getFileName(context, uri) ?: return null
	val tempFile = File(context.cacheDir, fileName)
	try {
		val inputStream = context.contentResolver.openInputStream(uri) ?: return null
		val outputStream = FileOutputStream(tempFile)
		inputStream.use { input ->
			outputStream.use { output ->
				input.copyTo(output)
			}
		}
		return tempFile
	} catch (e: IOException) {
		FirebaseCrashlytics.getInstance().recordException(e)
		e.printStackTrace()
		return null
	}
}

private fun getFileName(context: Context, uri: Uri): String? {
	var result: String? = null
	if (uri.scheme == "content") {
		val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
		cursor.use { cur ->
			if (cur != null && cur.moveToFirst()) {
				val index = cur.getColumnIndex(OpenableColumns.DISPLAY_NAME)
				if (index != -1) {
					result = cur.getString(index)
				}
			}
		}
	}
	if (result == null) {
		result = uri.path
		val cut = result?.lastIndexOf('/')
		if (cut != -1) {
			result = result?.substring(cut!! + 1)
		}
	}
	return result
}

fun isRuLocale(): Boolean {
	val locale = Locale.getDefault().language
	return (locale == "ru")
}

fun getCountryCodeForPhoneNumber(phoneNumber: String): String? {
	val phoneNumberUtil = PhoneNumberUtil.getInstance()
	return try {
		val numberProto = phoneNumberUtil.parse(phoneNumber, null)
		val regionCode = phoneNumberUtil.getRegionCodeForNumber(numberProto)
		regionCode
	} catch (e: NumberParseException) {
		FirebaseCrashlytics.getInstance().recordException(e)
		e.printStackTrace()
		null
	}
}