package tennis.bot.mobile.onboarding.phone

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSmsCodeBinding
import tennis.bot.mobile.onboarding.location.LocationFragment
import kotlin.math.min


@AndroidEntryPoint
class SmsCodeFragment : CoreFragment<FragmentSmsCodeBinding>() {
    override val bindingInflation: Inflation<FragmentSmsCodeBinding> = FragmentSmsCodeBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listTv = listOf(binding.num1, binding.num2, binding.num3, binding.num4)
        val listLines = listOf(binding.line1, binding.line2, binding.line3, binding.line4)
        val correctCode = "1234"

        fun EditText.showKeyboard(activity: Activity) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            requestFocus()
            inputMethodManager.showSoftInput(this, 0)
        }
        binding.codeEt.showKeyboard(requireActivity())

        binding.codeEt.addTextChangedListener {
            val inputText = binding.codeEt.text.toString()
            val numbers = ArrayList<String>(4)
            inputText.forEach { numbers.add(it.toString()) }
            displayNumbersInTextViews(numbers, listTv, listLines)
            if (numbers.size < 4 || correctCode != inputText) {
                for (i in 0 until 4) {
                    listLines[i].backgroundTintList =
                        ContextCompat.getColorStateList(requireContext(), androidx.appcompat.R.color.error_color_material_light);
                    clearDeletedNumbers(listTv, 4, numbers)
                }
                binding.errorTv.visibility = View.VISIBLE
            } else {
                binding.errorTv.visibility = View.INVISIBLE
            }
        }

        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = millisUntilFinished / 1000
                if (remaining < 10) {
                    binding.timerTv.text = "0:0$remaining"
                } else {
                    binding.timerTv.text = "0:$remaining"
                }
            }

            override fun onFinish() {
                binding.timerTv.visibility = View.GONE
            }
        }.start()

        binding.title.text = "Введи код отправленный на ${arguments?.getString(PhoneInputFragment.PHONE_NUMBER)}"

        binding.buttonNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, LocationFragment())
                .addToBackStack(LocationFragment::class.java.name)
                .commit()
        }
    }

    private fun displayNumbersInTextViews(
        numbers: List<String>,
        textViews: List<TextView>,
        listLines: List<View>
    ) {
        val minSize = min(numbers.size, textViews.size)

        for (i in 0 until minSize) {
            textViews[i].text = numbers[i]
            listLines[i].backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.tb_primary_green);
        }
    }

    private fun clearDeletedNumbers(textViews: List<TextView>, oldNumbers: Int, newNumbers: List<String>) {
        for (i in 0 until min(oldNumbers, textViews.size)) {
            val textView = textViews[i]
            if (!newNumbers.indices.contains(i)) {
                textView.text = " " // Clear the text if the number is deleted
            }
        }
    }
}