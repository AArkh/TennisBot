package tennis.bot.mobile.onboarding.password

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPasswordBinding
import tennis.bot.mobile.onboarding.login.LoginViewModel
import tennis.bot.mobile.onboarding.survey.SurveyFragment
import tennis.bot.mobile.utils.goToAnotherSectionFragment

@AndroidEntryPoint
class PasswordFragment : CoreFragment<FragmentPasswordBinding>() {

	override var adjustToKeyboard: Boolean = true
	override val bindingInflation: Inflation<FragmentPasswordBinding> = FragmentPasswordBinding::inflate
	private val viewModel: PasswordViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.passwordEt.doOnTextChanged { password, _, _, _ ->
			viewModel.onPasswordInput(password ?: "")
		}
		binding.passwordEt.filters = arrayOf(LoginViewModel.NoSpaceInputFilter())
		binding.passwordEt.doAfterTextChanged {
			it?.replace(Regex(" "), "")
		}

		binding.clearButton.setOnClickListener {
			binding.passwordEt.setText("")
		}
		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.confidentialityText.movementMethod = LinkMovementMethod.getInstance() // ссылка в strings

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked{
				parentFragmentManager.goToAnotherSectionFragment(SurveyFragment())
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			when(uiState) {
				is PasswordUiState.Initial -> {
					binding.buttonLoadingAnim.visibility = View.INVISIBLE
					binding.buttonNext.isEnabled = uiState.nextButtonEnabled
					binding.buttonNext.text = uiState.nextButtonText
					val buttonBackground = if (uiState.nextButtonEnabled) {
						R.drawable.btn_bkg_enabled
					} else {
						R.drawable.btn_bkg_disabled
					}
					binding.buttonNext.setBackgroundResource(buttonBackground)
					binding.textInputLayout.error = uiState.errorMessage
					binding.clearButton.visibility = if (uiState.clearButtonVisible) View.VISIBLE else View.INVISIBLE
				}
				is PasswordUiState.Loading -> {
					binding.buttonNext.text = ""
					binding.buttonLoadingAnim.visibility = View.VISIBLE
				}
				is PasswordUiState.Error -> {}
			}
		}
	}
}