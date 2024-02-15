package tennis.bot.mobile.feed.addscore

import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment.Companion.SCORE_TYPE_REQUEST_KEY
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment.Companion.SELECTED_SCORE_TYPE_OPTION
import javax.inject.Inject

@HiltViewModel
class AddScoreViewModel @Inject constructor(): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		AddScoreUiState(null, false)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onOptionPicked(activity: FragmentActivity, pickedOption: Int?){
		if (pickedOption == null) return
		_uiStateFlow.value = _uiStateFlow.value.copy(pickedOption = pickedOption, nextButtonEnabled = true)

		activity.supportFragmentManager.setFragmentResult(
			SCORE_TYPE_REQUEST_KEY,
			bundleOf(SELECTED_SCORE_TYPE_OPTION to pickedOption)
		)
	}

	fun onNextButtonClicked(navigationCallback: () -> Unit){
		navigationCallback.invoke()
	}
}