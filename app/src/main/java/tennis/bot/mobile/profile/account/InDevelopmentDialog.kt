package tennis.bot.mobile.profile.account

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.login.LoginDialogFragment

class InDevelopmentDialog: LoginDialogFragment() {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dialogTitle.text = getString(R.string.in_development_title)
	}
}