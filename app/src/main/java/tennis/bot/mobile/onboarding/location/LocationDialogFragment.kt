package tennis.bot.mobile.onboarding.location

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.onboarding.phone.CountryCodesDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class LocationDialogFragment : CountryCodesDialogFragment() {

    @Inject
    lateinit var locationAdapter: LocationAdapter
    private val viewModel: LocationDialogViewModel by viewModels()
    override var hasFixedHeight: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.countriesListRv.adapter = locationAdapter

        locationAdapter.clickListener = {countryItem ->
            viewModel.onPickedListItem(countryItem, requireActivity())
            dialog?.dismiss()
        }

        binding.tryAgainTv.setOnClickListener {
            viewModel.onLoadingSelectedList()
        }

        binding.searchBarEt.addTextChangedListener {
            viewModel.onSearchInputChanged(it.toString())
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: LocationDialogUiState ->
            when (uiState) {
                is LocationDialogUiState.Loading -> {
                    binding.loadingBar.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                }
                is LocationDialogUiState.DataPassed -> {
                    binding.loadingBar.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE

                    locationAdapter.submitList(uiState.dataList)
                }
                is LocationDialogUiState.Error -> {
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.tryAgainTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.loadingBar.visibility = View.GONE
                }
            }
        }
    }


}