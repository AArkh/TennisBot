package tennis.bot.mobile.onboarding.location

import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class LocationDialogViewModel @Inject constructor(
    private val repository: LocationRepo,
) : ViewModel(){

    private val _uiStateFlow = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()
}
