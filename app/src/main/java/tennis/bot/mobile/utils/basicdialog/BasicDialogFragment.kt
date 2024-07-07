package tennis.bot.mobile.utils.basicdialog

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.DialogLoginBinding

@AndroidEntryPoint
open class BasicDialogFragment: CoreDialogFragment<DialogLoginBinding>() {

	override val bindingInflation: Inflation<DialogLoginBinding> = DialogLoginBinding::inflate
	private val viewModel: BasicDialogViewModel by viewModels()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		if (dialog != null && dialog!!.window != null) {
			dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
		}
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonGreen.setOnClickListener { // basic, subject to deletion
			openLink(BOT_URL)
		}

		binding.buttonGrey.setOnClickListener {// basic, subject to deletion
			openLink(CHAT_URL)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			uiState.animation?.let { binding.dartsAnimation.setAnimation(it) }
			uiState.title?.let { binding.dialogTitle.text = it }
			uiState.text?.let { binding.dialogText.text = it }
			uiState.topButtonText?.let{ binding.buttonGreen.text = it }
			uiState.topButtonColor?.let { binding.buttonGreen.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), it))) }
			uiState.bottomButtonText?.let{ binding.buttonGrey.text = it }
			uiState.bottomButtonColor?.let { binding.buttonGrey.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), it))) }
			binding.buttonGrey.isVisible = !uiState.isOneButton
			dialog?.setCancelable(uiState.isCancelable)
			dialog?.setCanceledOnTouchOutside(uiState.isCanceledOnTouchOutside)
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