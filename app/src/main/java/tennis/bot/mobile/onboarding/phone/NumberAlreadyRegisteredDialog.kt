package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.utils.basicdialog.BasicDialogFragment

class NumberAlreadyRegisteredDialog: BasicDialogFragment() {

	companion object {
		const val FORGOT_PASSWORD_DIALOG_REQUEST_KEY = "FORGOT_PASSWORD_DIALOG_REQUEST_KEY"
		const val FORGOT_PASSWORD_DIALOG_SELECTED_OPTION_KEY = "FORGOT_PASSWORD_DIALOG_SELECTED_OPTION_KEY"
		const val GO_TO_FORGOT_PASSWORD = "GO_TO_FORGOT_PASSWORD"
		const val LOGIN_CALLBACK = "LOGIN_CALLBACK"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonGreen.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				FORGOT_PASSWORD_DIALOG_REQUEST_KEY,
				bundleOf(FORGOT_PASSWORD_DIALOG_SELECTED_OPTION_KEY to LOGIN_CALLBACK)
			)
		}
		binding.buttonGrey.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				FORGOT_PASSWORD_DIALOG_REQUEST_KEY,
				bundleOf(FORGOT_PASSWORD_DIALOG_SELECTED_OPTION_KEY to GO_TO_FORGOT_PASSWORD)
			)
		}
	}
}