package tennis.bot.mobile.onboarding.survey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSurveyBinding
import tennis.bot.mobile.databinding.FragmentSurveyResultsBinding


class SurveyResultsFragment : CoreFragment<FragmentSurveyResultsBinding>() {
	override val bindingInflation: Inflation<FragmentSurveyResultsBinding> = FragmentSurveyResultsBinding::inflate

}