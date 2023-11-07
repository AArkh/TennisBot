package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLocationBinding
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment

class LocationFragment : CoreFragment<FragmentLocationBinding>() {
    override val bindingInflation: Inflation<FragmentLocationBinding> = FragmentLocationBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            bottomSheet.show(childFragmentManager, bottomSheet.getTag())
        }

        setFragmentResultListener(
            LocationDialogFragment.COUNTRY_REQUEST_KEY
        ) { requestKey, result ->
            binding.countryTv.text = result.getString(LocationDialogFragment.SELECTED_COUNTRY_KEY)
        }
    }

}