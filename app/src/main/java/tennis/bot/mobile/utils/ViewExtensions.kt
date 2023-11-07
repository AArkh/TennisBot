package tennis.bot.mobile.utils

import android.widget.TextView

fun TextView.updateTextIfNeeded(text: CharSequence?) {
    if (this.text != text) {
        this.text = text
    }
}