package tennis.bot.mobile.profile.appsetttings

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import tennis.bot.mobile.R
import tennis.bot.mobile.profile.editprofile.EditProfileItem
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
	@ApplicationContext private val context: Context
): ViewModel() {

	private val appSettingsItems = listOf(
		EditProfileItem(icon = R.drawable.bell, title = context.getString(R.string.notification_title), isRadioButton = true),
		EditProfileItem(icon = R.drawable.mail, title = context.getString(R.string.contact_email)),
		EditProfileItem(icon = R.drawable.telegram, title = context.getString(R.string.contact_telegram), noUnderline = true),
	)

	private val _uiStateFlow = MutableStateFlow(
		AppSettingsUiState(
			appSettingsItems
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()
}