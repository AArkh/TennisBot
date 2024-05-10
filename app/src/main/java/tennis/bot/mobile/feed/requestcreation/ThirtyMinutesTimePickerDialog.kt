package tennis.bot.mobile.feed.requestcreation

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker

class ThirtyMinutesTimePickerDialog(
	context: Context,
	listener: OnTimeSetListener,
	hourOfDay: Int,
	minute: Int,
	is24HourView: Boolean
) : TimePickerDialog(context, listener, hourOfDay, minute, is24HourView) {

	override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
		val adjustedMinute = roundToNearest30(minute)
		super.onTimeChanged(view, hourOfDay, adjustedMinute)
	}

	private fun roundToNearest30(minute: Int): Int {
		val remainder = minute % 30
		return if (remainder < 15) {
			minute - remainder
		} else {
			minute + (30 - remainder)
		}
	}
}