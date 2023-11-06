package tennis.bot.mobile.onboarding.phone

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.subscribe
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import javax.inject.Inject

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject lateinit var countryAdapter: PhoneInputAdapter

    @Inject lateinit var repository: CountryCodeRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.phoneEt.addTextChangedListener {
            if (it?.isEmpty() == true) {
                binding.xIv.visibility = View.INVISIBLE
            } else {
                binding.xIv.visibility = View.VISIBLE
            }
        }
        binding.phoneEt.addTextChangedListener(PhoneNumberFormattingTextWatcher("US"))
        binding.phoneEt.doOnTextChanged { text, start, before, count ->
            if (text!!.length < 14) {
                binding.textInputLayout.error = "Введите действительный номер"
            } else {
                binding.textInputLayout.error = null
            }
        }
        binding.xIv.setOnClickListener {
            binding.phoneEt.setText("")
        }

        binding.buttonNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, SmsCodeFragment())
                .addToBackStack(SmsCodeFragment::class.java.name)
                .commit()
        }

        binding.openCountriesSheetLayout.setOnClickListener {
            val bottomSheet = CountryCodesDialogFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.getTag())
        }

        setFragmentResultListener(
            CountryCodesDialogFragment.COUNTRY_REQUEST_CODE_KEY
        ) { requestKey, result ->
            binding.textInputLayout.prefixText = result.getString(CountryCodesDialogFragment.SELECTED_COUNTRY_CODE_KEY)
        }

        setFragmentResultListener(
            CountryCodesDialogFragment.COUNTRY_REQUEST_ICON_KEY
        ) { requestKey, result ->
            binding.countryIv.setImageResource(result.getInt(CountryCodesDialogFragment.SELECTED_COUNTRY_ICON_KEY))
        }
    }
}