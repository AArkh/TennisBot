package tennis.bot.mobile.profile.matches

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
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.DefaultLoadStateAdapter
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentMatchesBinding
import javax.inject.Inject

@AndroidEntryPoint
class MatchesFragment : CoreFragment<FragmentMatchesBinding>() {
	override val bindingInflation: Inflation<FragmentMatchesBinding> = FragmentMatchesBinding::inflate
	@Inject
	lateinit var matchesAdapter: MatchesAdapter
	private val viewModel: MatchesViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.matchesContainer.adapter = matchesAdapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { matchesAdapter.retry() },
			footer = DefaultLoadStateAdapter { matchesAdapter.retry() }
		)
		binding.matchesContainer.layoutManager = LinearLayoutManager(requireContext())

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.allMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.allMatchesButton, binding.wonMatchesButton, binding.lostMatchesButton)
		}

		binding.wonMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.wonMatchesButton, binding.allMatchesButton, binding.lostMatchesButton)
		}

		binding.lostMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.lostMatchesButton, binding.wonMatchesButton, binding.allMatchesButton)
		}

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.getMatchesNew().collectLatest {
				matchesAdapter.submitData(it)
			}
		}

		matchesAdapter.addLoadStateListener { loadState ->
			binding.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading

		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: MatchesUiState ->
			when(uiState){
				is MatchesUiState.Loading -> {
					binding.errorLayout.visibility = View.GONE
					binding.loadingBar.visibility = View.VISIBLE
					binding.matchesContainer.visibility = View.VISIBLE
				}
				is MatchesUiState.MatchesDataReceived -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
					binding.matchesContainer.visibility = View.VISIBLE
				}
				is MatchesUiState.Error -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.VISIBLE
					binding.matchesContainer.visibility = View.GONE
				}
			}
		}
	}

}