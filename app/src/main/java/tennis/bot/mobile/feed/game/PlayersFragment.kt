package tennis.bot.mobile.feed.game

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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import tennis.bot.mobile.core.DefaultLoadStateAdapter
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentPlayersBinding
import tennis.bot.mobile.feed.requestcreation.RequestCreationDeniedDialog
import tennis.bot.mobile.utils.basicdialog.BasicDialogViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PlayersFragment: AuthorizedCoreFragment<FragmentPlayersBinding>() {

	override val bindingInflation: Inflation<FragmentPlayersBinding> = FragmentPlayersBinding::inflate
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
			viewModel.playersPager.debounce(300).collectLatest {
				adapter.submitData(it)
			}
		}

		adapter.clickListener = { command, opponentItem ->
			when(command) {
				GameAdapter.REQUEST_RESPONSE -> {
					viewModel.checkPermissionToInvite(opponentItem) { isPermitted ->
						if (isPermitted) {
							val bottomDialog = GameOrderResponseDialogFragment()
							bottomDialog.arguments = bundleOf(
								GameFragment.GAME_ORDER_ID to opponentItem.id
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
				targetPlayerId = result.getLong(GameFragment.GAME_ORDER_ID),
				comment = result.getString(GameFragment.GAME_ORDER_COMMENT)
			) {
				binding.container.post {
					adapter.updateInviteUi(it)
				}
			}
		}

		setFragmentResultListener(PLAYERS_SEARCH_BAR_REQUEST_KEY) { _, result ->
			result.getString(PLAYERS_SEARCH_BAR_QUERY)
				?.let { viewModel.updateSearchInput(it) }
				.also { adapter.refresh() }
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
		}
	}

	companion object {
		const val PLAYERS_SEARCH_BAR_REQUEST_KEY = "PLAYERS_SEARCH_BAR_REQUEST_KEY"
		const val PLAYERS_SEARCH_BAR_QUERY= "PLAYERS_SEARCH_BAR_QUERY"
	}

}