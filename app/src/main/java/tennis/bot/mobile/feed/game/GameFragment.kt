package tennis.bot.mobile.feed.game

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentGameBinding
import tennis.bot.mobile.utils.animateButtonTransition
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : CoreFragment<FragmentGameBinding>() {

	override val bindingInflation: Inflation<FragmentGameBinding> = FragmentGameBinding::inflate
	private val viewModel: GameViewModel by viewModels()
	@Inject
	lateinit var adapter: GameAdapter


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = adapter
		binding.container.layoutManager = LinearLayoutManager(context)

		binding.tabRequests.setOnClickListener {
			onTabClick(binding.tabRequests, binding.underline)
		}

		binding.tabPlayers.setOnClickListener {
			onTabClick(binding.tabPlayers, binding.underline)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			adapter.submitList(uiState.itemsList)
		}

	}

	private fun onTabClick(view: View, underline: View) {
		val selectedTab = view as TextView
		val otherTab = if (selectedTab === binding.tabRequests) binding.tabPlayers else binding.tabRequests

		selectedTab.setTextColor(requireContext().getColor(R.color.tb_black))
		otherTab.setTextColor(requireContext().getColor(R.color.tb_gray_gray))

		animateButtonTransition(underline, selectedTab)
	}

}