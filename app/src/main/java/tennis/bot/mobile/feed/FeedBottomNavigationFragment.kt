package tennis.bot.mobile.feed

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentFeedBottomNavigationBinding
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.AccountPageFragment
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.utils.dpToPx

@AndroidEntryPoint
class FeedBottomNavigationFragment : CoreFragment<FragmentFeedBottomNavigationBinding>() {
	override val bindingInflation: Inflation<FragmentFeedBottomNavigationBinding> = FragmentFeedBottomNavigationBinding::inflate
	private val viewModel: FeedBottomNavigationViewModel by viewModels()

	companion object {
		const val ADD_SCORE_INDEX = 0
		const val CREATE_GAME_ITEM = 1
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.playerPhoto.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.replace(R.id.fragment_container_view, AccountPageFragment())
				.addToBackStack(AccountPageFragment::class.java.name)
				.commit()
		}

		binding.addScoreButton.setOnClickListener {
			showAddScorePopup(it, viewModel.addScoreOptions)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: FeedBottomNavigationUiState ->
			loadProfilePicture(uiState.playerPicture)
		}
	}

	private fun loadProfilePicture(profileImageUrl: String?) {
		if (profileImageUrl == null) return

		binding.playerImage.load(R.drawable.user) { crossfade(true) }
		binding.playerPhoto.setPadding(binding.playerPhoto.context.dpToPx(10))

		if (profileImageUrl.contains("default")) {
			val resourceId = getDefaultDrawableResourceId(binding.playerImage.context, profileImageUrl.removeSuffix(".png"))
			if (resourceId != null) binding.playerImage.load(resourceId)
			binding.playerPhoto.setPadding(0)
		} else {
			binding.playerImage.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl) { crossfade(true) }
			binding.playerPhoto.setPadding(0)
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
					parentFragmentManager.beginTransaction()
						.replace(R.id.fragment_container_view, AddScoreFragment())
						.addToBackStack(AddScoreFragment::class.java.name)
						.commit()
				}
				CREATE_GAME_ITEM -> {}
				else -> {}
			}
			true
		}
		menu.show()
	}

}