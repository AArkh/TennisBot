package tennis.bot.mobile.onboarding.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import tennis.bot.mobile.onboarding.phone.SmsCodeFragment
import tennis.bot.mobile.onboarding.phone.SmsCodeViewModel
import tennis.bot.mobile.utils.traverseToAnotherFragment

class VerifySmsFragment: SmsCodeFragment() {

	private val viewModel: SmsCodeViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonNext.setOnClickListener {
			viewModel.onNextButtonClicked(true) {
				parentFragmentManager.traverseToAnotherFragment(UpdatePasswordFragment())
			}
		}
	}

	companion object {
		fun newInstance(phoneNumber: String): VerifySmsFragment {
			val fragment = VerifySmsFragment()
			val args = Bundle()
			args.putString(PHONE_NUMBER_ARGUMENT, phoneNumber)
			fragment.arguments = args
			return fragment
		}
	}
}