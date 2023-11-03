package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import javax.inject.Inject

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject lateinit var countryAdapter: PhoneInputAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)
    }


//    override fun onFragmentResult(requestKey: String, result: Bundle) {
//        Log.d("1234567", "onFragmentResult() called with requestKey = $requestKey, result = ${result.getString(CountryCodesDialogFragment.COUNTRY_REQUEST_CODE_KEY)}")
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            val bottomSheet: CountryCodesDialogFragment = CountryCodesDialogFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.tag)

        }

        setFragmentResultListener(CountryCodesDialogFragment.COUNTRY_REQUEST_CODE_KEY) { requestKey, bundle ->
//            val result = bundle.
//            Log.d("1234567", "The deed is done. requestKey = $requestKey, result = $result")
        }

        countryAdapter.clickListener = {

            Log.i("test", "countryAdapter.clickListener in PhoneInput is called")
        }

        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        const val COUNTRY_REQUEST_CODE_KEY = "COUNTRY_CODE_KEY"
        const val SELECTED_COUNTRY_CODE_KEY = "SELECTED_COUNTRY_CODE_KEY"
    }


}

