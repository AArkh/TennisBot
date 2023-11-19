package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLocationBinding
import tennis.bot.mobile.databinding.FragmentPhoneInputBinding
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment
import tennis.bot.mobile.onboarding.phone.PhoneInputUiState
import tennis.bot.mobile.utils.updateTextIfNeeded

@AndroidEntryPoint
class LocationFragment : CoreFragment<FragmentLocationBinding>() {
    override val bindingInflation: Inflation<FragmentLocationBinding> = FragmentLocationBinding::inflate

    val viewModel : LocationViewModel by viewModels()

    companion object {
        const val SOME_KEY = "somsdfadfg"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            bottomSheet.arguments = bundleOf(SOME_KEY to "country")
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.cityPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            bottomSheet.arguments = bundleOf(
                SOME_KEY to "city",
                )
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.districtPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            bottomSheet.arguments = bundleOf(
                SOME_KEY to "district", // other keys
                "selected_country" to "Russia", // итд
            )
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        setFragmentResultListener(
            LocationDialogFragment.COUNTRY_REQUEST_KEY
        ) { _, result ->
            val countryResult = result.getString(LocationDialogFragment.SELECTED_COUNTRY_KEY, "Страна")
            viewModel.onCountrySelected(countryResult)
        }

        setFragmentResultListener(
            LocationDialogFragment.CITY_REQUEST_KEY
        ) { _, result ->
            val cityResult = result.getString(LocationDialogFragment.SELECTED_CITY_KEY, "Город")
            viewModel.onCitySelected(binding.countryTv.text.toString(), cityResult)
        }

        setFragmentResultListener(
            LocationDialogFragment.DISTRICT_REQUEST_KEY
        ) { _, result ->
            val districtResult = result.getString(LocationDialogFragment.SELECTED_DISTRICT_KEY, "Район")
            viewModel.onDistrictSelected(binding.countryTv.text.toString(), binding.cityTv.text.toString(), districtResult)
        }

        binding.buttonNext.setOnClickListener {
            Toast.makeText(context, "We move onward", Toast.LENGTH_SHORT).show()
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: LocationUiState ->
            when(uiState) {
                LocationUiState.Initial -> {
                    binding.titleTv.visibility = View.VISIBLE
                    binding.countryPickLayout.visibility = View.VISIBLE
                    binding.countryPickLayout.setBackgroundResource(R.drawable.country_button_outline_active)
                    binding.cityPickLayout.visibility = View.INVISIBLE
                    binding.districtPickLayout.visibility = View.INVISIBLE
                    binding.buttonNext.visibility = View.VISIBLE
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)

                    binding.countryTv.text = "Страна"
                    binding.cityTv.text = "Город"
                    binding.districtTv.text = "Район"
                }
                is LocationUiState.CountrySelected -> {
                    binding.countryTv.text = uiState.country
                    binding.cityTv.text = "Город"
                    binding.districtTv.text = "Район"

                    binding.cityPickLayout.visibility = View.INVISIBLE
                    binding.districtPickLayout.visibility = View.INVISIBLE
                    binding.countryPickLayout.setBackgroundResource(R.drawable.country_button_outline)
                    binding.cityPickLayout.setBackgroundResource(R.drawable.country_button_outline_active)
                    binding.districtPickLayout.setBackgroundResource(R.drawable.country_button_outline)
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)

                    if(!uiState.nextButtonEnabled) {
                        binding.cityPickLayout.visibility = View.VISIBLE
                        binding.districtPickLayout.visibility = View.INVISIBLE
                    } else {
                        binding.buttonNext.isEnabled = true
                        binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                    }
                }
                is LocationUiState.CitySelected -> {
                    binding.countryTv.text = uiState.country
                    binding.cityTv.text = uiState.city
                    binding.districtTv.text = "Район"

                    binding.districtPickLayout.visibility = View.INVISIBLE
                    binding.cityPickLayout.setBackgroundResource(R.drawable.country_button_outline)
                    binding.districtPickLayout.setBackgroundResource(R.drawable.country_button_outline_active)
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)

                    if(!uiState.nextButtonEnabled) {
                        binding.districtPickLayout.visibility = View.VISIBLE
                    } else {
                        binding.buttonNext.isEnabled = true
                        binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                    }
                }
                is LocationUiState.DistrictSelected -> {
                    binding.countryTv.text = uiState.country
                    binding.cityTv.text = uiState.city
                    binding.districtTv.text = uiState.district

                    binding.districtPickLayout.setBackgroundResource(R.drawable.country_button_outline)
                    binding.buttonNext.isEnabled = true
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                }
                LocationUiState.Loading -> {
                    // loading screen, everything else is hidden
                    binding.countryPickLayout.visibility = View.GONE
                    binding.cityPickLayout.visibility = View.GONE
                    binding.districtPickLayout.visibility = View.GONE
                    binding.titleTv.visibility = View.GONE
                    binding.buttonNext.visibility = View.GONE
                }
                LocationUiState.Error -> {

                } // error logic
            }
        }
    }

}