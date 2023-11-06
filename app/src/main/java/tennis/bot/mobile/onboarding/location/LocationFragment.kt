package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLocationBinding
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding

class LocationFragment : CoreFragment<FragmentLocationBinding>() {
    override val bindingInflation: Inflation<FragmentLocationBinding> = FragmentLocationBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}