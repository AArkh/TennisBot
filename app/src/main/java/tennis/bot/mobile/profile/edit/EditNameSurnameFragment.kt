package tennis.bot.mobile.profile.edit

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditNameSurnameBinding

// todo поебаться с инсетами, чтобы кнопка над клавой выезжала
@AndroidEntryPoint
class EditNameSurnameFragment : CoreFragment<FragmentEditNameSurnameBinding>() {

	override val bindingInflation: Inflation<FragmentEditNameSurnameBinding> = FragmentEditNameSurnameBinding::inflate
	private val viewModel: EditNameSurnameViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.nameInputEt.doOnTextChanged { name, _, _, _ ->
			viewModel.onNameInput(name ?: "")
		}

		binding.surnameInputEt.doOnTextChanged { surname, _, _, _ ->
			viewModel.onSurnameInput(surname ?: "")
		}

		binding.clearNameButton.setOnClickListener {
			binding.nameInputEt.setText("")
		}
		binding.clearSurnameButton.setOnClickListener {
			binding.surnameInputEt.setText("")
		}

		binding.buttonChange.setOnClickListener {
			viewModel.onUpdateNameSurname(requireActivity()) {
				parentFragmentManager.popBackStack()
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			binding.clearNameButton.visibility = if (uiState.clearNameButtonVisible) View.VISIBLE else View.INVISIBLE
			binding.clearSurnameButton.visibility = if (uiState.clearSurnameButtonVisible) View.VISIBLE else View.INVISIBLE

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