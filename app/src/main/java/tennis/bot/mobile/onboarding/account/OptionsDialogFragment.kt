package tennis.bot.mobile.onboarding.account

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentOptionsDialogBinding

class OptionsDialogFragment : CoreBottomSheetDialogFragment<FragmentOptionsDialogBinding>() {
	override val bindingInflation: Inflation<FragmentOptionsDialogBinding> =
		FragmentOptionsDialogBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.cancelDialog.setOnClickListener {
			dialog?.dismiss()
		}
	}
}