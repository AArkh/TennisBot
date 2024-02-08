package tennis.bot.mobile.profile.editgamedata

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditGameDataBinding
import tennis.bot.mobile.profile.account.OptionsDialogFragment
import tennis.bot.mobile.profile.editprofile.EditProfileAdapter
import tennis.bot.mobile.profile.editprofile.EditProfileViewModel
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@AndroidEntryPoint
class EditGameDataFragment : CoreFragment<FragmentEditGameDataBinding>() {
	override val bindingInflation: Inflation<FragmentEditGameDataBinding> = FragmentEditGameDataBinding::inflate
	@Inject
	lateinit var adapter: EditGameDataAdapter
	private val viewModel: EditGameDataViewModel by viewModels()

	companion object {
		const val GAMEDATA_DIALOG_REQUEST_KEY = "GAMEDATA_DIALOG_REQUEST_KEY"
		const val GAMEDATA_DIALOG_SELECTED_OPTION = "GAMEDATA_DIALOG_SELECTED_OPTION"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		setFragmentResultListener(GAMEDATA_DIALOG_REQUEST_KEY) { _, result ->
			val title = result.getString("title")
			val position = result.getInt("position")

			Log.d("123456", "Result received: $title, $position")

		}

		adapter.clickListener = { position ->
			when(position) {
				EditGameDataAdapter.IS_RIGHT_HAND -> {
					val bottomSheet = EditGameDataDialog()
					bottomSheet.show(childFragmentManager, bottomSheet.tag)
				}
				EditGameDataAdapter.IS_ONE_BACKHAND -> {}
				EditGameDataAdapter.SURFACE -> {}
				EditGameDataAdapter.SHOES -> {}
				EditGameDataAdapter.RACQUET -> {}
				EditGameDataAdapter.RACQUET_STRINGS -> {}
			}

		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: EditGameDataUiState ->
			adapter.submitList(uiState.gameDataCategoriesList)
		}

	}
}