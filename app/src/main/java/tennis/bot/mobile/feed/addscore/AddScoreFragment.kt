package tennis.bot.mobile.feed.addscore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAddScoreBinding

@AndroidEntryPoint
class AddScoreFragment : CoreFragment<FragmentAddScoreBinding>() {
	override val bindingInflation: Inflation<FragmentAddScoreBinding> = FragmentAddScoreBinding::inflate
	private val viewModel: AddScoreViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.option1.setOnClickListener { viewModel.onOptionPicked(1) }
		binding.option2.setOnClickListener { viewModel.onOptionPicked(2) }
		binding.option3.setOnClickListener { viewModel.onOptionPicked(3) }
		binding.option4.setOnClickListener { viewModel.onOptionPicked(4) }

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked {
				// go whatever
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
			1 -> binding.option1.setBackgroundResource(R.drawable.survey_option_outline_picked)
			2 -> binding.option2.setBackgroundResource(R.drawable.survey_option_outline_picked)
			3 -> binding.option3.setBackgroundResource(R.drawable.survey_option_outline_picked)
			4 -> binding.option4.setBackgroundResource(R.drawable.survey_option_outline_picked)
		}
	}


}