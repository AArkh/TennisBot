package tennis.bot.mobile.feed.game

import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentFeedBottomNavigationBinding

class PlayersFragment: AuthorizedCoreFragment<FragmentFeedBottomNavigationBinding>() {

	override val bindingInflation: Inflation<FragmentFeedBottomNavigationBinding> = FragmentFeedBottomNavigationBinding::inflate
}