package tennis.bot.mobile.feed.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import tennis.bot.mobile.feed.game.GameAdapter
import javax.inject.Inject

@AndroidEntryPoint
class ActionableNotificationFragment: AuthorizedCoreFragment<FragmentRequestBinding>() {
	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate
	@Inject
	lateinit var adapter: GameAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonCreate.isVisible = false
		binding.title.text = getString(R.string.notification_title) // outcoming or incoming
	}
}