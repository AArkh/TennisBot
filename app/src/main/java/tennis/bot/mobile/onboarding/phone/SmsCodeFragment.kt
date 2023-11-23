package tennis.bot.mobile.onboarding.phone

import android.app.Activity
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSmsCodeBinding
import tennis.bot.mobile.onboarding.location.LocationFragment
import tennis.bot.mobile.onboarding.phone.SmsCodeViewModel.Companion.SMS_CODE_LENGTH
import tennis.bot.mobile.utils.showKeyboard
import tennis.bot.mobile.utils.updateTextIfNeeded
import kotlin.math.min


@AndroidEntryPoint
class SmsCodeFragment : CoreFragment<FragmentSmsCodeBinding>() {

    override var adjustToKeyboard: Boolean = true
    override val bindingInflation: Inflation<FragmentSmsCodeBinding> = FragmentSmsCodeBinding::inflate

    private val viewModel: SmsCodeViewModel by viewModels()
    private val listTv by lazy { listOf(binding.num1, binding.num2, binding.num3, binding.num4) }
    private val listLines by lazy { listOf(binding.line1, binding.line2, binding.line3, binding.line4) }
    private val blockingInputFilter = arrayOf(InputFilter { _, _, _, _, _, _ -> "" })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.hiddenEditText.showKeyboard(requireActivity())
        binding.linearLayout.setOnClickListener {
            binding.hiddenEditText.showKeyboard(requireActivity())
        }
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.hiddenEditText.addTextChangedListener {
            viewModel.onUserInput(binding.hiddenEditText.text.toString())
        }
        binding.buttonNext.setOnClickListener {
            viewModel.onNextButtonClicked {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, LocationFragment())
                    .addToBackStack(LocationFragment::class.java.name)
                    .commit()
            }
        }
        binding.resendButton.setOnClickListener {
            viewModel.onResendSmsButtonClicked()
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState -> updateUiState(uiState) }
    }

    private fun updateUiState(uiState: SmsCodeUiState) {
        binding.title.updateTextIfNeeded(uiState.title)
        binding.timerTv.text = if (uiState.retryButtonBlockedSec > 0 ) {
            uiState.retryButtonBlockedSec.toString()
        } else {
            ""
        }
        val numbers = ArrayList<String>(SMS_CODE_LENGTH)
        uiState.input.forEach { numbers.add(it.toString()) }
        displayNumbersInTextViews(numbers, uiState is SmsCodeUiState.Error)

        when (uiState) {
            is SmsCodeUiState.Error -> {
                binding.hiddenEditText.filters = arrayOf()
                binding.resendButton.isEnabled = !(uiState.retryButtonBlockedSec > 0 ||
                    uiState.retryButtonBlockedSec == SmsCodeUiState.RETRY_BUTTON_BLOCKED_NO_COUNTDOWN)
                binding.buttonNext.isEnabled = false
                binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)
                binding.errorTv.visibility = View.VISIBLE
                binding.errorTv.text = uiState.errorMessage
            }
            is SmsCodeUiState.Loading -> {
                binding.hiddenEditText.filters = blockingInputFilter
                binding.buttonNext.isEnabled = false
                binding.resendButton.isEnabled = false
                binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)
                binding.errorTv.visibility = View.INVISIBLE
            }
            is SmsCodeUiState.UserInput -> {
                binding.hiddenEditText.filters = if (uiState.input.length == SMS_CODE_LENGTH) {
                    blockingInputFilter
                } else {
                    arrayOf()
                }
                binding.resendButton.isEnabled = !(uiState.retryButtonBlockedSec > 0 ||
                    uiState.retryButtonBlockedSec == SmsCodeUiState.RETRY_BUTTON_BLOCKED_NO_COUNTDOWN)
                binding.buttonNext.isEnabled = !uiState.validateButtonBlocked
                val buttonBackground = if (!uiState.validateButtonBlocked) {
                    R.drawable.btn_bkg_enabled
                } else {
                    R.drawable.btn_bkg_disabled
                }
                binding.buttonNext.setBackgroundResource(buttonBackground)
                binding.errorTv.visibility = View.INVISIBLE
            }
        }
    }

    private fun displayNumbersInTextViews(numbers: List<String>, isError: Boolean) {
        val minSize = min(numbers.size, listTv.size)
        for (i in 0 until SMS_CODE_LENGTH) {
            val colorTint = if (isError) {
                ContextCompat.getColorStateList(requireContext(), androidx.appcompat.R.color.error_color_material_light)
            } else if (i < minSize) {
                ContextCompat.getColorStateList(requireContext(), R.color.tb_primary_green)
            } else {
                ContextCompat.getColorStateList(requireContext(), R.color.tb_gray_active)
            }
            listTv[i].text = numbers.getOrNull(i)
            listLines[i].backgroundTintList = colorTint
        }
    }

    companion object {
        const val PHONE_NUMBER_ARGUMENT = "SmsCodeFragment"

        fun newInstance(phoneNumber: String): SmsCodeFragment {
            val fragment = SmsCodeFragment()
            val args = Bundle()
            args.putString(PHONE_NUMBER_ARGUMENT, phoneNumber)
            fragment.arguments = args
            return fragment
        }
    }
}