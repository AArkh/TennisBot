package tennis.bot.mobile.feed.requestcreation

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class GaugeItemTouchListener(private val gaugeView: GaugeView) : RecyclerView.OnItemTouchListener {
	override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
		val view = rv.findChildViewUnder(e.x, e.y)
		if (view === gaugeView && gaugeView.isTouchWithinGauge(e.x, e.y)) {
			rv.requestDisallowInterceptTouchEvent(true) // Prevent RecyclerView from intercepting
			return true
		}
		return false
	}

	override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
	}

	override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
	}
}