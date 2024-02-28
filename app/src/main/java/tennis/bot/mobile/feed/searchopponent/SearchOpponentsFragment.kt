package tennis.bot.mobile.feed.searchopponent

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.DefaultLoadStateAdapter
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSearchOpponentBinding
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.feed.insertscore.InsertScoreFragment
import tennis.bot.mobile.utils.LetterInputFilter
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject

@AndroidEntryPoint
class SearchOpponentsFragment : CoreFragment<FragmentSearchOpponentBinding>() {
	override val bindingInflation: Inflation<FragmentSearchOpponentBinding> = FragmentSearchOpponentBinding::inflate
	private val viewModel: SearchOpponentsViewModel by viewModels()
	@Inject
	lateinit var adapter: SearchOpponentsAdapter

	companion object {
		const val SCORE_TYPE_REQUEST_KEY = "SCORE_TYPE_REQUEST_KEY"
		const val SELECTED_SCORE_TYPE_OPTION = "SELECTED_SCORE_TYPE_OPTION"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.searchBarEt.filters = arrayOf(LetterInputFilter())
		binding.searchBarEt.doAfterTextChanged { text ->
			binding.opponentsContainer.visibility = View.VISIBLE
			binding.cardsAnimation.visibility = View.GONE
			binding.hintTitle.visibility = View.GONE
			binding.hintText.visibility = View.GONE

			viewModel.onSearchOpponentsInput(text ?: "")
		}

		binding.opponentsContainer.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.opponentsContainer.layoutManager = LinearLayoutManager(requireContext())

		adapter.clickListener = { opponent ->
			Log.d("123546", "Recieved $opponent")
			binding.buttonNext.isEnabled = true
			val buttonBackground = if (binding.buttonNext.isEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonNext.setBackgroundResource(buttonBackground)
			viewModel.onOpponentPicked(opponent, requireActivity())
		}

		binding.buttonNext.setOnClickListener {
			parentFragmentManager.traverseToAnotherFragment(InsertScoreFragment())
		}

		setFragmentResultListener(SCORE_TYPE_REQUEST_KEY) { _, result ->
			when(result.getInt(SELECTED_SCORE_TYPE_OPTION)) { // there will be different logic and ui changes for each option
				AddScoreFragment.SCORE_SINGLE -> {}
				AddScoreFragment.SCORE_DOUBLE -> {}
				AddScoreFragment.SCORE_TOURNAMENT -> {}
				AddScoreFragment.SCORE_FRIENDLY -> {}
			}
		}

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.userInput.collectLatest {
				viewModel.opponentsPager.collectLatest {
					adapter.submitData(it)
				}
			}
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SearchOpponentsUiState -> // may be useless
			when(uiState){
				is SearchOpponentsUiState.Initial -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
				}
				is SearchOpponentsUiState.Loading -> {
					binding.errorLayout.visibility = View.GONE
					binding.loadingBar.visibility = View.VISIBLE
					binding.opponentsContainer.visibility = View.VISIBLE
				}
				is SearchOpponentsUiState.OpponentDataReceived -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
					binding.opponentsContainer.visibility = View.VISIBLE
				}
				is SearchOpponentsUiState.Error -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.VISIBLE
					binding.opponentsContainer.visibility = View.GONE
				}
			}
		}
	}

}