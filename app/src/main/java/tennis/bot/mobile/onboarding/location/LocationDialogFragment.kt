package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment

@AndroidEntryPoint
class LocationDialogFragment: CountryCodesDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}