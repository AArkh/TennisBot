package tennis.bot.mobile.feed.game

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
class PlayersFragment: AuthorizedCoreFragment<FragmentFeedBottomNavigationBinding>() {

	override val bindingInflation: Inflation<FragmentFeedBottomNavigationBinding> = FragmentFeedBottomNavigationBinding::inflate
	private val viewModel: PlayersViewModel by viewModels()
	@Inject
	lateinit var adapter: PlayersAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.container.itemAnimator = null
		binding.container.layoutManager = LinearLayoutManager(context)

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.playersPager.collectLatest {
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