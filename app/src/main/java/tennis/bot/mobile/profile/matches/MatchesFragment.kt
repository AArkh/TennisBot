package tennis.bot.mobile.profile.matches

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
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

		binding.matchesContainer.adapter = matchesAdapter
		binding.matchesContainer.layoutManager = LinearLayoutManager(requireContext())

		binding.allMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.allMatchesButton, binding.wonMatchesButton, binding.lostMatchesButton)
		}

		binding.wonMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.wonMatchesButton, binding.allMatchesButton, binding.lostMatchesButton)
		}

		binding.lostMatchesButton.setOnClickListener{
			viewModel.onOptionClicked(buttonClicked = binding.lostMatchesButton, binding.wonMatchesButton, binding.allMatchesButton)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: MatchesUiState ->
			when(uiState){
				is MatchesUiState.Loading -> {
					binding.errorLayout.visibility = View.GONE
					binding.matchesContainer.visibility = View.VISIBLE
					binding.loadingBar.visibility = View.VISIBLE
					viewModel.onFetchingMatches()

				}
				is MatchesUiState.MatchesDataReceived -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
					binding.matchesContainer.visibility = View.VISIBLE
					matchesAdapter.submitList(uiState.matchesList)

				}
				is MatchesUiState.Error -> {
					binding.loadingBar.visibility = View.GONE
					binding.matchesContainer.visibility = View.GONE
					binding.errorLayout.visibility = View.VISIBLE
				}
			}
		}
	}

}