package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
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
import tennis.bot.mobile.databinding.FragmentGameBinding
import tennis.bot.mobile.feed.insertscore.InsertScoreFragment
import tennis.bot.mobile.utils.animateButtonTransition
import tennis.bot.mobile.utils.showToast
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject

@AndroidEntryPoint
class GameFragment : AuthorizedCoreFragment<FragmentGameBinding>() {

	override val bindingInflation: Inflation<FragmentGameBinding> = FragmentGameBinding::inflate
	private val viewModel: GameViewModel by viewModels()
	@Inject
	lateinit var adapter: GameAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.container.layoutManager = LinearLayoutManager(context)
		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.onFetchingAllRequests().collectLatest {
				adapter.submitData(it)
			}
		}

		binding.allFilter.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				viewModel.onFetchingAllRequests().collectLatest {
					adapter.submitData(it)
				}
			}
			onFilterOptionClicked(buttonClicked = binding.allFilter)
		}

		binding.incomingFilter.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				viewModel.onFetchingIncomingRequests().collectLatest {
					adapter.submitData(it)
				}
			}
			onFilterOptionClicked(buttonClicked = binding.incomingFilter)
		}

		binding.outcomingFilter.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				viewModel.onFetchingOutcomingRequests().collectLatest {
					adapter.submitData(it)
				}
			}
			onFilterOptionClicked(buttonClicked = binding.outcomingFilter)
		}

		binding.acceptedFilter.setOnClickListener {
			lifecycleScope.launch(Dispatchers.IO) {
				viewModel.onFetchingAcceptedRequests().collectLatest {
					adapter.submitData(it)
				}
			}
			onFilterOptionClicked(buttonClicked = binding.acceptedFilter)
		}

		binding.tabRequests.setOnClickListener {
			onTabClick(binding.tabRequests, binding.underline)
		}

		binding.tabPlayers.setOnClickListener {
			onTabClick(binding.tabPlayers, binding.underline)
		}

		adapter.clickListener = { command, id ->
			when(command) {
				GameAdapter.REQUEST_OPTIONS_RESPONSE -> {
					showDeletePopup(binding.root.findViewById(R.id.options_dots), GameAdapter.REQUEST_OPTIONS_RESPONSE, id)
				}
				GameAdapter.REQUEST_OPTIONS_REQUEST -> {
					showDeletePopup(binding.root.findViewById(R.id.options_dots), GameAdapter.REQUEST_OPTIONS_REQUEST, id)
				}
				GameAdapter.REQUEST_RESPONSE -> {
					val bottomDialog = GameOrderResponseDialogFragment()
					bottomDialog.arguments = bundleOf(
						GAME_ORDER_ID to id,
					)
					bottomDialog.show(childFragmentManager, bottomDialog.tag)

				}
			}
		}
		adapter.insertScoreCallback = { listOfOpponents ->
			viewModel.onInsertScoreButtonClicked(requireActivity(), listOfOpponents) {
				parentFragmentManager.traverseToAnotherFragment(InsertScoreFragment())
				requireContext().showToast("InsertScore Callback")
			}
		}

		setFragmentResultListener(GAME_ORDER_RESPONSE_KEY) { _, result ->
			viewModel.onSendingRequestResponse(
				id = result.getLong(GAME_ORDER_ID),
				comment = result.getString(GAME_ORDER_COMMENT)
			)
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

	private fun onTabClick(view: View, underline: View) {
		val selectedTab = view as TextView
		val otherTab = if (selectedTab === binding.tabRequests) binding.tabPlayers else binding.tabRequests

		selectedTab.setTextColor(requireContext().getColor(R.color.tb_black))
		otherTab.setTextColor(requireContext().getColor(R.color.tb_gray_gray))

		animateButtonTransition(underline, selectedTab)
	}

	private fun onFilterOptionClicked(buttonClicked: TextView) {
		binding.allFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.invisible)
		binding.allFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.tb_gray_gray))
		binding.incomingFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.invisible)
		binding.incomingFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.tb_gray_gray))
		binding.outcomingFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.invisible)
		binding.outcomingFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.tb_gray_gray))
		binding.acceptedFilter.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.invisible)
		binding.acceptedFilter.setTextColor(ContextCompat.getColor(requireContext(), R.color.tb_gray_gray))

		buttonClicked.backgroundTintList = ContextCompat.getColorStateList(buttonClicked.context, R.color.tb_bg_card)
		buttonClicked.setTextColor(ContextCompat.getColor(buttonClicked.context, R.color.tb_black))
	}

	private fun showDeletePopup(view: View, command: String, id: Long) {
		val menu = PopupMenu(requireContext(), view, Gravity.BOTTOM, 0, R.style.PopupMenuStyle)

		when(command) {
			GameAdapter.REQUEST_OPTIONS_RESPONSE -> {
				menu.menu.add("Удалить отклик")
				menu.setOnMenuItemClickListener {
					viewModel.onDeletingMyGameResponse(adapter, id)
					true
				}
			}
			GameAdapter.REQUEST_OPTIONS_REQUEST -> {
				menu.menu.add("Удалить заявку")
				menu.setOnMenuItemClickListener {
					viewModel.onDeletingGameRequest(adapter, id)
					true
				}
			}
			else -> {}
		}
		menu.show()
	}

	companion object {
		const val GAME_ORDER_RESPONSE_KEY = "GAME_ORDER_RESPONSE_KEY"
		const val GAME_ORDER_ID = "GAME_ORDER_ID"
		const val GAME_ORDER_COMMENT = "GAME_ORDER_COMMENT"
	}
}