package tennis.bot.mobile.feed.activityfeed

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tennis.bot.mobile.core.DefaultLoadStateAdapter
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
		const val ADD_SCORE_INDEX = 0 // todo
		const val CREATE_GAME_ITEM = 1 // todo
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

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.getFeedPaginationFlow().collectLatest {
				hasData = true
				adapter.submitData(it)
			}
		}

		binding.swipeRefreshLayout.setOnRefreshListener {
			adapter.refresh()
//			binding.swipeRefreshLayout.isRefreshing = false
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			if (hasData) {
				binding.swipeRefreshLayout.isRefreshing = true
				binding.loadingBar.isVisible = false
			} else {
				binding.swipeRefreshLayout.isRefreshing = false
				binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
			}
		}
	}

	private var hasData = false // todo что-то такое, наверное
}