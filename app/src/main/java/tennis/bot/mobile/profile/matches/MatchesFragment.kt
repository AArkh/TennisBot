package tennis.bot.mobile.profile.matches

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAccountPageBinding
import tennis.bot.mobile.databinding.FragmentMatchesBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.AccountPageViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MatchesFragment : CoreFragment<FragmentMatchesBinding>() {
	override val bindingInflation: Inflation<FragmentMatchesBinding> = FragmentMatchesBinding::inflate
	@Inject
	lateinit var matchesAdapter: MatchesAdapter
	private val viewModel: MatchesViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)




	}

}