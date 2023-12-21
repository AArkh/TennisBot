package tennis.bot.mobile.onboarding.login

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLoginBinding
import tennis.bot.mobile.onboarding.password.PasswordUiState
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment
import tennis.bot.mobile.onboarding.phone.SmsCodeFragment
import tennis.bot.mobile.utils.hideKeyboard

@AndroidEntryPoint
class LoginFragment : CoreFragment<FragmentLoginBinding>() {
	override val bindingInflation: Inflation<FragmentLoginBinding> = FragmentLoginBinding::inflate
	private val viewModel: LoginViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.phoneEt.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
		binding.phoneEt.doOnTextChanged { phoneNumber, _, _, _ ->
			viewModel.onPhoneInput(phoneNumber ?: "")
		}
		binding.passwordEt.doOnTextChanged { password, _, _, _ ->
			viewModel.onPasswordInput(password ?: "")
		}

		binding.clearPhoneButton.setOnClickListener {
			binding.phoneEt.setText("")
		}
		binding.clearPasswordButton.setOnClickListener {
			binding.passwordEt.setText("")
		}

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.openCountriesSheetLayout.setOnClickListener {
			requireContext().hideKeyboard()
			lifecycleScope.launch {
				delay(180L) // wait for keyboard to hide
				val bottomSheet = CountryCodesDialogFragment()
				bottomSheet.show(childFragmentManager, bottomSheet.tag)
			}
		}

		setFragmentResultListener(
			CountryCodesDialogFragment.COUNTRY_REQUEST_CODE_KEY
		) { _, result ->
			val countryCode = result.getString(CountryCodesDialogFragment.SELECTED_COUNTRY_CODE_KEY, "+7")
			val countryIcon = result.getInt(CountryCodesDialogFragment.SELECTED_COUNTRY_ICON_KEY)
			viewModel.onCountryPicked(countryCode, countryIcon)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			when(uiState) {
				is LoginUiState.Initial -> {
					binding.countryIv.setImageResource(uiState.countryIconRes)
					binding.phoneInputLayout.prefixText = uiState.phonePrefix
					binding.phoneInputLayout.error = uiState.phoneErrorMessage
					binding.passwordInputLayout.error = uiState.passwordErrorMessage
					binding.clearPhoneButton.visibility = if (uiState.clearPhoneButtonVisible) View.VISIBLE else View.INVISIBLE
					binding.clearPasswordButton.visibility = if (uiState.clearPasswordButtonVisible) View.VISIBLE else View.INVISIBLE


					binding.buttonLogin.isEnabled = uiState.loginButtonEnabled
					val buttonBackground = if (uiState.loginButtonEnabled) {
						R.drawable.btn_bkg_enabled
					} else {
						R.drawable.btn_bkg_disabled
					}
					binding.buttonLogin.setBackgroundResource(buttonBackground)

				}
				is LoginUiState.Loading -> {}
				is LoginUiState.Error -> {}
			}

		}
	}


}