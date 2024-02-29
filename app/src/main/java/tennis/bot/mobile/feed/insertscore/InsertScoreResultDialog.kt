package tennis.bot.mobile.feed.insertscore

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.insertscore.InsertScoreFragment.Companion.RESULT_DIALOG_REQUEST_KEY
import tennis.bot.mobile.feed.insertscore.InsertScoreFragment.Companion.RESULT_DIALOG_SELECTED_OPTION_KEY
import tennis.bot.mobile.onboarding.login.LoginDialogFragment

class InsertScoreResultDialog: LoginDialogFragment() {

	companion object {
		const val GO_TO_FEED = "GO_TO_FEED"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		dialog?.setCancelable(false)
		dialog?.setCanceledOnTouchOutside(false)

		binding.dartsAnimation.setAnimation(R.raw.done)

		binding.dialogTitle.text = getString(R.string.insert_score_dialog_title)
		binding.dialogText.text = getString(R.string.insert_score_dialog_text)

		binding.buttonGreen.text = getString(R.string.go_to_feed)
		binding.buttonGreen.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				RESULT_DIALOG_REQUEST_KEY,
				bundleOf(RESULT_DIALOG_SELECTED_OPTION_KEY to GO_TO_FEED)
			)
			dialog?.dismiss()
		}
		binding.buttonGrey.visibility = View.GONE

	}
}