package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.R
import tennis.bot.mobile.utils.basicdialog.BasicDialogFragment

open class RequestCreationDeniedDialog: BasicDialogFragment() {

	companion object {
		const val GO_TO_FEED = "GO_TO_FEED"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dialogTitle.text = getString(R.string.you_have_request)
		binding.dialogText.text = getString(R.string.you_have_request_dialog_text)

		binding.buttonGreen.text = getString(R.string.understood)
		binding.buttonGreen.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				RequestCreationFragment.REQUEST_DENIED_DIALOG_KEY,
				bundleOf(RequestCreationFragment.REQUEST_DENIED_DIALOG_PICKED_OPTION to GO_TO_FEED)
			)
			dialog?.dismiss()
		}
	}
}