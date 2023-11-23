package tennis.bot.mobile.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Int): Int = dpToPx(dp.toFloat()).roundToInt()

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.showToast(message: String) {
    AppCoroutineScopes.mainScope.launch {
        Toast.makeText(this@showToast, message, Toast.LENGTH_SHORT).show()
    }
}

fun Context.showToast(@StringRes messageRes: Int) {
    AppCoroutineScopes.mainScope.launch {
        Toast.makeText(this@showToast, messageRes, Toast.LENGTH_SHORT).show()
    }
}

fun Context.toActivitySafe(): Activity? {
    var context = this
    while (context !is Activity && context is ContextWrapper) {
        context = context.baseContext
    }
    return context as? Activity
}

fun Context.hideKeyboard(focusView: View? = null) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = focusView
        ?: toActivitySafe()?.currentFocus
        ?: toActivitySafe()?.window?.decorView
        ?: return
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    view.clearFocus()
}