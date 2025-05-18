package tennis.bot.mobile.profile.appsetttings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAppSettingsBinding
import tennis.bot.mobile.onboarding.login.LoginDialogFragment
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel
import javax.inject.Inject

@AndroidEntryPoint
class AppSettingsFragment : CoreFragment<FragmentAppSettingsBinding>() {
	override val bindingInflation: Inflation<FragmentAppSettingsBinding> = FragmentAppSettingsBinding::inflate
	private val viewModel: AppSettingsViewModel by viewModels()
	@Inject
	lateinit var adapter: AppSettingsAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		adapter.clickListener = {index ->
			when(index) {
				AppSettingsAdapter.NOTIFICATIONS_SWITCH -> {}
				AppSettingsAdapter.SEND_EMAIL -> {}
				AppSettingsAdapter.SEND_TELEGRAM -> { openLink(LoginDialogFragment.CHAT_URL) }
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: AppSettingsUiState ->
			adapter.submitList(uiState.categoriesList)
		}


	}

	private fun openLink(url: String) {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
		startActivity(intent)
	}

}