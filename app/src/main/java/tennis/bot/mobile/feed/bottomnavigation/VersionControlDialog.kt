package tennis.bot.mobile.feed.bottomnavigation

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.utils.basicdialog.BasicDialogFragment

class VersionControlDialog: BasicDialogFragment() {

	companion object {
		const val UPDATE_URL = "https://t.me/+wK8uBq8Lyg00MmQy"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonGreen.setOnClickListener {
			openLink(UPDATE_URL)
		}

		binding.buttonGrey.setOnClickListener {
			dialog?.dismiss()
		}
	}
}