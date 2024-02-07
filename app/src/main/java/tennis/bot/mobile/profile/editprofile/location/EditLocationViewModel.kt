package tennis.bot.mobile.profile.editprofile.location

import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.onboarding.location.LocationViewModel
import tennis.bot.mobile.onboarding.survey.OnboardingRepository
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel
import javax.inject.Inject

@HiltViewModel
class EditLocationViewModel @Inject constructor(
	private val locationRepository: LocationRepository,
	private val onboardingRepository: OnboardingRepository
) : LocationViewModel(locationRepository, onboardingRepository) {

	fun onChangeLocation(activity: FragmentActivity, country: String?, city: String?, district: String?, navigationCallback: () -> Unit) {
		activity.supportFragmentManager.setFragmentResult(
			EditProfileViewModel.LOCATION_STRINGS_REQUEST_KEY,
			bundleOf(
				EditProfileViewModel.SELECTED_LOCATION_STRINGS to
					arrayListOf(country, city, district)
			)
		)
		navigationCallback.invoke()
	}



}