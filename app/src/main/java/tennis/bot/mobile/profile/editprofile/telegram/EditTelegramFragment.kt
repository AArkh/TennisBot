package tennis.bot.mobile.profile.editprofile.telegram

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditTelegramBinding

class EditTelegramFragment : CoreFragment<FragmentEditTelegramBinding>() {

	override var adjustToKeyboard: Boolean = true
	override val bindingInflation: Inflation<FragmentEditTelegramBinding> = FragmentEditTelegramBinding::inflate
	private val viewModel: EditTelegramViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.telegramEt.filters = arrayOf(viewModel.LetterInputFilter())
		binding.telegramEt.doOnTextChanged { name, _, _, _ ->
			viewModel.onTelegramInput(name ?: "")
		}

		binding.clearTextButton.setOnClickListener {
			binding.telegramEt.setText("")
		}

		binding.buttonChange.setOnClickListener {
			viewModel.onUpdateNameSurname(requireActivity()) {
				parentFragmentManager.popBackStack()
			}
		}

		requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE) // fixme

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			binding.clearTextButton.visibility = if (uiState.clearTelegramButtonVisible) View.VISIBLE else View.INVISIBLE

			viewModel.isChangeButtonEnabled()
			binding.buttonChange.isEnabled = uiState.changeButtonEnabled
			val buttonBackground = if (uiState.changeButtonEnabled) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonChange.setBackgroundResource(buttonBackground)
		}
	}
}