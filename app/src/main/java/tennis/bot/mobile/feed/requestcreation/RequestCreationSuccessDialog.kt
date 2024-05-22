package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.R

class RequestCreationSuccessDialog: RequestCreationDeniedDialog() {
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dartsAnimation.setAnimation(R.raw.done)

		binding.dialogTitle.text = getString(R.string.request_creation_success_title)
		binding.dialogText.text = getString(R.string.request_creation_success_text)

		binding.buttonGreen.text = getString(R.string.go_to_feed)
	}
}