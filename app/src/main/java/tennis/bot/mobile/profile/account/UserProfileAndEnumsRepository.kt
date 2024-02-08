package tennis.bot.mobile.profile.account

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat.getString
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.IS_ONE_BACKHAND_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.IS_RIGHTHAND_TITLE
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.RACQUET_STRINGS_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.RACQUET_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.SHOES_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.SURFACE_TITLE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileAndEnumsRepository @Inject constructor(
	private val api: UserProfileApi,
	private val enumsDao: AllEnumsDao,
	private val enumsApi: EnumsApi,
	@ApplicationContext private val context: Context
) {
	private lateinit var cachedProfileData: ProfileData
	private val sharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

	val defaultGameData = listOf(
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_hand), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_backhand), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_surface), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_shoes), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_racquet), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_racquetStrings), getString(context, R.string.survey_option_null), noUnderline = true),
	)

	@WorkerThread
	fun precacheProfile(): ProfileData {
		val response = api.getProfile().execute()
		if (response.code() == 200) {
			cachedProfileData = response.body()!!
		}
		return response.body()!!
	}

	@WorkerThread
	fun getProfile() : ProfileData {
		if (::cachedProfileData.isInitialized) {
			return cachedProfileData
		}
		return precacheProfile()
	}

	fun updateCachedProfile(key: String, value: String) {
		return when(key){
			"name" -> { cachedProfileData = cachedProfileData.copy(name = value) }
			"birthday" -> {cachedProfileData = cachedProfileData.copy(birthday = value) }
			"cityId" -> {cachedProfileData = cachedProfileData.copy(cityId = value.toInt()) }
			"districtId" -> {cachedProfileData = cachedProfileData.copy(districtId = value.toInt()) }
			"phoneNumber" -> { recordPhone(value) }
			"telegram" -> {cachedProfileData = cachedProfileData.copy(telegram = value) }
			else -> {}
		}
	}

	@WorkerThread
	suspend fun precacheEnums(): List<EnumType> {
		val response = enumsApi.getAllEnums().execute().body()
		enumsDao.insert(response!!)
		enumsDao.insert(listOf(
			EnumType(IS_RIGHTHAND_TITLE, listOf(
				EnumData(0, "левая", "left"),
				EnumData(1, "правая", "right")
			)),
			EnumType(IS_ONE_BACKHAND_TITLE, listOf(
				EnumData(0, "двуручный", "two-handed"),
				EnumData(1, "одноручный", "one-handed")
			)),
		))
		return response
	}

	@WorkerThread
	suspend fun getEnums(): List<EnumType> {
		val cachedAllEnums = enumsDao.getAllEnumTypes()
		if (cachedAllEnums.isNotEmpty()) {
			return cachedAllEnums
		}
		return precacheEnums()
	}

	fun getEnumsById(allEnums: List<EnumType>, selectedEnumTypesAndIds: List<Pair<String, Int?>>): List<String> {
		val decodedList = mutableListOf<String>()

		for ((type, id) in selectedEnumTypesAndIds){
			val enumType = allEnums.find { return@find it.type == type }

			if (id != null && id != 0) {
				val enum = enumType?.enums?.find { return@find it.id == id }
				if (enum != null) decodedList.add(enum.name.replaceFirstChar { it.uppercase() })
			} else {
				decodedList.add(context.getString(R.string.survey_option_null))
			}
		}

		return if (decodedList.isNotEmpty()) { decodedList.toList() } else { emptyList() }
	}

	suspend fun getEnumGroup(enumType: String): List<EnumData>? {
		val enums = getEnums()
		return enums.find { it.type == enumType }?.enums
	}

	fun getEnumTitle(enumType: String): String {
		return when(enumType) {
			IS_RIGHTHAND_TITLE -> { defaultGameData[0].resultTitle }
			IS_ONE_BACKHAND_TITLE -> { defaultGameData[1].resultTitle }
			SURFACE_TITLE -> { defaultGameData[2].resultTitle }
			SHOES_TITLE -> { defaultGameData[3].resultTitle }
			RACQUET_TITLE -> { defaultGameData[4].resultTitle }
			RACQUET_STRINGS_TITLE -> { defaultGameData[5].resultTitle }
			else -> { AccountPageAdapter.NULL_STRING }
		}
	}

	fun recordPhone(phoneNumber: String) {
		sharedPreferences.edit().putString(OnboardingRepository.PHONE_NUMBER_HEADER, phoneNumber).apply()
	}

	fun getPhoneNumber(): String? {
		return sharedPreferences.getString(OnboardingRepository.PHONE_NUMBER_HEADER, null)
	}
}