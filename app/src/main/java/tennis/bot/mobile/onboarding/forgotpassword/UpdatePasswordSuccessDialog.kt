package tennis.bot.mobile.onboarding.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.onboarding.forgotpassword.UpdatePasswordFragment.Companion.UPDATE_PASSWORD_SUCCESS_DIALOG
import tennis.bot.mobile.utils.basicdialog.BasicDialogFragment

class UpdatePasswordSuccessDialog: BasicDialogFragment() {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonGreen.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				UPDATE_PASSWORD_SUCCESS_DIALOG,
				bundleOf() // trying with an empty one
			)
		}
	}
}