package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.os.bundleOf
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
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import tennis.bot.mobile.onboarding.forgotpassword.VerifySmsFragment
import tennis.bot.mobile.onboarding.login.LoginFragment
import tennis.bot.mobile.onboarding.phone.NumberAlreadyRegisteredDialog.Companion.FORGOT_PASSWORD_DIALOG_REQUEST_KEY
import tennis.bot.mobile.onboarding.phone.NumberAlreadyRegisteredDialog.Companion.GO_TO_FORGOT_PASSWORD
import tennis.bot.mobile.onboarding.phone.NumberAlreadyRegisteredDialog.Companion.LOGIN_CALLBACK
import tennis.bot.mobile.utils.basicdialog.BasicDialogViewModel
import tennis.bot.mobile.utils.hideKeyboard
import tennis.bot.mobile.utils.traverseToAnotherFragment

@AndroidEntryPoint
open class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {

    override var adjustToKeyboard: Boolean = true
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    private val phoneInputViewModel: PhoneInputViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.phoneEt.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        binding.phoneEt.doOnTextChanged { text, _, _, _ ->
            phoneInputViewModel.onTextInput(text ?: "")
        }
        binding.clearButton.setOnClickListener {
            binding.phoneEt.setText("")
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.confidentialityText.movementMethod = LinkMovementMethod.getInstance() // ссылка в strings

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
            phoneInputViewModel.onCountryPicked(countryCode, countryIcon)
        }

        setFragmentResultListener(FORGOT_PASSWORD_DIALOG_REQUEST_KEY) { _, result ->
            when (result.getString(NumberAlreadyRegisteredDialog.FORGOT_PASSWORD_DIALOG_SELECTED_OPTION_KEY)) {
                LOGIN_CALLBACK -> {
                    parentFragmentManager.traverseToAnotherFragment(LoginFragment())
                }
                GO_TO_FORGOT_PASSWORD -> {
                    phoneInputViewModel.onNextClicked(true) { phoneNumber, _ ->
                        parentFragmentManager.traverseToAnotherFragment(VerifySmsFragment.newInstance(phoneNumber))
                    }
                }
            }
        }

        subscribeToFlowOn(phoneInputViewModel.uiStateFlow) { uiState: PhoneInputUiState ->
            binding.textInputLayout.prefixText = uiState.prefix
            binding.countryIv.setImageResource(uiState.iconRes)

            binding.buttonNext.isEnabled = uiState.proceedButtonEnabled
            val buttonBackground = if (uiState.proceedButtonEnabled) {
                R.drawable.btn_bkg_enabled
            } else {
                R.drawable.btn_bkg_disabled
            }
            binding.buttonNext.setBackgroundResource(buttonBackground)
            binding.textInputLayout.error = uiState.errorMessage
            binding.clearButton.visibility = if (uiState.clearButtonVisible) View.VISIBLE else View.INVISIBLE
        }

        binding.buttonNext.setOnClickListener {
            phoneInputViewModel.onNextClicked { phoneNumber, isSuccess ->
                if (isSuccess) {
                    parentFragmentManager.traverseToAnotherFragment(SmsCodeFragment.newInstance(phoneNumber))
                } else {
                    val dialog = NumberAlreadyRegisteredDialog()
                    dialog.arguments = bundleOf(
                        BasicDialogViewModel.SELECT_DIALOG_TITLE to getString(R.string.number_exist_dialog_title),
                        BasicDialogViewModel.SELECT_DIALOG_TEXT to getString(R.string.number_exist_dialog_text),
                        BasicDialogViewModel.SELECT_DIALOG_TOP_BUTTON_TEXT to getString(R.string.login_title),
                        BasicDialogViewModel.SELECT_DIALOG_BOTTOM_BUTTON_TEXT to getString(R.string.restore_password))
                    dialog.show(childFragmentManager, dialog.tag)
                }
            }
        }
    }
}