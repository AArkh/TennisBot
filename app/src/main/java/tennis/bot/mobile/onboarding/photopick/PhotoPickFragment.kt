package tennis.bot.mobile.onboarding.photopick

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhotoPickBinding
import javax.inject.Inject

@AndroidEntryPoint
class PhotoPickFragment() : CoreFragment<FragmentPhotoPickBinding>() {
    override val bindingInflation: Inflation<FragmentPhotoPickBinding> = FragmentPhotoPickBinding::inflate
    @Inject
    lateinit var photoPickAdapter: PhotoPickAdapter
    @Inject
    lateinit var afterPhotoPickedAdapter: AfterPhotoPickedAdapter
    private val viewModel: PhotoPickViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconsRecyclerView.adapter = photoPickAdapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        binding.pickPhotoButton.setOnClickListener {
            val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: $uri")
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        photoPickAdapter.clickListener = {pickedCircledImage ->
            viewModel.onPickedCircledImage(pickedCircledImage)

        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: PhotoPickUiState ->
            when (uiState) {
                is PhotoPickUiState.Loading -> {}
                is PhotoPickUiState.InitialWithIconList -> {
                    photoPickAdapter.submitList(uiState.iconList)
                }
                is PhotoPickUiState.PickedPreselectedImage -> {
                    binding.iconsRecyclerView.adapter = afterPhotoPickedAdapter
                    photoPickAdapter.submitList(uiState.iconListWithSelection)
                }
                is PhotoPickUiState.PickedUserImage -> {}
                is PhotoPickUiState.Error -> {}
            }
        }
    }
}