package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import javax.inject.Inject

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate
    @Inject lateinit var countryAdapter: PhoneInputAdapter


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


        countryAdapter.setListAndNotify(listOf(
            CountryItem(R.drawable.russia_big, "Россия", "+7"),
            CountryItem(R.drawable.ukraine, "Украина", "+380"),
            CountryItem(R.drawable.belarus, "Беларусь", "+375"),
            CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
            CountryItem(R.drawable.canada, "Канада", "+1")
        ))
        binding.openCountriesSheetIv.setOnClickListener {

            val dialog = BottomSheetDialog(requireContext())
            val dialogView = layoutInflater.inflate(R.layout.country_phones_bottom_sheet, null)
            val closeBtn = dialogView.findViewById<ImageView>(R.id.close_button_iv)
            val countriesRecycler = dialogView.findViewById<RecyclerView>(R.id.countries_list_rv)
            countriesRecycler.adapter = countryAdapter
            countriesRecycler.layoutManager = LinearLayoutManager(requireContext())
            closeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.setContentView(dialogView)
            dialog.show()
        }

        super.onViewCreated(view, savedInstanceState)
    }
}