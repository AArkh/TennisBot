package tennis.bot.mobile.onboarding.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentUpdatePasswordBinding
import tennis.bot.mobile.onboarding.login.LoginFragment
import tennis.bot.mobile.utils.NoSpaceInputFilter
import tennis.bot.mobile.utils.basicdialog.BasicDialogViewModel
import tennis.bot.mobile.utils.traverseToAnotherFragment

@AndroidEntryPoint
class UpdatePasswordFragment: CoreFragment<FragmentUpdatePasswordBinding>() {

	override var adjustToKeyboard: Boolean = true
	override val bindingInflation: Inflation<FragmentUpdatePasswordBinding> = FragmentUpdatePasswordBinding::inflate
	private val viewModel: UpdatePasswordViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.passwordEt.doOnTextChanged { password, _, _, _ ->
			viewModel.onPasswordInput(password ?: "", false)
		}
		binding.passwordEt.filters = arrayOf(NoSpaceInputFilter())
		binding.passwordEt.doAfterTextChanged {
			it?.replace(Regex(" "), "")
		}

		binding.confirmPasswordEt.doOnTextChanged { password, _, _, _ ->
			viewModel.onPasswordInput(password ?: "", true)
		}
		binding.confirmPasswordEt.filters = arrayOf(NoSpaceInputFilter())
		binding.confirmPasswordEt.doAfterTextChanged {
			it?.replace(Regex(" "), "")
		}

		binding.clearPasswordButton.setOnClickListener {
			binding.passwordEt.setText("")
		}
		binding.clearConfirmPasswordButton.setOnClickListener {
			binding.confirmPasswordEt.setText("")
		}
		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked {
				val dialog = UpdatePasswordSuccessDialog()
				dialog.arguments = bundleOf(
					BasicDialogViewModel.SELECT_DIALOG_ANIMATION to R.raw.done,
					BasicDialogViewModel.SELECT_DIALOG_TITLE to getString(R.string.password_changed_success),
					BasicDialogViewModel.SELECT_DIALOG_TEXT to getString(R.string.password_change_success_text),
					BasicDialogViewModel.SELECT_DIALOG_TOP_BUTTON_TEXT to getString(R.string.okay),
					BasicDialogViewModel.SELECT_DIALOG_IS_ONE_BUTTON to true,
					BasicDialogViewModel.SELECT_DIALOG_IS_CANCELABLE to false,
					BasicDialogViewModel.SELECT_DIALOG_IS_CANCELABLE_ON_TOUCH_OUTSIDE to false)
				dialog.show(childFragmentManager, dialog.tag)
			}
		}
		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		setFragmentResultListener(UPDATE_PASSWORD_SUCCESS_DIALOG) { _, _ ->
			parentFragmentManager.clearBackStack(LoginFragment::class.java.name)
			parentFragmentManager.clearBackStack(EnterPhoneFragment::class.java.name)
			parentFragmentManager.clearBackStack(VerifySmsFragment::class.java.name)
			parentFragmentManager.clearBackStack(UpdatePasswordFragment::class.java.name)
			parentFragmentManager.traverseToAnotherFragment(LoginFragment())
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			binding.passwordInputLayout.error = uiState.passwordErrorMessage
			binding.clearPasswordButton.visibility = if (uiState.passwordClearButtonVisible) View.VISIBLE else View.INVISIBLE

			binding.confirmPasswordInputLayout.error = uiState.confirmPasswordErrorMessage
			binding.clearConfirmPasswordButton.visibility = if (uiState.confirmPasswordClearButtonVisible) View.VISIBLE else View.INVISIBLE

			binding.buttonNext.isEnabled = viewModel.isNextButtonEnabled()
			buttonVisibilityController(uiState.isLoading)
		}
	}

	private fun buttonVisibilityController(isLoading: Boolean) {
		val buttonSendBackground = if (binding.buttonNext.isEnabled) {
			R.drawable.btn_bkg_enabled
		} else {
			R.drawable.btn_bkg_disabled
		}
		binding.buttonNext.setBackgroundResource(buttonSendBackground)
		if (isLoading) {
			binding.buttonNext.text = ""
			binding.buttonLoadingAnim.visibility = View.VISIBLE
		} else {
			binding.buttonNext.text = context?.getString(R.string.button_send)
			binding.buttonLoadingAnim.visibility = View.GONE
		}
	}

	companion object {
		const val UPDATE_PASSWORD_SUCCESS_DIALOG = "UPDATE_PASSWORD_SUCCESS_DIALOG"
	}
}