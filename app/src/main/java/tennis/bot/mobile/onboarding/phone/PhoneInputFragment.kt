package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import tennis.bot.mobile.utils.hideKeyboard
import javax.inject.Inject

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject
    lateinit var countryAdapter: PhoneInputAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.phoneEt.addTextChangedListener {
            if (it?.isEmpty() == true) {
                binding.clearButton.visibility = View.INVISIBLE
            } else {
                binding.clearButton.visibility = View.VISIBLE
            }
        }
        binding.phoneEt.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        binding.phoneEt.doOnTextChanged { text, _, _, _ ->
            if (text!!.length < 14) {
                binding.textInputLayout.error = requireContext().getString(R.string.onboarding_text_incorrect_phone_number)
            } else {
                binding.textInputLayout.error = null
            }
        }
        binding.clearButton.setOnClickListener {
            binding.phoneEt.setText("")
        }

        binding.buttonNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, SmsCodeFragment())
                .addToBackStack(SmsCodeFragment::class.java.name)
                .commit()
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
            binding.textInputLayout.prefixText = result.getString(CountryCodesDialogFragment.SELECTED_COUNTRY_CODE_KEY)
            binding.countryIv.setImageResource(result.getInt(CountryCodesDialogFragment.SELECTED_COUNTRY_ICON_KEY))
        }
    }
}