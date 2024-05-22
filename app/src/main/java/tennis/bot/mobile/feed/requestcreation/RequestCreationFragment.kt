package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import tennis.bot.mobile.onboarding.location.LocationDialogViewModel
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@AndroidEntryPoint
class RequestCreationFragment : AuthorizedCoreFragment<FragmentRequestBinding>() {

	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate
	private val viewModel: RequestCreationViewModel by viewModels()
	@Inject
	lateinit var adapter: RequestAdapter
	override var adjustToKeyboard: Boolean = true
	companion object {
		const val REQUEST_DIALOG_REQUEST_KEY = "REQUEST_DIALOG_REQUEST_KEY"
		const val REQUEST_DIALOG_SELECT_ACTION_KEY = "REQUEST_DIALOG_SELECT_ACTION_KEY"
		const val REQUEST_DIALOG_TITLE = "title"
		const val REQUEST_DIALOG_PICKED_OPTION_ID = "optionId"
		const val REQUEST_DIALOG_PICKED_OPTION = "option"
		private const val DISTRICT = 0
		private const val GAME_TYPE = 1
		private const val GAME_PAY = 2
		private const val DATE = 3
		private const val TIME = 4
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.container.adapter = adapter
		binding.container.layoutManager = LinearLayoutManager(requireContext())
		viewModel.onStartup()
		adapter.clickListener = { position ->
			when (position) {
				DISTRICT -> { viewModel.onDistrictPressed(childFragmentManager) }
				GAME_TYPE -> { startDialogWithKey(position) }
				GAME_PAY -> { startDialogWithKey(position) }
				DATE -> { viewModel.showDatePickerDialog(requireContext()).show() }
				TIME -> { viewModel.showTimePickerDialog().show(parentFragmentManager, "Timepicker") }
			}
		}
		adapter.currentRating = { rating ->
			// todo мы так делаем, чтобы лишний раз не дергать обновление всего списка и не терять touch event при adapter.submitList
			viewModel.updateRating(binding.root.findViewById(R.id.comment_text), rating) // не должно быть сайдэффекта с обновлением viewModel.uiStateFlow
		}

		binding.buttonCreate.setOnClickListener {
			viewModel.onCreateButtonPressed {
				requireContext().showToast("its a success")
			}
		}


		setFragmentResultListener(
			LocationDialogViewModel.DISTRICT_REQUEST_KEY
		) { _, result ->
			viewModel.onDistrictPicked(
				result.getString(LocationDialogViewModel.SELECTED_DISTRICT_KEY, requireContext().getString(R.string.survey_option_null))
			)
		}

		setFragmentResultListener(REQUEST_DIALOG_REQUEST_KEY) { _, result ->
			val title = result.getString(REQUEST_DIALOG_TITLE)
			val option = result.getString(REQUEST_DIALOG_PICKED_OPTION)
			val optionId = result.getInt(REQUEST_DIALOG_PICKED_OPTION_ID)
			Log.d("REQUEST_DIALOG_REQUEST_KEY", "$title, $option")

			if (title != null && option != null) {
				viewModel.onValueChanged(title, option, optionId)
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			adapter.submitList(uiState.layoutItemsList)
			binding.buttonCreate.isEnabled = uiState.isCreateButtonActive
			val buttonBackground = if (uiState.isCreateButtonActive) {
				R.drawable.btn_bkg_enabled
			} else {
				R.drawable.btn_bkg_disabled
			}
			binding.buttonCreate.setBackgroundResource(buttonBackground)
		}
	}

	private fun startDialogWithKey(key: Int) {
		val bottomSheet = RequestCreationDialog()
		bottomSheet.arguments = bundleOf(REQUEST_DIALOG_SELECT_ACTION_KEY to key)
		bottomSheet.show(childFragmentManager, bottomSheet.tag)
	}
}