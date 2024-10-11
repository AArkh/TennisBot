package tennis.bot.mobile.feed.notifications

import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.feed.game.GameRepository
import tennis.bot.mobile.feed.game.GameViewModel
import javax.inject.Inject

@HiltViewModel
class ActionableNotificationViewModel @Inject constructor(
	private val gameRepository: GameRepository,
	private val notificationsRepository: NotificationsRepository,
	@ApplicationContext private val context: Context
): GameViewModel(gameRepository, notificationsRepository, context) {
}