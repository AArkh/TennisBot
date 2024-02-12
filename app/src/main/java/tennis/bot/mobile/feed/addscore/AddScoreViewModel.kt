package tennis.bot.mobile.feed.addscore

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import javax.inject.Inject

@HiltViewModel
class AddScoreViewModel @Inject constructor(): ViewModel() {

	private val _uiStateFlow = MutableStateFlow(
		AddScoreUiState(null, false)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	fun onOptionPicked(pickedOption: Int?){
		if (pickedOption == null) return

		_uiStateFlow.value = _uiStateFlow.value.copy(pickedOption = pickedOption, nextButtonEnabled = true)
	}

	fun onNextButtonClicked(navigationCallback: () -> Unit){
		navigationCallback.invoke()
	}
}