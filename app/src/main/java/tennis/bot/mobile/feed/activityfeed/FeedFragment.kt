package tennis.bot.mobile.feed.activityfeed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentFeedBottomNavigationBinding
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : AuthorizedCoreFragment<FragmentFeedBottomNavigationBinding>() {
	override val bindingInflation: Inflation<FragmentFeedBottomNavigationBinding> = FragmentFeedBottomNavigationBinding::inflate
	private val viewModel: FeedViewModel by viewModels()
	@Inject
	lateinit var adapter: FeedAdapter

	companion object {
		const val ADD_SCORE_INDEX = 0
		const val CREATE_GAME_ITEM = 1
		const val LIKE = "LIKE"
		const val UNLIKE = "UNLIKE"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = adapter
		binding.container.itemAnimator = null
		binding.container.layoutManager = LinearLayoutManager(context)

		adapter.clickListener = { command, postId ->
			when(command) {
				LIKE -> {
					viewModel.onLikeButtonPressed(true, postId)
				}
				UNLIKE -> {
					viewModel.onLikeButtonPressed(false, postId)
				}
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: FeedUiState ->
			adapter.submitList(uiState.postItems)
		}
	}
}