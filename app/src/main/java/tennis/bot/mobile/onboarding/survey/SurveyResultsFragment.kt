package tennis.bot.mobile.onboarding.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSurveyBinding
import tennis.bot.mobile.databinding.FragmentSurveyResultsBinding
import javax.inject.Inject

@AndroidEntryPoint
class SurveyResultsFragment : CoreFragment<FragmentSurveyResultsBinding>() {
	override val bindingInflation: Inflation<FragmentSurveyResultsBinding> = FragmentSurveyResultsBinding::inflate

	@Inject
	lateinit var surveyResultsAdapter: SurveyResultsAdapter
	private val viewModel: SurveyResultsViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.answersRecycler.adapter = surveyResultsAdapter
		binding.answersRecycler.layoutManager = LinearLayoutManager(requireContext())

		binding.buttonTryAgain.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.replace(R.id.fragment_container_view, SurveyFragment())
				.addToBackStack(SurveyFragment::class.java.name)
				.commit()
		}

		binding.buttonContinue.setOnClickListener {
			// endpoint logic + whatever we decide to do next
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SurveyResultsUiState ->
			when (uiState) {
				is SurveyResultsUiState.InitialWithAnswers -> {
					surveyResultsAdapter.submitList(uiState.answers)
				}
				SurveyResultsUiState.SendingPost -> {}
				SurveyResultsUiState.Error -> {}
			}
		}
	}
}