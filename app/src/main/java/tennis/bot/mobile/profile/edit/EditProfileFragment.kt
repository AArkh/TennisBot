package tennis.bot.mobile.profile.edit

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditProfileBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.LOCATION_STRINGS_REQUEST_KEY
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.NAME_SURNAME_REQUEST_KEY
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.PHONE_NUMBER_REQUEST_KEY
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.SELECTED_LOCATION_STRINGS
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.SELECTED_NAME_SURNAME
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.SELECTED_PHONE_NUMBER
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.SELECTED_TELEGRAM
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.TELEGRAM_REQUEST_KEY
import tennis.bot.mobile.profile.edit.location.EditLocationFragment
import tennis.bot.mobile.profile.edit.namesurname.EditNameSurnameFragment
import tennis.bot.mobile.profile.edit.telegram.EditTelegramFragment
import tennis.bot.mobile.utils.showToast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileFragment : CoreFragment<FragmentEditProfileBinding>() {

	override val bindingInflation: Inflation<FragmentEditProfileBinding> = FragmentEditProfileBinding::inflate
	private val viewModel: EditProfileViewModel by viewModels()
	private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

	@Inject
	lateinit var adapter: EditProfileAdapter
	private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			Log.d("PhotoPicker", "Selected URI: $uri")
			viewModel.onPickedProfilePic(uri)
		} else {
			Log.d("PhotoPicker", "No media selected")
		}
	}

	companion object {
		const val PHOTO_DIALOG_REQUEST_KEY = "PHOTO_DIALOG_REQUEST_KEY"
		const val PHOTO_DIALOG_SELECTED_OPTION = "PHOTO_DIALOG_SELECTED_OPTION"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				if (result.resultCode == Activity.RESULT_OK && result.data != null) {
					val imageBitmap = result.data?.extras?.get("data") as Bitmap
					getImageUriFromBitmap(imageBitmap)
					viewModel.onPickedProfilePic(getImageUriFromBitmap(imageBitmap))

				} else {
					requireContext().showToast("Unrecognized request code")
				}
			}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		super.onViewCreated(view, savedInstanceState)

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.accountPhoto.setOnClickListener {
			lifecycleScope.launch {
				delay(100L) // wait for keyboard to hide
				val bottomSheet = EditPhotoDialogFragment()
				bottomSheet.show(childFragmentManager, bottomSheet.tag)
			}
		}

		adapter.clickListener = { index ->
			when (index) {
				EditProfileAdapter.CHANGE_NAME_INDEX -> {
					goToAnotherFragment(EditNameSurnameFragment())
				}

				EditProfileAdapter.CHANGE_BIRTHDAY_INDEX -> {
					showDatePickerDialog()
				}

				EditProfileAdapter.CHANGE_LOCATION_INDEX -> {
					goToAnotherFragment(EditLocationFragment())
				}

				EditProfileAdapter.CHANGE_PHONE_INDEX -> {
					goToAnotherFragment(EditPhoneFragment())
				}

				EditProfileAdapter.CHANGE_TELEGRAM_INDEX -> {
					goToAnotherFragment(EditTelegramFragment())
				}
			}
		}

		setFragmentResultListener(PHOTO_DIALOG_REQUEST_KEY) { _, result ->
			lifecycleScope.launch {
				delay(180L) // to wait until bottom shit get closed w anim
				when (result.getString(PHOTO_DIALOG_SELECTED_OPTION)) {
					EditPhotoDialogFragment.OPEN_CAMERA -> {
						val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
						cameraLauncher.launch(callCameraIntent)
					}

					EditPhotoDialogFragment.PICK_FROM_GALLERY -> {
						pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
					}
				}
			}
		}

		setFragmentResultListener(NAME_SURNAME_REQUEST_KEY) { _, result ->
			val updatedNameSurname = result.getString(SELECTED_NAME_SURNAME)
			viewModel.onUpdatedValues(
				EditProfileAdapter.CHANGE_NAME_INDEX,
				updatedNameSurname ?: getString(R.string.survey_option_null)
			)
		}

		setFragmentResultListener(LOCATION_STRINGS_REQUEST_KEY) { _, result ->
			val updatedLocationStrings = result.getStringArrayList(SELECTED_LOCATION_STRINGS)
			viewModel.updateLocation(
				updatedLocationStrings?.getOrNull(0), updatedLocationStrings?.getOrNull(1), updatedLocationStrings?.getOrNull(2)
			)
		}

		setFragmentResultListener(PHONE_NUMBER_REQUEST_KEY) { _, result ->
			val updatedPhoneNumber = result.getString(SELECTED_PHONE_NUMBER)
			viewModel.onUpdatedValues(
				EditProfileAdapter.CHANGE_PHONE_INDEX,
				updatedPhoneNumber ?: getString(R.string.survey_option_null)
			)
		}

		setFragmentResultListener(TELEGRAM_REQUEST_KEY) { _, result ->
			val updatedTelegram = result.getString(SELECTED_TELEGRAM)
			viewModel.onUpdatedValues(
				EditProfileAdapter.CHANGE_TELEGRAM_INDEX,
				updatedTelegram ?: getString(R.string.survey_option_null)
			)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: EditProfileUiState ->
			onLoadingProfileImage(uiState.profilePicture)
			viewModel.onStartup()
			adapter.submitList(uiState.categoriesList)

		}
	}

	private fun goToAnotherFragment(fragment: Fragment) {
		parentFragmentManager.beginTransaction()
			.replace(R.id.fragment_container_view, fragment)
			.addToBackStack(fragment::class.java.name)
			.commit()
	}

	private fun onLoadingProfileImage(profileImageUrl: String?) {
		if (profileImageUrl == null) return

		if (profileImageUrl.contains("default")) {
			val resourceId = getDefaultDrawableResourceId(
				requireContext(),
				profileImageUrl.removeSuffix(".png")
			) // add logic for changing cached picture
			binding.accountPhoto.visibility = View.VISIBLE
			if (resourceId != null) binding.accountPhoto.load(resourceId)
			binding.placeholderPhoto.visibility = View.GONE
		} else {
			binding.accountPhoto.visibility = View.VISIBLE
			binding.accountPhoto.load(AccountPageAdapter.IMAGES_LINK + profileImageUrl) { crossfade(true) }
			binding.placeholderPhoto.visibility = View.GONE
		}
	}

	private fun showDatePickerDialog() {
		val calendar = Calendar.getInstance()
		val currentYear = calendar.get(Calendar.YEAR)
		val currentMonth = calendar.get(Calendar.MONTH)
		val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

		val datePickerDialog = DatePickerDialog(
			requireContext(),
			{ _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
				val selectedDate = "$dayOfMonth/${month + 1}/$year"
				Log.d("123456", "Selected Date: $selectedDate")
				viewModel.onUpdatedValues(EditProfileAdapter.CHANGE_BIRTHDAY_INDEX, selectedDate)
				viewModel.onStartup()
			},
			currentYear,
			currentMonth,
			currentDay
		)
		datePickerDialog.show()
	}

	fun getImageUriFromBitmap(bitmap: Bitmap): Uri{
		val bytes = ByteArrayOutputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
		val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver, bitmap, "Title", null)
		return Uri.parse(path.toString())
	}
}