package tennis.bot.mobile.onboarding.sport

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SportViewModel @Inject constructor(): ViewModel() {

	val sportItemsList = listOf(
		SportItem("Пинг-понг"),
		SportItem("Падл"),
		SportItem("Сквош"),
		SportItem("Фитнес"),
		SportItem("Бадминтон"),
	)
}