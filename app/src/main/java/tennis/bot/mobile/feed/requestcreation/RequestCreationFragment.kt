package tennis.bot.mobile.feed.requestcreation

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
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
		adapter.listener?.let { binding.container.addOnItemTouchListener(it) }
		viewModel.onStartup()
		adapter.clickListener = { position ->
			when (position) {
				DISTRICT -> {}
				GAME_TYPE -> { startDialogWithKey(position) }
				GAME_PAY -> { startDialogWithKey(position) }
				DATE -> { showDatePickerDialog() }
				TIME -> { showTimePickerDialog() }
			}
		}

		setFragmentResultListener(REQUEST_DIALOG_REQUEST_KEY) { _, result ->
			val title = result.getString(REQUEST_DIALOG_TITLE)
			val option = result.getString(REQUEST_DIALOG_PICKED_OPTION)

			Log.d("123456", "Result is received: $title $option")

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
		val startTimePicker = TimePickerDialog.newInstance({ _, selectedHour, roundedMinute, _ ->
			viewModel.onTimePicked(String.format("%02d:%02d", selectedHour, roundedMinute))
		},true)
		startTimePicker.setTimeInterval(1, 30)
		startTimePicker.show(parentFragmentManager, "Timepicker")
	}
}