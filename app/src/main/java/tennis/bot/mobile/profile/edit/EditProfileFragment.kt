package tennis.bot.mobile.profile.edit

import android.os.Bundle
import android.view.View
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
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.NAME_SURNAME_REQUEST_KEY
import tennis.bot.mobile.profile.edit.EditProfileViewModel.Companion.SELECTED_NAME_SURNAME
import tennis.bot.mobile.profile.matches.MatchesFragment
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

		adapter.clickListener = { command ->
			when(command) {
				EditProfileAdapter.CHANGE_NAME -> {
					parentFragmentManager.beginTransaction()
						.replace(R.id.fragment_container_view, EditNameSurnameFragment())
						.addToBackStack(EditNameSurnameFragment::class.java.name)
						.commit()
				}
			}
		}

		setFragmentResultListener(NAME_SURNAME_REQUEST_KEY) { _, result ->
			val updatedNameSurname = result.getString(SELECTED_NAME_SURNAME)
			viewModel.onUpdatedValues(0, updatedNameSurname ?: getString(R.string.survey_option_null))
		}

		subscribeToFlowOn(viewModel.uiStateFlow) {uiState: EditProfileUiState ->
			onLoadingProfileImage(uiState.profilePicture)
			viewModel.onStartup()

			adapter.submitList(uiState.categoriesList)

		}
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

}