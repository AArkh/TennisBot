package tennis.bot.mobile.feed.searchopponent

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSearchOpponentBinding
import tennis.bot.mobile.utils.LetterInputFilter
import javax.inject.Inject

@AndroidEntryPoint
class SearchOpponentsFragment : CoreFragment<FragmentSearchOpponentBinding>() {
	override val bindingInflation: Inflation<FragmentSearchOpponentBinding> = FragmentSearchOpponentBinding::inflate
	private val viewModel: SearchOpponentsViewModel by viewModels()
	@Inject
	lateinit var adapter: SearchOpponentsAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.searchBarEt.filters = arrayOf(LetterInputFilter())
		binding.searchBarEt.doAfterTextChanged { text ->
			viewModel.onSearchOpponentsInput(text ?: "")
		}

		binding.matchesContainer.adapter = adapter
		binding.matchesContainer.layoutManager = LinearLayoutManager(requireContext())

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.userInput.collectLatest {
				viewModel.opponentsPager.collectLatest {
					adapter.submitData(it)
				}
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SearchOpponentsUiState -> // may be useless, todo check out the way to add loading and catch error states in PagingLibrary
			when(uiState){
				is SearchOpponentsUiState.Initial -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
				}
				is SearchOpponentsUiState.Loading -> {
					binding.errorLayout.visibility = View.GONE
					binding.loadingBar.visibility = View.VISIBLE
					binding.matchesContainer.visibility = View.VISIBLE
				}
				is SearchOpponentsUiState.OpponentDataReceived -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
					binding.matchesContainer.visibility = View.VISIBLE
				}
				is SearchOpponentsUiState.Error -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.VISIBLE
					binding.matchesContainer.visibility = View.GONE
				}

			}
		}
	}

}