package tennis.bot.mobile.onboarding.survey

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.DialogSurveyWelcomeBinding

@AndroidEntryPoint
class SurveyWelcomeDialog: CoreDialogFragment<DialogSurveyWelcomeBinding>() {

	override val bindingInflation: Inflation<DialogSurveyWelcomeBinding> = DialogSurveyWelcomeBinding::inflate

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (dialog != null && dialog!!.window != null) {
			dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dialogButtonStart.setOnClickListener {
			dialog?.dismiss()
		}
	}
}