package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
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
import tennis.bot.mobile.utils.hideKeyboard
import tennis.bot.mobile.utils.updateTextIfNeeded
import javax.inject.Inject

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {

    override var adjustToKeyboard: Boolean = true
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject
    lateinit var countryAdapter: CountryCodesAdapter
    private val phoneInputViewModel: PhoneInputViewModel by viewModels()

    companion object {
        const val PHONE_NUMBER = "PHONE_NUMBER"
    }

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

        subscribeToFlowOn(phoneInputViewModel.uiStateFlow) { uiState: PhoneInputUiState ->
            binding.textInputLayout.prefixText = uiState.prefix
            binding.countryIv.setImageResource(uiState.iconRes)
            binding.phoneEt.updateTextIfNeeded(uiState.userInput)

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
            parentFragment?.arguments = bundleOf(
                PHONE_NUMBER to binding.phoneEt.text.toString()
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, SmsCodeFragment())
                .addToBackStack(SmsCodeFragment::class.java.name)
                .commit()
        }
    }
}