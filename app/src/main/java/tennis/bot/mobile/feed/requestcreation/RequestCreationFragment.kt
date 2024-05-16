package tennis.bot.mobile.feed.requestcreation

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.wdullaer.materialdatetimepicker.time.Timepoint
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import tennis.bot.mobile.onboarding.location.LocationDialogViewModel
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
				DATE -> { showDatePickerDialog() }
				TIME -> { showTimePickerDialog() }
			}
		}
		adapter.currentRating = { rating ->
			// todo мы так делаем, чтобы лишний раз не дергать обновление всего списка и не терять touch event при adapter.submitList
			viewModel.updateRating(binding.root.findViewById(R.id.comment_text), rating) // не должно быть сайдэффекта с обновлением viewModel.uiStateFlow
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

			if (title != null && option != null) {
				viewModel.onValueChanged(title, option)
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			adapter.submitList(uiState.layoutItemsList)
		}
	}

	private fun startDialogWithKey(key: Int) {
		val bottomSheet = RequestCreationDialog()
		bottomSheet.arguments = bundleOf(REQUEST_DIALOG_SELECT_ACTION_KEY to key)
		bottomSheet.show(childFragmentManager, bottomSheet.tag)
	}

	private fun showDatePickerDialog() {
		val calendar = Calendar.getInstance()
		val currentYear = calendar.get(Calendar.YEAR)
		val currentMonth = calendar.get(Calendar.MONTH)
		val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

		val datePickerDialog = DatePickerDialog(
			requireContext(),
			{ _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
				val selectedDate = "${String.format("%02d", dayOfMonth)}.${String.format("%02d", month + 1)}.$year"
				Log.d("123456", "Selected Date: $selectedDate")
				viewModel.onDatePicked(selectedDate)
			},
			currentYear,
			currentMonth,
			currentDay
		)
		datePickerDialog.datePicker.minDate = System.currentTimeMillis()
		datePickerDialog.show()
	}

	private fun showTimePickerDialog() {
		val currentTime = Calendar.getInstance()
		val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
		val currentMinute = currentTime.get(Calendar.MINUTE)

		val timePickerDialog = TimePickerDialog.newInstance({ _, selectedHour, roundedMinute, _ ->
			viewModel.onTimePicked(String.format("%02d:%02d", selectedHour, roundedMinute))
		}, currentHour, currentMinute, true)

		timePickerDialog.setMinTime(Timepoint(currentHour, currentMinute))
		timePickerDialog.setTimeInterval(1, 30)
		timePickerDialog.show(parentFragmentManager, "Timepicker")
	}
}