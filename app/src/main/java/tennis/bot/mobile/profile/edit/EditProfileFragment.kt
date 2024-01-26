package tennis.bot.mobile.profile.edit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditProfileBinding
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
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