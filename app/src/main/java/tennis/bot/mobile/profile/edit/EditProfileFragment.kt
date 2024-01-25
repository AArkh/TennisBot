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


@AndroidEntryPoint
class EditProfileFragment : CoreFragment<FragmentEditProfileBinding>() {

	override val bindingInflation: Inflation<FragmentEditProfileBinding> = FragmentEditProfileBinding::inflate
	private val viewModel: EditProfileViewModel by viewModels()
	private lateinit var adapter: EditProfileAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		subscribeToFlowOn(viewModel.uiStateFlow) {uiState: EditProfileUiState ->
			binding.accountPhoto.load(uiState.profilePicture) {crossfade(true)}

			adapter.submitList(viewModel.editProfileItems)

		}
	}

}