package tennis.bot.mobile.feed.searchopponent.doubles

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsRepository
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import javax.inject.Inject

@HiltViewModel
class SearchOpponentsDoubleViewModel @Inject constructor(
	repository: SearchOpponentsRepository,
	@ApplicationContext private val context: Context
): SearchOpponentsViewModel(repository)  {


	private val _uiStateFlow = MutableStateFlow(
		SearchOpponentsDoubleUiState(
			context.getString(R.string.opponents_double_hint_title_1),
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()


}