package tennis.bot.mobile.onboarding.photopick

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhotoPickBinding
import tennis.bot.mobile.onboarding.password.PasswordFragment
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject

@AndroidEntryPoint
class PhotoPickFragment : CoreFragment<FragmentPhotoPickBinding>() {
    override val bindingInflation: Inflation<FragmentPhotoPickBinding> = FragmentPhotoPickBinding::inflate
    @Inject
    lateinit var photoPickAdapter: PhotoPickAdapter
    @Inject
    lateinit var afterPhotoPickedAdapter: AfterPhotoPickedAdapter
    private val viewModel: PhotoPickViewModel by viewModels()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            viewModel.onPickedUserImage(pickedImageUri = uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.iconsRecyclerView.adapter = photoPickAdapter
        binding.iconsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        photoPickAdapter.clickListener = { pickedCircledImage ->
            viewModel.onPickedCircledImage(pickedCircledImage)
        }
        binding.pickPhotoImage.setOnClickListener {
//            when (viewModel.uiStateFlow.value.userPickedImage) {
//                 -> {}
//                else -> {  }
//            }
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.buttonNext.setOnClickListener {
            viewModel.onButtonNextClicked {
                parentFragmentManager.traverseToAnotherFragment(PasswordFragment())
            }
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: PhotoPickUiState ->
            when (uiState) {
                is PhotoPickUiState.Loading -> {
                    binding.title.visibility = View.INVISIBLE
                    binding.pickPhotoImage.visibility = View.INVISIBLE
                    binding.titleBelow.visibility = View.INVISIBLE
                    binding.iconsRecyclerView.visibility = View.INVISIBLE
                    binding.buttonNext.isEnabled = false
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)
                }
                is PhotoPickUiState.InitialWithIconList -> {
                    binding.iconsRecyclerView.adapter = photoPickAdapter
                    photoPickAdapter.submitList(uiState.iconList)
                    photoPickAdapter.clickListener = { pickedCircledImage ->
                        viewModel.onPickedCircledImage(pickedCircledImage)
                    }

                    binding.title.visibility = View.VISIBLE
                    binding.pickPhotoImage.visibility = View.VISIBLE
                    binding.titleBelow.visibility = View.VISIBLE
                    binding.iconsRecyclerView.visibility = View.VISIBLE
                    binding.buttonNext.isEnabled = uiState.nextButtonEnabled
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_disabled)
                }
                is PhotoPickUiState.PickedPreselectedImage -> {
                    if (uiState.iconListWithSelection.any {it.isSelected}) {
                        binding.iconsRecyclerView.adapter = afterPhotoPickedAdapter
                        afterPhotoPickedAdapter.submitList(uiState.iconListWithSelection)
                        afterPhotoPickedAdapter.clickListener = { pickedCircledImage ->
                            viewModel.onPickedCircledImage(pickedCircledImage)
                        }
                        if(uiState.userPickedImage != null) {
                            binding.pickPhotoImage.setPadding(requireContext().dpToPx(0))
                        }
                        binding.pickPhotoImage.setBackgroundResource(R.drawable.circle_background)

                        binding.buttonNext.isEnabled = uiState.nextButtonEnabled
                        binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                    } else viewModel.onInitial()
                }
                is PhotoPickUiState.PickedUserImage -> {
                    binding.pickPhotoImage.load(uiState.userPickedImage) {
                        crossfade(true)
                            .transformations(CircleCropTransformation())
                    }
                    binding.pickPhotoImage.setPadding(requireContext().dpToPx(4))
                    binding.pickPhotoImage.setBackgroundResource(R.drawable.circle_photo_outline)

                    binding.iconsRecyclerView.adapter = afterPhotoPickedAdapter
                    afterPhotoPickedAdapter.submitList(uiState.iconList)
                    afterPhotoPickedAdapter.clickListener = { pickedCircledImage ->
                        viewModel.onPickedCircledImage(pickedCircledImage)
                    }

                    binding.buttonNext.isEnabled = uiState.nextButtonEnabled
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                }
                is PhotoPickUiState.Error -> {}
            }
        }
    }
}