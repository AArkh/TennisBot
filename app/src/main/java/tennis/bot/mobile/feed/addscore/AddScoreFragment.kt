package tennis.bot.mobile.feed.addscore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAddScoreBinding
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment
import tennis.bot.mobile.utils.showToast

@AndroidEntryPoint
class AddScoreFragment : CoreFragment<FragmentAddScoreBinding>() {
	override val bindingInflation: Inflation<FragmentAddScoreBinding> = FragmentAddScoreBinding::inflate
	private val viewModel: AddScoreViewModel by viewModels()

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

		binding.option1.setOnClickListener { viewModel.onOptionPicked(requireActivity(),SCORE_SINGLE) }
		binding.option2.setOnClickListener { context?.showToast("Still in development") }
		binding.option3.setOnClickListener { context?.showToast("Still in development") }
		binding.option4.setOnClickListener { context?.showToast("Still in development") }

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked {
				parentFragmentManager.beginTransaction()
					.replace(R.id.fragment_container_view, SearchOpponentsFragment())
					.addToBackStack(SearchOpponentsFragment::class.java.name)
					.commit()
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			onPickedActive(uiState.pickedOption)
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