package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLocationBinding
import tennis.bot.mobile.onboarding.photopick.PhotoPickFragment

@AndroidEntryPoint
open class LocationFragment : CoreFragment<FragmentLocationBinding>() {

    override val bindingInflation: Inflation<FragmentLocationBinding> = FragmentLocationBinding::inflate
    private val viewModel: LocationViewModel by viewModels()

    companion object {
        const val SELECT_ACTION_KEY = "somsdfadfg"
        const val SELECT_COUNTRY = "SELECT_COUNTRY"
        const val SELECT_CITY = "SELECT_CITY"
        const val SELECT_DISTRICT = "SELECT_DISTRICT"
        const val SELECTED_COUNTRY_NAME_KEY = "SELECTED_COUNTRY_NAME_KEY"
        const val SELECTED_CITY_NAME_KEY = "SELECTED_CITY_NAME_KEY"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countryPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            bottomSheet.arguments = bundleOf(SELECT_ACTION_KEY to SELECT_COUNTRY)
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.cityPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            val country = when(val location = viewModel.uiStateFlow.value) {
                is LocationUiState.CountrySelected -> location.country
                is LocationUiState.CitySelected -> location.country
                is LocationUiState.DistrictSelected -> location.country
                else -> return@setOnClickListener
            }
            bottomSheet.arguments = bundleOf(
                SELECT_ACTION_KEY to SELECT_CITY,
                SELECTED_COUNTRY_NAME_KEY to country
            )
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        binding.districtPickLayout.setOnClickListener {
            val bottomSheet = LocationDialogFragment()
            val country = when(val location = viewModel.uiStateFlow.value) {
                is LocationUiState.CountrySelected -> location.country
                is LocationUiState.CitySelected -> location.country
                is LocationUiState.DistrictSelected -> location.country
                else -> return@setOnClickListener
            }
            val city = when(val location = viewModel.uiStateFlow.value) {
                is LocationUiState.CitySelected -> location.city
                is LocationUiState.DistrictSelected -> location.city
                else -> return@setOnClickListener
            }
            bottomSheet.arguments = bundleOf(
                SELECT_ACTION_KEY to SELECT_DISTRICT,
                SELECTED_COUNTRY_NAME_KEY to country,
                SELECTED_CITY_NAME_KEY to city,
            )
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
        }

        setFragmentResultListener(
            LocationDialogViewModel.COUNTRY_REQUEST_KEY
        ) { _, result ->
            val countryResult =
                result.getString(LocationDialogViewModel.SELECTED_COUNTRY_KEY, "Страна")
            viewModel.onCountrySelected(countryResult)
        }

        setFragmentResultListener(
            LocationDialogViewModel.CITY_REQUEST_KEY
        ) { _, result ->
            val cityResult = result.getString(LocationDialogViewModel.SELECTED_CITY_KEY, "Город")
            viewModel.onCitySelected(binding.countryTv.text.toString(), cityResult)
        }

        setFragmentResultListener(
            LocationDialogViewModel.DISTRICT_REQUEST_KEY
        ) { _, result ->
            val districtResult =
                result.getString(LocationDialogViewModel.SELECTED_DISTRICT_KEY, "Район")
            viewModel.onDistrictSelected(
                binding.countryTv.text.toString(),
                binding.cityTv.text.toString(),
                districtResult
            )
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.buttonNext.setOnClickListener {
            viewModel.recordLocationValues(
                selectedCountry = binding.countryTv.text.toString(),
                selectedCity = binding.cityTv.text.toString(),
                selectedDistrict = binding.districtTv.text.toString()
            )
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, PhotoPickFragment())
                .addToBackStack(PhotoPickFragment::class.java.name)
                .commit()
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: LocationUiState ->
            when (uiState) {
                LocationUiState.Initial -> {
                    binding.titleTv.visibility = View.VISIBLE
                    binding.countryPickLayout.visibility = View.VISIBLE
                    binding.countryPickLayout.setBackgroundResource(R.drawable.outline_16dp_2dp_active)
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
                    binding.cityPickLayout.setBackgroundResource(R.drawable.outline_16dp_2dp_active)
                    binding.districtPickLayout.setBackgroundResource(R.drawable.country_button_outline)
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)

                    if (!uiState.nextButtonEnabled) {
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
                    binding.districtPickLayout.setBackgroundResource(R.drawable.outline_16dp_2dp_active)
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)

                    if (!uiState.nextButtonEnabled) {
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
                    binding.countryPickLayout.visibility = View.GONE
                    binding.cityPickLayout.visibility = View.GONE
                    binding.districtPickLayout.visibility = View.GONE
                    binding.titleTv.visibility = View.GONE
                    binding.buttonNext.visibility = View.GONE
                }

                LocationUiState.Error -> {}
            }
        }
    }

}