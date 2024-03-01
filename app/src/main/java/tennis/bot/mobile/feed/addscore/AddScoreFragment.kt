package tennis.bot.mobile.feed.addscore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAddScoreBinding
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment
import tennis.bot.mobile.onboarding.survey.SurveyResultsAdapter
import tennis.bot.mobile.utils.showToast
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject

@AndroidEntryPoint
class AddScoreFragment : CoreFragment<FragmentAddScoreBinding>() {
	override val bindingInflation: Inflation<FragmentAddScoreBinding> = FragmentAddScoreBinding::inflate
	private val viewModel: AddScoreViewModel by viewModels()
	@Inject
	lateinit var sideNoteAdapter: AddScoreSideNoteAdapter

	companion object {
		const val SCORE_SINGLE = 1
		const val SCORE_DOUBLE = 2
		const val SCORE_TOURNAMENT = 3
		const val SCORE_FRIENDLY = 4
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.sideNoteContainer.adapter = sideNoteAdapter
		sideNoteAdapter.submitList(viewModel.sideNoteItems)

		binding.option1.setOnClickListener {
			binding.sideNoteContainer.visibility = View.GONE
			viewModel.onOptionPicked(requireActivity(), SCORE_SINGLE)
		}
		binding.option2.setOnClickListener {
			binding.sideNoteContainer.visibility = View.GONE
			viewModel.onOptionPicked(requireActivity(), SCORE_DOUBLE)
		}
		binding.option3.setOnClickListener { context?.showToast("Still in development") }
		binding.option4.setOnClickListener { context?.showToast("Still in development") }

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked {
				parentFragmentManager.traverseToAnotherFragment(SearchOpponentsFragment())
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			onPickedActive(uiState.pickedOption)
			binding.sideNoteTitle.text = uiState.sideNoteTitle
			binding.sideNoteText.text = uiState.sideNoteText

			binding.sideNoteContainer.isVisible = uiState.sideNoteContainer
			binding.buttonNext.isEnabled = uiState.nextButtonEnabled
			val buttonBackground = if (uiState.nextButtonEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonNext.setBackgroundResource(buttonBackground)
		}
	}

	private fun onPickedActive(pickedOption: Int?) {
		if (pickedOption == null) return

		binding.option1.setBackgroundResource(R.drawable.survey_option_outline)
		binding.option3.setBackgroundResource(R.drawable.survey_option_outline)
		binding.option4.setBackgroundResource(R.drawable.survey_option_outline)
		binding.option2.setBackgroundResource(R.drawable.survey_option_outline)

		when(pickedOption) {
			SCORE_SINGLE -> binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
			SCORE_DOUBLE -> binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
			SCORE_TOURNAMENT -> binding.option3.setBackgroundResource(R.drawable.survey_option_outline_picked)
			SCORE_FRIENDLY -> binding.option4.setBackgroundResource(R.drawable.survey_option_outline_picked)
		}
	}


}