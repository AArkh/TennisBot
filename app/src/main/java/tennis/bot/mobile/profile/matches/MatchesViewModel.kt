package tennis.bot.mobile.profile.matches

import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel  @Inject constructor(
	private val repository: MatchesRepository
): ViewModel() {
	fun onOptionClicked(buttonClicked: TextView, buttonNotClicked1: TextView, buttonNotClicked2: TextView) {
		buttonClicked.backgroundTintList = getColorStateList(buttonClicked.context, R.color.tb_bg_card)
		buttonClicked.setTextColor(getColor(buttonClicked.context, R.color.tb_gray_dark))

		buttonNotClicked1.backgroundTintList = getColorStateList(buttonNotClicked1.context, R.color.invisible)
		buttonNotClicked1.setTextColor(getColor(buttonNotClicked1.context, R.color.tb_gray_active))
		buttonNotClicked2.backgroundTintList = getColorStateList(buttonNotClicked2.context, R.color.invisible)
		buttonNotClicked2.setTextColor(getColor(buttonNotClicked2.context, R.color.tb_gray_active))
	}

	val mockData = listOf(
		MatchItem(
			isWin = true,
			isDouble = false,
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
			isWin = true,
			isDouble = false,
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
			isWin = true,
			isDouble = false,
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
		MatchesUiState.Loading
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onFetchingMatches(){
		viewModelScope.launch {
			val matches = repository.getMatchItems()
			_uiStateFlow.value = MatchesUiState.MatchesDataReceived( matches )
		}
	}

}