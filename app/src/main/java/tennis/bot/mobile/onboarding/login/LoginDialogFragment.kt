package tennis.bot.mobile.onboarding.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.LoginDialogBinding


@AndroidEntryPoint
class LoginDialogFragment: CoreDialogFragment<LoginDialogBinding>() {
	override val bindingInflation: Inflation<LoginDialogBinding> = LoginDialogBinding::inflate

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

		if (dialog != null && dialog!!.window != null) {
			dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
		}

		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonBot.setOnClickListener {
			openLink(BOT_URL)
		}

		binding.buttonChat.setOnClickListener {
			openLink(CHAT_URL)
		}

	}

	private fun openLink(url: String) {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		startActivity(intent)
	}

	companion object {
		const val BOT_URL = "https://t.me/TennisPartnerBot"
		const val CHAT_URL = "https://t.me/tennisbotru"
	}
}