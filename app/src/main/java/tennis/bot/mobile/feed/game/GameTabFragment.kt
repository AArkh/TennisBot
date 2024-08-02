package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentGameTabBinding

@AndroidEntryPoint
class GameTabFragment : AuthorizedCoreFragment<FragmentGameTabBinding>() {

	override val bindingInflation: Inflation<FragmentGameTabBinding> = FragmentGameTabBinding::inflate
	private lateinit var pagerAdapter: GameTabPager

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		pagerAdapter = GameTabPager(childFragmentManager, lifecycle)
		binding.viewPager.adapter = pagerAdapter
		TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
			tab.text = when (position) {
				0 -> "GAME"
				1 -> "PLAYERS"
				else -> null
			}
		}.attach()
	}

}

class GameTabPager(fragmentManager: FragmentManager, lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
	override fun getItemCount(): Int {
		return 2
	}

	override fun createFragment(position: Int): Fragment {
		return when (position) {
			0 -> GameFragment()
			1 -> PlayersFragment()
			else -> throw IllegalArgumentException("Invalid position")
		}
	}
}