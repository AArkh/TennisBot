package tennis.bot.mobile.profile.account

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.login.LoginDialogFragment

class TournamentDialog: LoginDialogFragment() {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dartsAnimation.setAnimation(R.raw.telegram)
		binding.dialogTitle.text = getString(R.string.tournament_dialog_title)
		binding.dialogText.text = getString(R.string.tournament_dialog_text)
		binding.buttonGreen.setBackgroundColor(getColor(requireContext(), R.color.tb_blue_telegram))
		binding.buttonGreen.text = getString(R.string.button_go_to_telegram)
		binding.buttonGrey.text = getString(R.string.stay_in_the_app)

		binding.buttonGrey.setOnClickListener {
			dialog?.dismiss()
		}
	}
}