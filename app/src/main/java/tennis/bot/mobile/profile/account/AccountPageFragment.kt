package tennis.bot.mobile.profile.account

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAccountPageBinding
import tennis.bot.mobile.onboarding.login.LoginDialogFragment
import tennis.bot.mobile.profile.edit.EditProfileFragment
import tennis.bot.mobile.profile.matches.MatchesFragment
import javax.inject.Inject

@AndroidEntryPoint
class AccountPageFragment : CoreFragment<FragmentAccountPageBinding>() {
	@Inject
	lateinit var accountPageAdapter: AccountPageAdapter
	private val viewModel: AccountPageViewModel by viewModels()
	private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			Log.d("PhotoPicker", "Selected URI: $uri")
//			viewModel.onPickedProfilePic(uri)
		} else {
			Log.d("PhotoPicker", "No media selected")
		}
	}

	override val bindingInflation: Inflation<FragmentAccountPageBinding> = FragmentAccountPageBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = accountPageAdapter
		binding.container.layoutManager = LinearLayoutManager(requireContext())

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}
		binding.optionsButton.setOnClickListener {
			lifecycleScope.launch {
				delay(180L) // wait for keyboard to hide
				val bottomSheet = OptionsDialogFragment()
				bottomSheet.show(childFragmentManager, bottomSheet.tag)
			}
		}

		setFragmentResultListener(OPTIONS_DIALOG_REQUEST_KEY) { _, result ->
			lifecycleScope.launch {
				delay(180L) // to wait until bottom shit get closed w anim
				when (result.getString(SELECTED_DIALOG_OPTION)) {
					OptionsDialogFragment.UPDATE_CONTACTS -> {
						parentFragmentManager.beginTransaction()
							.replace(R.id.fragment_container_view, EditProfileFragment())
							.addToBackStack(EditProfileFragment::class.java.name)
							.commit()
					}
					OptionsDialogFragment.UPDATE_GAMEDATA -> {}
					OptionsDialogFragment.UPDATE_SETTINGS -> {}
					OptionsDialogFragment.LOGOUT -> {}
				}
			}
		}

		binding.tryAgainTv.setOnClickListener {
			viewModel.onTryAgainPressed()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: AccountPageUiState ->
			when(uiState){
				is AccountPageUiState.Loading -> {
					viewModel.onFetchingProfileData()
					binding.loadingBar.visibility = View.VISIBLE
					binding.errorLayout.visibility = View.GONE
					binding.container.visibility = View.GONE
				}
				is AccountPageUiState.ProfileDataReceived -> {
					binding.loadingBar.visibility = View.GONE
					binding.errorLayout.visibility = View.GONE
					binding.container.visibility = View.VISIBLE

					accountPageAdapter.submitList(uiState.receivedDataItems)
					accountPageAdapter.childAdapter.submitList(uiState.gameDataList)
					accountPageAdapter.clickListener = { command ->
						when(command) {
							PICK_IMAGE -> {
								pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
							}
							INFLATE_GAMEDATA -> {
								accountPageAdapter.childAdapter.submitList(uiState.gameDataList)
							}
							INFLATE_CONTACTS -> {
								accountPageAdapter.childAdapter.submitList(uiState.contactsList)
							}
							GO_TO_MATCHES -> {
								parentFragmentManager.beginTransaction()
									.replace(R.id.fragment_container_view, MatchesFragment())
									.addToBackStack(MatchesFragment::class.java.name)
									.commit()
							}
							GO_TO_TOURNAMENTS -> {
								val dialog = LoginDialogFragment()
								dialog.changeTitle("") // ask about the proper title
								dialog.show(childFragmentManager, dialog.tag)
							}
						}
					}
				}
				is AccountPageUiState.Error -> {
					binding.errorLayout.visibility = View.VISIBLE
					binding.container.visibility = View.GONE
					binding.loadingBar.visibility = View.GONE
				}
			}
		}
	}

	companion object {
		const val GO_TO_MATCHES = "GO_TO_MATCHES"
		const val INFLATE_CONTACTS = "INFLATE_CONTACTS"
		const val INFLATE_GAMEDATA = "INFLATE_GAMEDATA"
		const val GO_TO_TOURNAMENTS = "GO_TO_TOURNAMENTS"
		const val PICK_IMAGE = "PICK_IMAGE"
		const val OPTIONS_DIALOG_REQUEST_KEY = "OPTIONS_DIALOG_REQUEST_KEY"
		const val SELECTED_DIALOG_OPTION = "SELECTED_DIALOG_OPTION"
	}
}