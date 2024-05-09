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
import tennis.bot.mobile.onboarding.survey.toApiNumericFormat
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.GAME_STYLE_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.RACQUET_STRINGS_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.RACQUET_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.SHOES_TITLE
import tennis.bot.mobile.profile.account.AccountPageViewModel.Companion.SURFACE_TITLE
import tennis.bot.mobile.profile.editgamedata.EditGameDataApi
import tennis.bot.mobile.profile.editgamedata.GameStyleNetwork
import tennis.bot.mobile.profile.editgamedata.IsOneBackhandNetwork
import tennis.bot.mobile.profile.editgamedata.IsRightHandNetwork
import tennis.bot.mobile.profile.editgamedata.RacquetNetwork
import tennis.bot.mobile.profile.editgamedata.RacquetStringsNetwork
import tennis.bot.mobile.profile.editgamedata.ShoesNetwork
import tennis.bot.mobile.profile.editgamedata.SurfaceNetwork
import tennis.bot.mobile.profile.editprofile.BirthdayNetwork
import tennis.bot.mobile.profile.editprofile.EditProfileApi
import tennis.bot.mobile.profile.editprofile.LocationNetwork
import tennis.bot.mobile.profile.editprofile.NameSurnameNetwork
import tennis.bot.mobile.profile.editprofile.PhoneNumberNetwork
import tennis.bot.mobile.profile.editprofile.TelegramNetwork
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileAndEnumsRepository @Inject constructor(
	private val userProfileApi: UserProfileApi,
	private val enumsDao: AllEnumsDao,
	private val enumsApi: EnumsApi,
	private val gameDataApi: EditGameDataApi,
	private val editProfileApi: EditProfileApi,
	@ApplicationContext private val context: Context
) {
	private lateinit var cachedProfileData: ProfileData
	private val sharedPreferences = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

	val defaultGameData = listOf(
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_style), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_hand), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_backhand), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_surface), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_shoes), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_racquet), getString(context, R.string.survey_option_null)),
		SurveyResultItem(context.getString(R.string.accountpage_gamedata_racquetStrings), getString(context, R.string.survey_option_null), noUnderline = true),
	)

	companion object {
		const val NAME_TITLE = "name"
		const val BIRTHDAY_TITLE = "birthday"
		const val CITY_ID_TITLE = "cityId"
		const val DISTRICT_ID_TITLE = "districtId"
		const val PHONE_NUMBER_TITLE = "phoneNumber"
		const val TELEGRAM_TITLE = "telegram"
	}

	@WorkerThread
	fun precacheProfile(): ProfileData? {
		val response = userProfileApi.getProfile().execute()
		if (response.code() == 200) {
			cachedProfileData = response.body()!!
		}
		return response.body()
	}

	@WorkerThread
	fun getProfile() : ProfileData {
		if (::cachedProfileData.isInitialized) {
			return cachedProfileData
		} else {
			precacheProfile()
		}
		return cachedProfileData
	}

	fun updateCachedProfile(key: String, value: String) {
		return when(key){
			NAME_TITLE -> { cachedProfileData = cachedProfileData.copy(name = value) } // extract to const
			BIRTHDAY_TITLE -> { cachedProfileData = cachedProfileData.copy(birthday = value) }
			CITY_ID_TITLE -> { cachedProfileData = cachedProfileData.copy(cityId = value.toInt()) }
			DISTRICT_ID_TITLE -> { cachedProfileData = cachedProfileData.copy(districtId = value.toInt()) }
			PHONE_NUMBER_TITLE -> { recordPhone(value) }
			TELEGRAM_TITLE -> { cachedProfileData = cachedProfileData.copy(telegram = value) }
			IS_RIGHTHAND_TITLE -> {}
			IS_ONE_BACKHAND_TITLE -> {}
			SURFACE_TITLE -> {}
			SHOES_TITLE -> {}
			RACQUET_TITLE -> {}
			RACQUET_STRINGS_TITLE -> {}
			else -> {}
		}
	}

	private fun updateCachedProfile(key: String, value: Int) {
		return when(key){
			GAME_STYLE_TITLE -> {cachedProfileData = cachedProfileData.copy(gameStyle = value)}
			CITY_ID_TITLE -> { cachedProfileData = cachedProfileData.copy(cityId = value) }
			DISTRICT_ID_TITLE -> { cachedProfileData = cachedProfileData.copy(districtId = value) }
			IS_RIGHTHAND_TITLE -> { cachedProfileData = cachedProfileData.copy(isRightHand = value == 1) }
			IS_ONE_BACKHAND_TITLE -> {  cachedProfileData = cachedProfileData.copy(isOneBackhand = value == 1) }
			SURFACE_TITLE -> { cachedProfileData = cachedProfileData.copy(surface = value) }
			SHOES_TITLE -> { cachedProfileData = cachedProfileData.copy(shoes = value)}
			RACQUET_TITLE -> { cachedProfileData = cachedProfileData.copy(racquet = value)}
			RACQUET_STRINGS_TITLE -> { cachedProfileData = cachedProfileData.copy(racquetStrings = value)}
			else -> {}
		}
	}

	@WorkerThread
	suspend fun precacheEnums(): List<EnumType> {
		val response = enumsApi.getAllEnums().execute().body()
		enumsDao.insert(response!!)
		enumsDao.insert(listOf(
			EnumType(IS_RIGHTHAND_TITLE, listOf(
				EnumData(0, "Левая", "left"),
				EnumData(1, "Правая", "right")
			)),
			EnumType(IS_ONE_BACKHAND_TITLE, listOf(
				EnumData(0, "Двуручный", "two-handed"),
				EnumData(1, "Одноручный", "one-handed")
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

	suspend fun getEnumById(selectedEnumTypeAndId: Pair<String, Int?>): String {
		val allEnums = getEnums()

		val enumType = allEnums.find { return@find it.type == selectedEnumTypeAndId.first }

		if (selectedEnumTypeAndId.second != null) {
			val enum = enumType?.enums?.find { return@find it.id == selectedEnumTypeAndId.second }
			if (enum != null) return enum.name
		} else {
			return context.getString(R.string.survey_option_null)
		}

		return ""
	}

	suspend fun getEnumsById(selectedEnumTypesAndIds: List<Pair<String, Int?>>): List<String> {
		val allEnums = getEnums()
		val decodedList = mutableListOf<String>()

		for ((type, id) in selectedEnumTypesAndIds){
			val enumType = allEnums.find { return@find it.type == type }

			if (id != null) {
				val enum = enumType?.enums?.find { return@find it.id == id }
				if (enum != null) decodedList.add(enum.name)
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
			GAME_STYLE_TITLE -> { defaultGameData[0].resultTitle }
			IS_RIGHTHAND_TITLE -> { defaultGameData[1].resultTitle }
			IS_ONE_BACKHAND_TITLE -> { defaultGameData[2].resultTitle }
			SURFACE_TITLE -> { defaultGameData[3].resultTitle }
			SHOES_TITLE -> { defaultGameData[4].resultTitle }
			RACQUET_TITLE -> { defaultGameData[5].resultTitle }
			RACQUET_STRINGS_TITLE -> { defaultGameData[6].resultTitle }
			else -> { AccountPageAdapter.NULL_STRING }
		}
	}

	@WorkerThread
	fun updateGameDataValue(gameDataType: String, value: Int) {
		when(gameDataType) {
			defaultGameData[0].resultTitle -> {
				kotlin.runCatching {
					gameDataApi.putGameStyle(GameStyleNetwork(value)).execute()
				}.onSuccess {
					updateCachedProfile(GAME_STYLE_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[1].resultTitle -> {
				kotlin.runCatching {
					gameDataApi.putIsRightHand(IsRightHandNetwork(value == 1)).execute()
				}.onSuccess {
					updateCachedProfile(IS_RIGHTHAND_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[2].resultTitle -> {
				kotlin.runCatching {
				gameDataApi.putIsOneBackhand(IsOneBackhandNetwork(value == 1)).execute()
				}.onSuccess {
					updateCachedProfile(IS_ONE_BACKHAND_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[3].resultTitle -> {
				kotlin.runCatching {
				gameDataApi.putSurface(SurfaceNetwork(value)).execute()
				}.onSuccess {
					updateCachedProfile(SURFACE_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[4].resultTitle -> {
				kotlin.runCatching {
				gameDataApi.putShoes(ShoesNetwork(value)).execute()
				}.onSuccess {
					updateCachedProfile(SHOES_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[5].resultTitle -> {
				kotlin.runCatching {
				gameDataApi.putRacquet(RacquetNetwork(value)).execute()
				}.onSuccess {
					updateCachedProfile(RACQUET_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
			defaultGameData[6].resultTitle -> {
				kotlin.runCatching {
				gameDataApi.putRacquetStrings(RacquetStringsNetwork(value)).execute()
				}.onSuccess {
					updateCachedProfile(RACQUET_STRINGS_TITLE, value)
				}.onFailure { context.showToast("Failed to update the value. Please, try again later") }
			}
		}
	}

	@WorkerThread
	fun putNameSurname(name: String, surname: String): Boolean{
		val response = kotlin.runCatching { editProfileApi.putNameSurname( NameSurnameNetwork(name, surname) ).execute() }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	fun putBirthday(networkDateTime: String): Boolean {
		val response = kotlin.runCatching { editProfileApi.putBirthday(BirthdayNetwork(networkDateTime)).execute()  }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	fun putLocation(cityId: Int, districtId: Int?): Boolean {
		val response = kotlin.runCatching { editProfileApi.putLocation(LocationNetwork(cityId, districtId)).execute()  }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	fun putPhoneNumber(phoneNumber: String): Boolean {
		val response =  kotlin.runCatching { editProfileApi.putPhoneNumber(PhoneNumberNetwork(phoneNumber.toApiNumericFormat())).execute() }.getOrElse { return false }
		return response.isSuccessful
	}

	@WorkerThread
	fun putTelegramNetwork(telegram: String): Boolean {
		val response =  kotlin.runCatching { editProfileApi.putTelegram(TelegramNetwork(telegram)).execute() }.getOrElse { return false }
		return response.isSuccessful
	}

	fun recordPhone(phoneNumber: String) {
		sharedPreferences.edit().putString(OnboardingRepository.PHONE_NUMBER_HEADER, phoneNumber).apply()
	}

	fun getPhoneNumber(): String? {
		return sharedPreferences.getString(OnboardingRepository.PHONE_NUMBER_HEADER, null)
	}
}