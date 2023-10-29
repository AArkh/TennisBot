package tennis.bot.mobile.utils

import android.content.Context
import android.widget.Toast
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Int): Int = dpToPx(dp.toFloat()).roundToInt()

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}