package tennis.bot.mobile.onboarding.account

import androidx.annotation.WorkerThread
import tennis.bot.mobile.core.AuthTokenRepository
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileAndEnumsRepository @Inject constructor(
	private val api: UserProfileApi,
	private val enumsApi: EnumsApi,
	private val tokenRepo: AuthTokenRepository,
) {
	private lateinit var cachedProfileData: ProfileData
	private lateinit var cachedAllEnums: List<EnumType>
	private lateinit var cachedGameData: List<SurveyResultItem>

	@WorkerThread
	suspend fun precacheProfile(): ProfileData {
		val response = api.getProfile(tokenRepo.getAccessToken() ?: "").execute()
		if (response.code() == 200) {
			cachedProfileData = response.body()!!
		}
		return response.body()!!
	}

	@WorkerThread
	suspend fun getProfile() : ProfileData {
		if (cachedProfileData != null) {
			return cachedProfileData
		}
		return precacheProfile()
	}

	@WorkerThread
	suspend fun precacheEnums(): List<EnumType> {
		val response = enumsApi.getAllEnums().execute().body()
		cachedAllEnums = response!!
		return response
	}

	@WorkerThread
	suspend fun getEnums() : List<EnumType> {
		if (cachedAllEnums.isNotEmpty()) {
			return cachedAllEnums
		}
		return precacheEnums()
	}

	fun getEnumTypeList(allEnums: List<EnumType>, selectedEnumType: String): List<EnumData> {
		val enumType = allEnums.find { return@find it.type == selectedEnumType }
		if (enumType?.enums?.isNotEmpty() == true) {
			return enumType.enums.map { return@map EnumData(it.id, it.name, it.nameEnglish) }
		} else {
			return emptyList()
		}
	}
}