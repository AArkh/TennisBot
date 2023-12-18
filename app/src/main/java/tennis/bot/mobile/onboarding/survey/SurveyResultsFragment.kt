package tennis.bot.mobile.onboarding.survey

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
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

		binding.buttonTryAgain.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.replace(R.id.fragment_container_view, SurveyFragment())
				.addToBackStack(SurveyFragment::class.java.name)
				.commit()
		}

		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
			parentFragmentManager.beginTransaction()
				.remove(this@SurveyResultsFragment)
				.replace(R.id.fragment_container_view, SurveyFragment())
				.addToBackStack(SurveyFragment::class.java.name)
				.commit()
		}

		binding.buttonContinue.setOnClickListener {
			viewModel.onContinueButtonClicked {
				// there will be next screen logic here
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SurveyResultsUiState ->
			when (uiState) {
				is SurveyResultsUiState.InitialWithAnswers -> {
					binding.buttonLoadingAnim.visibility = View.INVISIBLE
					binding.buttonContinue.text = uiState.buttonContinueText
					surveyResultsAdapter.submitList(uiState.answers)
				}
				SurveyResultsUiState.Loading -> {
					binding.buttonContinue.text = ""
					binding.buttonLoadingAnim.visibility = View.VISIBLE
				}
				SurveyResultsUiState.Error -> {}
			}
		}
	}
}