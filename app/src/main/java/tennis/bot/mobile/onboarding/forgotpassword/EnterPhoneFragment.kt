package tennis.bot.mobile.onboarding.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.onboarding.phone.PhoneInputFragment
import tennis.bot.mobile.onboarding.phone.PhoneInputViewModel
import tennis.bot.mobile.utils.traverseToAnotherFragment

@AndroidEntryPoint
class EnterPhoneFragment: PhoneInputFragment() {

	private val phoneInputViewModel: PhoneInputViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonNext.setOnClickListener {
			phoneInputViewModel.onNextClicked(true) { phoneNumber, _ ->
				parentFragmentManager.traverseToAnotherFragment(VerifySmsFragment.newInstance(phoneNumber))
			}
		}
	}
}