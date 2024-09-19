package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentGameTabBinding
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.FRAGMENT_TYPE_NUMBER
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.FRAGMENT_TYPE_REQUEST_KEY
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.PLAYERS_NUMBER
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.REQUESTS_NUMBER

@AndroidEntryPoint
class GameTabFragment : AuthorizedCoreFragment<FragmentGameTabBinding>() {

	override val bindingInflation: Inflation<FragmentGameTabBinding> = FragmentGameTabBinding::inflate
	private lateinit var pagerAdapter: GameTabPager

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		pagerAdapter = GameTabPager(parentFragmentManager, lifecycle)
		binding.viewPager.adapter = pagerAdapter
		binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
			override fun onPageSelected(position: Int) {
				super.onPageSelected(position)

				requireActivity().supportFragmentManager.setFragmentResult(
					FRAGMENT_TYPE_REQUEST_KEY,
					bundleOf(
						FRAGMENT_TYPE_NUMBER to if (position == 0) REQUESTS_NUMBER else PLAYERS_NUMBER
					)
				)
			}
		})
		TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
			tab.text = when (position) {
				0 -> getString(R.string.requests_title)
				else -> getString(R.string.players_title)
			}
		}.attach()
	}
}

class GameTabPager(
	fragmentManager: FragmentManager,
	lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
	override fun getItemCount(): Int = 2

	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> GameFragment()
			else -> PlayersFragment()
		}
	}
}