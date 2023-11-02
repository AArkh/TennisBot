package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentResultListener
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
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>()
//    , FragmentResultListener
{
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject lateinit var countryAdapter: PhoneInputAdapter

    @Inject lateinit var repository: CountryCodeRepository

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        parentFragmentManager.setFragmentResultListener(
//            CountryCodesDialogFragment.COUNTRY_REQUEST_CODE_KEY,
//            viewLifecycleOwner,
//            this
//        )
//        return super.onCreateView(inflater, container, savedInstanceState)
//    }
//
//    override fun onFragmentResult(requestKey: String, result: Bundle) {
//        Log.d("1234567", "onFragmentResult() called with: requestKey = $requestKey, result = ${result.getString(CountryCodesDialogFragment.SELECTED_COUNTRY_CODE_KEY)}")
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                repository.selectedCountryFlow
                    .onEach { countryItem ->
                        Log.d("1234567", "onViewCreated: $countryItem")
                    }
                    .launchIn(lifecycleScope)
            }
        }

        binding.openCountriesSheetLayout.setOnClickListener {
            val bottomSheet: CountryCodesDialogFragment = CountryCodesDialogFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.getTag())
//            val dialog = BottomSheetDialog(requireContext())
//            val dialogView = layoutInflater.inflate(R.layout.country_phones_bottom_sheet, null)
//            val closeBtn = dialogView.findViewById<ImageView>(R.id.close_button_iv)
//            val countriesRecycler = dialogView.findViewById<RecyclerView>(R.id.countries_list_rv)
//            countriesRecycler.adapter = countryAdapter
//            countriesRecycler.layoutManager = LinearLayoutManager(requireContext())
//            closeBtn.setOnClickListener {
//                dialog.dismiss()
//            }
//            dialog.setContentView(dialogView)
//            dialog.show()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}