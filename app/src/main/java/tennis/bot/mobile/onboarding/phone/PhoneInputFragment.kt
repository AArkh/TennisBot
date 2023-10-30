package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding

@AndroidEntryPoint
class PhoneInputFragment : CoreFragment<FragmentPhoneInputBinding>() {
    override val bindingInflation: Inflation<FragmentPhoneInputBinding> = FragmentPhoneInputBinding::inflate

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

        super.onViewCreated(view, savedInstanceState)
    }
}