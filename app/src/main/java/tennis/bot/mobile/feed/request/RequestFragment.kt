package tennis.bot.mobile.feed.request

import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding

@AndroidEntryPoint
class RequestFragment : AuthorizedCoreFragment<FragmentRequestBinding>() {
	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate


}