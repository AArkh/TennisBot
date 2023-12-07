package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.core.content.ContextCompat.getString
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import javax.inject.Inject
import javax.inject.Singleton

// todo можно удалить и слить данные с SurveyUiState
@Singleton
class AccountInfoRepository @Inject constructor() { // for storing account info throughout the onboarding process

	val surveyData = mutableMapOf<String, Int>() // experience to 4, forehand to 3, etc



}
