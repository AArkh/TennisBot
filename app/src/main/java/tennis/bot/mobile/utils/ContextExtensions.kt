package tennis.bot.mobile.utils

import android.content.Context
import kotlin.math.roundToInt

fun Context.dpToPx(dp: Int): Int = dpToPx(dp.toFloat()).roundToInt()

fun Context.dpToPx(dp: Float): Float = dp * resources.displayMetrics.density