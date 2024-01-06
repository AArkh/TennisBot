package tennis.bot.mobile.onboarding.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAccountPageBinding
import javax.inject.Inject

@AndroidEntryPoint
class AccountPageFragment : CoreFragment<FragmentAccountPageBinding>() {
	@Inject
	lateinit var accountPageAdapter: AccountPageAdapter

	override val bindingInflation: Inflation<FragmentAccountPageBinding> = FragmentAccountPageBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = accountPageAdapter
		binding.container.layoutManager = LinearLayoutManager(requireContext())

		accountPageAdapter.submitList(listOf( // todo replace int's with const or whatever suitable
			BasicInfoAndRating("", "test test", "testId", "1920", "1080"),
			Calibration(
				9,
				requireContext().getString(R.string.calibration_matches_remain, 1),
				requireContext().getString(R.string.calibration_rounds_remain_text, 9)),
			MatchesPlayed(
				requireContext().getString(R.string.account_matches_played, 1),
				requireContext().getString(R.string.last_game_date, "6 Января 2024") ),
			PointsAndPosition(
				requireContext().getString(R.string.account_tournament_points, 99),
				requireContext().getString(R.string.tournament_title), // should get it elsewhere
				3.toString() ),
			Tournaments(requireContext().getString(R.string.tournament_title) ),
			Friends(
				requireContext().getString(R.string.tournament_title),
				null, null, null, null,
				false),
			ButtonSwitch(true)
			))





	}
}