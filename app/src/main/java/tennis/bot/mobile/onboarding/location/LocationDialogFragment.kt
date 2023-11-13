package tennis.bot.mobile.onboarding.location

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class LocationDialogFragment: CountryCodesDialogFragment() {

    @Inject
    lateinit var locationAdapter: LocationAdapter
    private val viewModel: LocationDialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countriesListRv.adapter = locationAdapter

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiStateFlow.collect { countryList ->
                    locationAdapter.submitList(countryList)
                }
            }
        }
//        binding.searchBarEt.addTextChangedListener {
//            viewModel.onSearchInput(it.toString())
//        }

//              how to pass arguments to the fragment's viewmodel

        locationAdapter.clickListener = {
            requireActivity().supportFragmentManager.setFragmentResult(
                COUNTRY_REQUEST_KEY,
                bundleOf(SELECTED_COUNTRY_KEY to it.countryName)
            )


        }


    }
    companion object {
        const val COUNTRY_REQUEST_KEY = "COUNTRY_KEY"
        const val SELECTED_COUNTRY_KEY = "SELECTED_COUNTRY_KEY"
        const val CITY_REQUEST_KEY = "COUNTRY_KEY"
        const val SELECTED_CITY_KEY = "SELECTED_COUNTRY_KEY"
        const val DISTRICT_REQUEST_KEY = "COUNTRY_KEY"
        const val SELECTED_DISTRICT_KEY = "SELECTED_COUNTRY_KEY"

    }
}