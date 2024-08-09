package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
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
import tennis.bot.mobile.feed.requestcreation.RequestCreationDeniedDialog
import tennis.bot.mobile.feed.requestcreation.RequestCreationFragment
import tennis.bot.mobile.utils.basicdialog.BasicDialogViewModel
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

		adapter.clickListener = { command, targetPlayerId ->
			when(command) {
				GameAdapter.REQUEST_RESPONSE -> {
					viewModel.viewModelScope.launch {
						if (viewModel.checkPermission() == true) {
							val bottomDialog = GameOrderResponseDialogFragment()
							bottomDialog.arguments = bundleOf(
								GameFragment.TARGET_PLAYER_ID to targetPlayerId
							)
							bottomDialog.show(childFragmentManager, bottomDialog.tag)
						} else {
							val dialog = RequestCreationDeniedDialog()
							dialog.arguments = bundleOf(
								BasicDialogViewModel.SELECT_DIALOG_IS_ONE_BUTTON to true)
							dialog.show(childFragmentManager, dialog.tag)
						}
					}
				}
			}
		}
		setFragmentResultListener(GameFragment.GAME_ORDER_RESPONSE_KEY) { _, result ->
			viewModel.onSendingPlayerRequestResponse(
				targetPlayerId = result.getLong(GameFragment.TARGET_PLAYER_ID),
				comment = result.getString(GameFragment.GAME_ORDER_COMMENT)
			)
		}

		setFragmentResultListener(RequestCreationFragment.REQUEST_DENIED_DIALOG_KEY) { _, _ ->

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