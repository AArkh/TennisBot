package tennis.bot.mobile.onboarding.namegender

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentNameGenderBinding
import tennis.bot.mobile.onboarding.location.LocationFragment
import tennis.bot.mobile.onboarding.namegender.Const.FEMALE
import tennis.bot.mobile.onboarding.namegender.Const.MALE
import tennis.bot.mobile.utils.LetterInputFilter
import tennis.bot.mobile.utils.traverseToAnotherFragment

@AndroidEntryPoint
class NameGenderFragment : CoreFragment<FragmentNameGenderBinding>() {

	override var adjustToKeyboard: Boolean = true
	override val bindingInflation: Inflation<FragmentNameGenderBinding> = FragmentNameGenderBinding::inflate
	private val viewModel: NameGenderViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.nameInputEt.filters = arrayOf(LetterInputFilter())
		binding.nameInputEt.doOnTextChanged { name, _, _, _ ->
			viewModel.onNameInput(name ?: "")
		}

		binding.surnameInputEt.filters = arrayOf(LetterInputFilter())
		binding.surnameInputEt.doOnTextChanged { surname, _, _, _ ->
			viewModel.onSurnameInput(surname ?: "")
		}

		binding.clearNameButton.setOnClickListener {
			binding.nameInputEt.setText("")
		}
		binding.clearSurnameButton.setOnClickListener {
			binding.surnameInputEt.setText("")
		}

		binding.male.setOnClickListener {
			viewModel.onGenderChosen(1)
		}

		binding.female.setOnClickListener {
			viewModel.onGenderChosen(2)
		}

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked()
			parentFragmentManager.traverseToAnotherFragment(LocationFragment())
		}

		subscribeToFlowOn(viewModel.uiStateFlow) {uiState ->
			binding.clearNameButton.visibility = if (uiState.clearNameButtonVisible) View.VISIBLE else View.INVISIBLE
			binding.clearSurnameButton.visibility = if (uiState.clearSurnameButtonVisible) View.VISIBLE else View.INVISIBLE

			binding.male.setBackgroundResource(R.drawable.survey_option_outline)
			binding.female.setBackgroundResource(R.drawable.survey_option_outline)
			when(uiState.gender) {
				MALE -> binding.male.setBackgroundResource(R.drawable.survey_option_outline_picked)
				FEMALE -> binding.female.setBackgroundResource(R.drawable.survey_option_outline_picked)
			}

			viewModel.isNextButtonEnabled()
			binding.buttonNext.isEnabled = uiState.nextButtonEnabled
			val buttonBackground = if (uiState.nextButtonEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonNext.setBackgroundResource(buttonBackground)
		}
	}
}