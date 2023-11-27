package tennis.bot.mobile.onboarding.password

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPasswordBinding
import tennis.bot.mobile.utils.updateTextIfNeeded

@AndroidEntryPoint
class PasswordFragment : CoreFragment<FragmentPasswordBinding>() {

	override var adjustToKeyboard: Boolean = true
	override val bindingInflation: Inflation<FragmentPasswordBinding> = FragmentPasswordBinding::inflate
	private val viewModel: PasswordViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.passwordEt.doOnTextChanged { password, _, _, _ ->
			viewModel.onPasswordInput(password ?: "")
		}

		binding.clearButton.setOnClickListener {
			binding.passwordEt.setText("")
		}
		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.confidentialityText.movementMethod = LinkMovementMethod.getInstance()

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			binding.buttonNext.isEnabled = uiState.nextButtonEnabled
			val buttonBackground = if (uiState.nextButtonEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonNext.setBackgroundResource(buttonBackground)
			binding.textInputLayout.error = uiState.errorMessage
			binding.clearButton.visibility = if (uiState.clearButtonVisible) View.VISIBLE else View.INVISIBLE
		}
	}
}