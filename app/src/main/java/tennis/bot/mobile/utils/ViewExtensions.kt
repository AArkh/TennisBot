package tennis.bot.mobile.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

fun TextView.updateTextIfNeeded(text: CharSequence?) {
    if (this.text != text) {
        this.text = text
    }
}

fun EditText.showKeyboard(activity: Activity) {
    val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    requestFocus()
    inputMethodManager.showSoftInput(this, 0)
}