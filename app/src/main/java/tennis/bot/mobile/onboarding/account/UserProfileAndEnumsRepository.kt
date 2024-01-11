package tennis.bot.mobile.onboarding.account

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat.getString
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.core.AuthTokenRepository
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileAndEnumsRepository @Inject constructor(
	private val api: UserProfileApi,
	private val enumsDao: AllEnumsDao,
	private val enumsApi: EnumsApi,
	private val tokenRepo: AuthTokenRepository,
	@ApplicationContext private val context: Context
) {
	private lateinit var cachedProfileData: ProfileData
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
		if (::cachedProfileData.isInitialized) {
			return cachedProfileData
		}
		return precacheProfile()
	}

	@WorkerThread
	suspend fun precacheEnums(): List<EnumType> {
		val response = enumsApi.getAllEnums().execute().body()
		enumsDao.insert(response!!)
		return response
	}

	@WorkerThread
	suspend fun getEnums() : List<EnumType> {
		val cachedAllEnums = enumsDao.getAllEnumTypes()
		if (cachedAllEnums.isNotEmpty()) {
			return cachedAllEnums
		}
		return precacheEnums()
	}

	fun getEnumsById(allEnums: List<EnumType>, selectedEnumTypesAndIds: List<Pair<String, Int?>>): List<String?> {
		val decodedList = mutableListOf<String?>()

		for ((type, id) in selectedEnumTypesAndIds){
			val enumType = allEnums.find { return@find it.type == type }
			val enum = enumType?.enums?.find {return@find it.id == id}
			decodedList.add(enum?.name)
		}

		return if (decodedList.isNotEmpty()) {
			decodedList.toList()
		} else {
			emptyList()
		}
	}
}