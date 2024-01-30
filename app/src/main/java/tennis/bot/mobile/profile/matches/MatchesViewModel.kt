package tennis.bot.mobile.profile.matches

import android.util.Log
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getColorStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ItemKeyedDataSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.PositionalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.matches.MatchesRepository.*
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

	fun onFetchingMatches(){
		viewModelScope.launch {
			val matches = repository.getMatchItems()
			_uiStateFlow.value = MatchesUiState.MatchesDataReceived( matches )
		}
	}

	fun getMatchesNew(): Flow<PagingData<MatchItem>> {
		return Pager(
			config = PagingConfig(
				pageSize = 20,
				enablePlaceholders = true
			),
			pagingSourceFactory = { repository.MyDataSource() }
		).flow
	}

}