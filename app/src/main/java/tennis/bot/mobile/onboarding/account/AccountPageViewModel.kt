package tennis.bot.mobile.onboarding.account

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.survey.SurveyResultItem
import javax.inject.Inject

@HiltViewModel
class AccountPageViewModel @Inject constructor(
	@ApplicationContext private val context: Context
): ViewModel() {

	val basicLayout = listOf( // todo replace int's with const or whatever suitable
		BasicInfoAndRating("", "test test", "testId", "1920", "1080"),
		Calibration(
			9,
			context.getString(R.string.calibration_matches_remain, 1),
			context.getString(R.string.calibration_rounds_remain_text, 9)),
		MatchesPlayed(
			context.getString(R.string.account_matches_played, 1),
			context.getString(R.string.last_game_date, "6 Января 2024") ),
		PointsAndPosition(
			context.getString(R.string.account_tournament_points, 99),
			context.getString(R.string.tournament_title), // should get it elsewhere
			3.toString() ),
		Tournaments(context.getString(R.string.tournament_title) ),
		Friends(
			context.getString(R.string.tournament_title),
			null, null, null, null,
			false),
		ButtonSwitch(true)
	)

	val dummyGameDataForButtons = listOf(
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value"),
		SurveyResultItem("something", "something's value", noUnderline = true),
	)

	val dummyContactsForButtons = listOf(
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value"),
		SurveyResultItem("something else", "something else's value", noUnderline = true),
	)
}