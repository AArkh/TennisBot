package tennis.bot.mobile.profile.account

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentOptionsDialogBinding
import tennis.bot.mobile.profile.edit.EditProfileFragment
import tennis.bot.mobile.profile.matches.MatchesFragment

class OptionsDialogFragment : CoreBottomSheetDialogFragment<FragmentOptionsDialogBinding>() {
	override val bindingInflation: Inflation<FragmentOptionsDialogBinding> =
		FragmentOptionsDialogBinding::inflate

	companion object {
		const val UPDATE_CONTACTS = "CHANGE_CONTACTS"
		const val UPDATE_GAMEDATA = "CHANGE_GAMEDATA"
		const val UPDATE_SETTINGS = "CHANGE_SETTINGS"
		const val LOGOUT = "LOGOUT"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.cancelDialog.setOnClickListener {
			dialog?.dismiss()
		}

		binding.updateContacts.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				AccountPageFragment.OPTIONS_DIALOG_REQUEST_KEY,
				bundleOf(AccountPageFragment.SELECTED_DIALOG_OPTION to UPDATE_CONTACTS)
			)
			dialog?.dismiss()
		}

		binding.updateGameData.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				AccountPageFragment.OPTIONS_DIALOG_REQUEST_KEY,
				bundleOf(AccountPageFragment.SELECTED_DIALOG_OPTION to UPDATE_GAMEDATA)
			)
			dialog?.dismiss()
		}

		binding.settings.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				AccountPageFragment.OPTIONS_DIALOG_REQUEST_KEY,
				bundleOf(AccountPageFragment.SELECTED_DIALOG_OPTION to UPDATE_SETTINGS)
			)
			dialog?.dismiss()
		}

		binding.logout.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				AccountPageFragment.OPTIONS_DIALOG_REQUEST_KEY,
				bundleOf(AccountPageFragment.SELECTED_DIALOG_OPTION to LOGOUT)
			)
			dialog?.dismiss()
		}

	}
}