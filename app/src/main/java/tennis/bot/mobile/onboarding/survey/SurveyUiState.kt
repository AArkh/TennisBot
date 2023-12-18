package tennis.bot.mobile.onboarding.survey

import tennis.bot.mobile.core.CoreUtilsItem

data class SurveyUiState(
    val progress: Int,
    val title: String,
    val selectedPage: Int, // current shown index in surveyPages
    val surveyPages: List<SurveyItem>
)

data class SurveyItem(
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val sideNoteTitle: String,
    val sideNoteText: String,
    val isTwoOptions: Boolean = false,
    val pickedOptionId: Int? = null // unpicked by default
): CoreUtilsItem()