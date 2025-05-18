package tennis.bot.mobile.feed.activityfeed

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentFeedBottomNavigationBinding
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.profile.account.AccountPageFragment
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.goToAnotherSectionFragment
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject

@AndroidEntryPoint
class FeedBottomNavigationFragment : CoreFragment<FragmentFeedBottomNavigationBinding>() {
	override val bindingInflation: Inflation<FragmentFeedBottomNavigationBinding> = FragmentFeedBottomNavigationBinding::inflate
	private val viewModel: FeedBottomNavigationViewModel by viewModels()
	@Inject
	lateinit var adapter: FeedAdapter

	companion object {
		const val ADD_SCORE_INDEX = 0
		const val CREATE_GAME_ITEM = 1
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = adapter
		binding.container.layoutManager = LinearLayoutManager(context)

		binding.playerPhoto.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
				.replace(R.id.fragment_container_view, AccountPageFragment())
				.addToBackStack(AccountPageFragment::class.java.name)
				.commit()
		}

		binding.addScoreButton.setOnClickListener {
			showAddScorePopup(it, viewModel.addScoreOptions)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: FeedBottomNavigationUiState ->
			binding.playerPhoto.setImage(AvatarImage(uiState.playerPicture))
			binding.playerPhoto.drawableSize = requireContext().dpToPx(32)
			adapter.submitList(uiState.postItems)
		}
	}

	private fun showAddScorePopup(view: View, items: List<String>) {
		val menu = PopupMenu(requireContext(), view, Gravity.END, 0, R.style.PopupMenuStyle)
		for (item in items) {
			menu.menu.add(item)
		}
		menu.setOnMenuItemClickListener { menuItem ->
			when(items.indexOf(menuItem.title.toString())) {
				ADD_SCORE_INDEX -> {
					parentFragmentManager.goToAnotherSectionFragment(AddScoreFragment())
				}
				CREATE_GAME_ITEM -> {}
				else -> {}
			}
			true
		}
		menu.show()
	}

}