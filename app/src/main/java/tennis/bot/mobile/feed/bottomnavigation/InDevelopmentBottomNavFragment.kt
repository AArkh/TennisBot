package tennis.bot.mobile.feed.bottomnavigation

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentInDevelopmentBottomNavBinding
import tennis.bot.mobile.profile.account.InDevelopmentDialog


class InDevelopmentBottomNavFragment : CoreFragment<FragmentInDevelopmentBottomNavBinding>() {
	override val bindingInflation: Inflation<FragmentInDevelopmentBottomNavBinding> = FragmentInDevelopmentBottomNavBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val dialog = InDevelopmentDialog()
		dialog.show(childFragmentManager, dialog.tag)
	}
}