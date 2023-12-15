package tennis.bot.mobile.onboarding.account

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import javax.inject.Inject

class RatingsInfoView @Inject constructor(
	@ApplicationContext private val context: Context,
): LinearLayout(context) {

	init {
		// Inflate the custom layout
		LayoutInflater.from(context).inflate(R.layout.rating_custom_view, this, true)

		val singleRatingValue = findViewById<TextView>(R.id.single_rating_value)
		val doublesRatingValue = findViewById<TextView>(R.id.doubles_rating_value)

	}
}