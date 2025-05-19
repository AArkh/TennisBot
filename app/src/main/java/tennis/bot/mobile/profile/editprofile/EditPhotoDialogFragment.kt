package tennis.bot.mobile.profile.editprofile

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditPhotoDialogBinding

class EditPhotoDialogFragment : CoreBottomSheetDialogFragment<FragmentEditPhotoDialogBinding>() {
	override val bindingInflation: Inflation<FragmentEditPhotoDialogBinding> =
		FragmentEditPhotoDialogBinding::inflate

	companion object {
		const val OPEN_CAMERA = "OPEN_CAMERA"
		const val PICK_FROM_GALLERY = "PICK_FROM_GALLERY"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.cancelDialog.setOnClickListener {
			dialog?.dismiss()
		}

		binding.makePhoto.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				EditProfileFragment.PHOTO_DIALOG_REQUEST_KEY,
				bundleOf(EditProfileFragment.PHOTO_DIALOG_SELECTED_OPTION to OPEN_CAMERA)
			)
			dialog?.dismiss()
		}

		binding.pickFromGallery.setOnClickListener {
			requireActivity().supportFragmentManager.setFragmentResult(
				EditProfileFragment.PHOTO_DIALOG_REQUEST_KEY,
				bundleOf(EditProfileFragment.PHOTO_DIALOG_SELECTED_OPTION to PICK_FROM_GALLERY)
			)
			dialog?.dismiss()
		}
	}
}