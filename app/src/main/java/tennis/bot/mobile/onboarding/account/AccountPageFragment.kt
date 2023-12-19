package tennis.bot.mobile.onboarding.account

import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAccountPageBinding

@AndroidEntryPoint
class AccountPageFragment() : CoreFragment<FragmentAccountPageBinding>() {
	override val bindingInflation: Inflation<FragmentAccountPageBinding> = FragmentAccountPageBinding::inflate



}