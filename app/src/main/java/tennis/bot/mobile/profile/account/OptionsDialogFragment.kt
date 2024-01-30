package tennis.bot.mobile.profile.account

import android.os.Bundle
import android.view.View
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentOptionsDialogBinding
import tennis.bot.mobile.profile.edit.EditProfileFragment
import tennis.bot.mobile.profile.matches.MatchesFragment

class OptionsDialogFragment : CoreBottomSheetDialogFragment<FragmentOptionsDialogBinding>() {
	override val bindingInflation: Inflation<FragmentOptionsDialogBinding> =
		FragmentOptionsDialogBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.cancelDialog.setOnClickListener {
			dialog?.dismiss()
		}

		binding.updateContacts.setOnClickListener {

			// todo set result, depending on option selected and then just close the dialog
		}
	}
}