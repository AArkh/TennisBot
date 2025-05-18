package tennis.bot.mobile.profile.editprofile.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.onboarding.location.LocationFragment

@AndroidEntryPoint
class EditLocationFragment: LocationFragment() {

	private val viewModel: EditLocationViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonNext.text = requireContext().getString(R.string.button_change)
		binding.buttonNext.setOnClickListener {
			viewModel.onChangeLocation(requireActivity(),
				country = binding.countryTv.text.toString(),
				city = binding.cityTv.text.toString(),
				district = binding.districtTv.text.toString()) {
				parentFragmentManager.popBackStack()
			}
		}

	}
}
