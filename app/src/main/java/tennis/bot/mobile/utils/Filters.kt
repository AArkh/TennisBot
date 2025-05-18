package tennis.bot.mobile.utils

import android.text.InputFilter
import android.text.Spanned

open class LetterInputFilter : InputFilter {
	override fun filter(
		source: CharSequence?,
		start: Int,
		end: Int,
		dest: Spanned?,
		dstart: Int,
		dend: Int
	): CharSequence? {
		for (i in start until end) {
			if (!Character.isLetter(source!![i])) {
				return ""
			}
		}
		return null
	}
}