package tennis.bot.mobile.onboarding.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentAccountPageBinding
import javax.inject.Inject

@AndroidEntryPoint
class AccountPageFragment : CoreFragment<FragmentAccountPageBinding>() {
	@Inject
	lateinit var accountPageAdapter: AccountPageAdapter
	private val viewModel: AccountPageViewModel by viewModels()

	override val bindingInflation: Inflation<FragmentAccountPageBinding> = FragmentAccountPageBinding::inflate

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.container.adapter = accountPageAdapter
		binding.container.layoutManager = LinearLayoutManager(requireContext())

		binding.tryAgainTv.setOnClickListener {
			viewModel.onFetchingProfileData()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: AccountPageUiState ->
			when(uiState){
				is AccountPageUiState.Loading -> {
					viewModel.onFetchingProfileData()
					binding.loadingBar.visibility = View.VISIBLE
				}
				is AccountPageUiState.ProfileDataReceived -> {
					binding.loadingBar.visibility = View.GONE

					accountPageAdapter.submitList(uiState.receivedDataItems)
					accountPageAdapter.childAdapter.submitList(uiState.gameDataList)
					accountPageAdapter.clickListener = { command ->
						when(command) {
							INFLATE_GAMEDATA -> {
								accountPageAdapter.childAdapter.submitList(uiState.gameDataList)
							}
							INFLATE_CONTACTS -> {
								accountPageAdapter.childAdapter.submitList(uiState.contactsList)
							}
						}
					}
				}
				is AccountPageUiState.Error -> {
					binding.errorLayout.visibility = View.VISIBLE
				}
			}

		}
	}

	companion object {
		const val INFLATE_CONTACTS = "INFLATE_CONTACTS"
		const val INFLATE_GAMEDATA = "INFLATE_GAMEDATA"
	}
}