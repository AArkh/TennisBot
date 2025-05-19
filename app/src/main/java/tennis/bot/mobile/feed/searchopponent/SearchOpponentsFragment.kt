package tennis.bot.mobile.feed.searchopponent

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
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
import tennis.bot.mobile.feed.insertscore.InsertScoreFragment
import tennis.bot.mobile.utils.LetterInputFilter
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject

@AndroidEntryPoint
open class SearchOpponentsFragment : CoreFragment<FragmentSearchOpponentBinding>() {
	override val bindingInflation: Inflation<FragmentSearchOpponentBinding> = FragmentSearchOpponentBinding::inflate
	private val viewModel: SearchOpponentsViewModel by viewModels()
	@Inject
	lateinit var adapter: SearchOpponentsAdapter
	override var adjustToKeyboard: Boolean = true

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
			if(text?.isNotEmpty() == true) {
				onContainerVisibility(isVisible = true)
				viewModel.onSearchOpponentsInput(text)
			}
		}

		binding.opponentsContainer.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.opponentsContainer.layoutManager = LinearLayoutManager(requireContext())

		adapter.clickListener = { opponent ->
			Log.d("123546", "Recieved $opponent")
			viewModel.onOpponentPicked(opponent)

			if (viewModel.uiStateFlow.value.numberOfOpponents > 1) {
				binding.imageSeriesView.isVisible = true
				binding.searchBarEt.text = null // for some reason want Editable?
				viewModel.uiStateFlow.value.photosList?.let { binding.imageSeriesView.setImages(it, 0) }
				if (!viewModel.uiStateFlow.value.isNextButtonEnabled) {
					adapter.clearOutlinePosition()
					onContainerVisibility(isVisible = false)
				}
			}
		}

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked(requireActivity()) {
				parentFragmentManager.traverseToAnotherFragment(InsertScoreFragment())
			}
		}

		setFragmentResultListener(SCORE_TYPE_REQUEST_KEY) { _, result ->
			viewModel.onReceivingScoreType(result.getInt(SELECTED_SCORE_TYPE_OPTION))
		}

		viewModel.viewModelScope.launch(Dispatchers.IO) {
			viewModel.userInput.collectLatest {
				adapter.clearOutlinePosition()
				viewModel.opponentsPager.collectLatest {
					adapter.submitData(it)
				}
			}
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SearchOpponentsUiState ->
			binding.hintTitle.text = uiState.hintTitle
			binding.buttonNext.isEnabled = uiState.isNextButtonEnabled
			val buttonBackground = if (binding.buttonNext.isEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonNext.setBackgroundResource(buttonBackground)

		}
	}

	private fun onContainerVisibility(isVisible: Boolean) {
		if(isVisible) {
			binding.opponentsContainer.visibility = View.VISIBLE
			binding.cardsAnimation.visibility = View.GONE
			binding.hintTitle.visibility = View.GONE
			binding.hintText.visibility = View.GONE
		} else {
			binding.opponentsContainer.visibility = View.GONE
			binding.cardsAnimation.visibility = View.VISIBLE
			binding.hintTitle.visibility = View.VISIBLE
			binding.hintText.visibility = View.VISIBLE
		}
	}

}