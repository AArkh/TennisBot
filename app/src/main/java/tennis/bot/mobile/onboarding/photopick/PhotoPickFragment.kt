package tennis.bot.mobile.onboarding.photopick

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentPhotoPickBinding
import tennis.bot.mobile.onboarding.password.PasswordFragment
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
        binding.pickPhotoButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonSkip.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, PasswordFragment())
                .addToBackStack(PasswordFragment::class.java.name)
                .commit()
        }

        binding.buttonNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, PasswordFragment())
                .addToBackStack(PasswordFragment::class.java.name)
                .commit()
        }

        subscribeToFlowOn(viewModel.uiStateFlow) { uiState: PhotoPickUiState ->
            when (uiState) {
                is PhotoPickUiState.Loading -> {
                    binding.title.visibility = View.INVISIBLE
                    binding.pickPhotoButton.visibility = View.INVISIBLE
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
                    binding.pickPhotoButton.visibility = View.VISIBLE
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
                        binding.buttonNext.isEnabled = uiState.nextButtonEnabled
                        binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                    } else viewModel.onInitial()
                }
                is PhotoPickUiState.PickedUserImage -> {
                    binding.buttonNext.isEnabled = uiState.nextButtonEnabled
                    binding.buttonNext.setBackgroundResource(R.drawable.btn_bkg_enabled)
                }
                is PhotoPickUiState.Error -> {}
            }
        }
    }
}