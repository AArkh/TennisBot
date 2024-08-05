package tennis.bot.mobile.onboarding.login

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import coil.decode.SvgDecoder
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLoginBinding
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationFragment
import tennis.bot.mobile.onboarding.forgotpassword.EnterPhoneFragment
import tennis.bot.mobile.onboarding.sport.SportFragment
import tennis.bot.mobile.utils.NoSpaceInputFilter
import tennis.bot.mobile.utils.destroyBackstack
import tennis.bot.mobile.utils.goToAnotherSectionFragment

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
		binding.passwordEt.filters = arrayOf(NoSpaceInputFilter())
		binding.passwordEt.doAfterTextChanged {
			it.toString().replace(" ", "")
		}

		binding.clearPhoneButton.setOnClickListener {
			binding.phoneEt.setText("")
		}
		binding.clearPasswordButton.setOnClickListener {
			binding.passwordEt.setText("")
		}

		binding.buttonLogin.setOnClickListener {
			viewModel.onLoginPressed(
				username = binding.phoneEt.text.toString(),
				password = binding.passwordEt.text.toString()
			) { isContinueRegistration ->
				if (!isContinueRegistration) {
					parentFragmentManager.destroyBackstack()
					requireActivity().supportFragmentManager.beginTransaction()
						.replace(R.id.fragment_container_view, BottomNavigationFragment())
						.commit()
				} else {
					parentFragmentManager.goToAnotherSectionFragment(SportFragment())
				}
			}
		}

		binding.buttonForgotPassword.setOnClickListener {
			parentFragmentManager.goToAnotherSectionFragment(EnterPhoneFragment())
		}

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			if (uiState.countryCode.isNotEmpty()) {
				binding.countryIv.load("https://hatscripts.github.io/circle-flags/flags/${uiState.countryCode}.svg") {
					decoderFactory { result, options, _ ->
						SvgDecoder(result.source, options)
					}
				}
			}
			binding.phoneInputLayout.error = uiState.phoneErrorMessage
			binding.passwordInputLayout.error = uiState.passwordErrorMessage
			binding.clearPhoneButton.visibility = if (uiState.clearPhoneButtonVisible) View.VISIBLE else View.INVISIBLE
			binding.clearPasswordButton.visibility = if (uiState.clearPasswordButtonVisible) View.VISIBLE else View.INVISIBLE

			viewModel.isLoginButtonEnabled()
			binding.buttonLogin.isEnabled = uiState.loginButtonEnabled
			val buttonBackground = if (uiState.loginButtonEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonLogin.setBackgroundResource(buttonBackground)
			if (uiState.isLoading) {
				binding.buttonLogin.text = ""
				binding.buttonLoadingAnim.visibility = View.VISIBLE
			} else {
				binding.buttonLogin.text = context?.getString(R.string.login_title)
				binding.buttonLoadingAnim.visibility = View.GONE
			}
		}
	}
}