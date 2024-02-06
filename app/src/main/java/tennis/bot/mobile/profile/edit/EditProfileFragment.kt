package tennis.bot.mobile.profile.edit

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
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
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileFragment : CoreFragment<FragmentEditProfileBinding>() {

	override val bindingInflation: Inflation<FragmentEditProfileBinding> = FragmentEditProfileBinding::inflate
	private val viewModel: EditProfileViewModel by viewModels()
	@Inject
	lateinit var adapter: EditProfileAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		adapter.clickListener = { index ->
			when(index) {
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

		setFragmentResultListener(NAME_SURNAME_REQUEST_KEY) { _, result ->
			val updatedNameSurname = result.getString(SELECTED_NAME_SURNAME)
			viewModel.onUpdatedValues(EditProfileAdapter.CHANGE_NAME_INDEX, updatedNameSurname ?: getString(R.string.survey_option_null))
		}

		setFragmentResultListener(LOCATION_STRINGS_REQUEST_KEY) { _, result ->
			val updatedLocationStrings = result.getStringArrayList(SELECTED_LOCATION_STRINGS)
			viewModel.updateLocation(
				updatedLocationStrings?.getOrNull(0), updatedLocationStrings?.getOrNull(1), updatedLocationStrings?.getOrNull(2)
			)
		}

		setFragmentResultListener(PHONE_NUMBER_REQUEST_KEY) { _, result ->
			val updatedPhoneNumber = result.getString(SELECTED_PHONE_NUMBER)
			viewModel.onUpdatedValues(EditProfileAdapter.CHANGE_PHONE_INDEX, updatedPhoneNumber ?: getString(R.string.survey_option_null))
		}

		setFragmentResultListener(TELEGRAM_REQUEST_KEY) { _, result ->
			val updatedTelegram = result.getString(SELECTED_TELEGRAM)
			viewModel.onUpdatedValues(EditProfileAdapter.CHANGE_TELEGRAM_INDEX, updatedTelegram ?: getString(R.string.survey_option_null))
		}

		subscribeToFlowOn(viewModel.uiStateFlow) {uiState: EditProfileUiState ->
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

	private fun onLoadingProfileImage(profileImageUrl: String?){
		if (profileImageUrl == null) return

		if (profileImageUrl.contains("default")) {
			val resourceId = getDefaultDrawableResourceId(requireContext(), profileImageUrl.removeSuffix(".png"))
			binding.accountPhoto.visibility = View.VISIBLE
			if (resourceId != null) binding.accountPhoto.setImageResource(resourceId)
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
}