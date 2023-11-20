package tennis.bot.mobile.onboarding.photopick

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentLocationBinding
import tennis.bot.mobile.databinding.FragmentPhotoPickBinding
import tennis.bot.mobile.onboarding.location.LocationAdapter
import tennis.bot.mobile.onboarding.location.LocationDialogUiState
import tennis.bot.mobile.onboarding.location.LocationDialogViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PhotoPickFragment() : CoreFragment<FragmentPhotoPickBinding>() {
    override val bindingInflation: Inflation<FragmentPhotoPickBinding> = FragmentPhotoPickBinding::inflate
    @Inject
    lateinit var photoPickAdapter: PhotoPickAdapter
    private val viewModel: PhotoPickViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconsRecyclerView.adapter = photoPickAdapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: PhotoPickUiState ->
            when (uiState) {
                is PhotoPickUiState.Loading -> {}
                is PhotoPickUiState.DataPassed -> {
                    photoPickAdapter.submitList(uiState.iconList)
                }
                is PhotoPickUiState.Error -> {}
            }
        }
    }
}