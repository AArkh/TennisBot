package tennis.bot.mobile.profile.matches

import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
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

	private val _uiStateFlow = MutableStateFlow<MatchesUiState>(
		MatchesUiState.Loading
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun getMatchesNew(): Flow<PagingData<MatchItem>> {
		Log.d("1234567", "New page")
		return Pager(
			config = PagingConfig(
				pageSize = SearchOpponentsViewModel.PAGE_SIZE,
				maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2),
				enablePlaceholders = true
			),
			pagingSourceFactory = { repository.MatchesDataSource() }
		).flow
	}
}