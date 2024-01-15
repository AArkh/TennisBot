package tennis.bot.mobile.profile.matches

import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel  @Inject constructor(): ViewModel() {
	fun onOptionClicked(buttonClicked: TextView, buttonNotClicked1: TextView, buttonNotClicked2: TextView) {
		buttonClicked.setBackgroundColor(getColor(buttonClicked.context, R.color.tb_bg_card))
		buttonClicked.setTextColor(getColor(buttonClicked.context, R.color.tb_gray_dark))

		buttonNotClicked1.setBackgroundColor(getColor(buttonNotClicked1.context, R.color.invisible))
		buttonNotClicked1.setTextColor(getColor(buttonNotClicked1.context, R.color.tb_gray_active))
		buttonNotClicked2.setBackgroundColor(getColor(buttonNotClicked2.context, R.color.invisible))
		buttonNotClicked2.setTextColor(getColor(buttonNotClicked2.context, R.color.tb_gray_active))
	}

	val mockData = listOf(
		MatchItem(
			playerOneProfilePic = "url_player_one",
			playerOneName = "Player One",
			playerOneCurrentRating = "1200",
			playerOnePreviousRating = "1150",
			playerTwoProfilePic = "url_player_two",
			playerTwoName = "Player Two",
			playerTwoCurrentRating = "1100",
			playerTwoPreviousRating = "1050",
			score = "2-1",
			set11 = "6",
			set12 = "4",
			set13 = "7",
			set21 = "3",
			set22 = "6",
			set23 = "5",
			dateTime = "2022-01-01"),
		MatchItem(
			playerOneProfilePic = "url_player_one_1",
			playerOneName = "Player One 1",
			playerOneCurrentRating = "1180",
			playerOnePreviousRating = "1140",
			playerTwoProfilePic = "url_player_two_1",
			playerTwoName = "Player Two 1",
			playerTwoCurrentRating = "1120",
			playerTwoPreviousRating = "1080",
			score = "2-0",
			set11 = "6",
			set12 = "4",
			set13 = " ",
			set21 = "6",
			set22 = "3",
			set23 = " ",
			dateTime = "2022-01-02"),
		MatchItem(
			playerOneProfilePic = "url_player_one_2",
			playerOneName = "Player One 2",
			playerOneCurrentRating = "1250",
			playerOnePreviousRating = "1200",
			playerTwoProfilePic = "url_player_two_2",
			playerTwoName = "Player Two 2",
			playerTwoCurrentRating = "1050",
			playerTwoPreviousRating = "1000",
			score = "0-2",
			set11 = "3",
			set12 = "6",
			set13 = " ",
			set21 = "4",
			set22 = "6",
			set23 = " ",
			dateTime = "2022-01-03")
	)

	private val _uiStateFlow = MutableStateFlow<MatchesUiState>(
		MatchesUiState.MatchesDataReceived(
			mockData
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()



}