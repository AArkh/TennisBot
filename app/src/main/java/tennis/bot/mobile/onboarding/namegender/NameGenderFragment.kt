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

@AndroidEntryPoint
class NameGenderFragment : CoreFragment<FragmentNameGenderBinding>() {
	override val bindingInflation: Inflation<FragmentNameGenderBinding> = FragmentNameGenderBinding::inflate
	private val viewModel: NameGenderViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.nameInputEt.doOnTextChanged { name, _, _, _ ->
			viewModel.onNameInput(name ?: "")
		}

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

		binding.buttonNext.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.replace(R.id.fragment_container_view, LocationFragment())
				.addToBackStack(LocationFragment::class.java.name)
				.commit()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) {uiState ->
			binding.clearNameButton.visibility = if (uiState.clearNameButtonVisible) View.VISIBLE else View.INVISIBLE
			binding.clearSurnameButton.visibility = if (uiState.clearSurnameButtonVisible) View.VISIBLE else View.INVISIBLE

			binding.nameInputLayout.error = uiState.nameErrorMessage
			binding.surnameInputLayout.error = uiState.surnameErrorMessage

			binding.male.setBackgroundResource(R.drawable.survey_option_outline)
			binding.female.setBackgroundResource(R.drawable.survey_option_outline)
			when(uiState.gender) {
				1 -> binding.male.setBackgroundResource(R.drawable.survey_option_outline_picked)
				2 -> binding.female.setBackgroundResource(R.drawable.survey_option_outline_picked)
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