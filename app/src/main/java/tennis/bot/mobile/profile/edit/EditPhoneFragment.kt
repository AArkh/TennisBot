package tennis.bot.mobile.profile.edit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.phone.PhoneInputFragment

@AndroidEntryPoint
class EditPhoneFragment: PhoneInputFragment() {

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonNext.text = requireContext().getString(R.string.button_change)
		binding.buttonNext.setOnClickListener {
			val result = "${binding.textInputLayout.prefixText.toString()} ${binding.phoneEt.text.toString()}"

			activity?.supportFragmentManager?.setFragmentResult(
				EditProfileViewModel.PHONE_NUMBER_REQUEST_KEY,
				bundleOf(EditProfileViewModel.SELECTED_PHONE_NUMBER to result)
			)
			parentFragmentManager.popBackStack()
		}

	}
}