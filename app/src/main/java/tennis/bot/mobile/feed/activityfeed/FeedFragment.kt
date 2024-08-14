package tennis.bot.mobile.feed.activityfeed

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.DefaultLoadStateAdapter
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentFeedBottomNavigationBinding
import tennis.bot.mobile.feed.game.GameAdapter
import tennis.bot.mobile.feed.game.GameFragment
import tennis.bot.mobile.feed.game.GameOrderResponseDialogFragment
import tennis.bot.mobile.utils.showToast
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

		binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.container.itemAnimator = null
		binding.container.layoutManager = LinearLayoutManager(context)

		adapter.clickListener = { command, postId, playerId ->
			when (command) {
				LIKE -> {
					viewModel.onLikeButtonPressed(true, postId)
				}

				UNLIKE -> {
					viewModel.onLikeButtonPressed(false, postId)
				}
				GameAdapter.REQUEST_RESPONSE -> {
					if (!viewModel.checkIfRequestIsYours(playerId ?: 0)) {
						val bottomDialog = GameOrderResponseDialogFragment()
						bottomDialog.arguments = bundleOf(
							GameFragment.GAME_ORDER_ID to postId
						)
						bottomDialog.show(childFragmentManager, bottomDialog.tag)
					} else {
						requireContext().showToast(getString(R.string.response_to_own_request))
					}
				}
			}
		}

		setFragmentResultListener(GameFragment.GAME_ORDER_RESPONSE_KEY) { _, result ->
			viewModel.onSendingRequestResponse(
				id = result.getLong(GameFragment.GAME_ORDER_ID),
				comment = result.getString(GameFragment.GAME_ORDER_COMMENT),
				requireContext()
			)
		}

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.getFeedPaginationFlow().collectLatest {
				adapter.submitData(it)
			}
		}

		binding.swipeRefreshLayout.setOnRefreshListener {
			adapter.refresh()
			binding.swipeRefreshLayout.isRefreshing = false
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
		}
	}
}