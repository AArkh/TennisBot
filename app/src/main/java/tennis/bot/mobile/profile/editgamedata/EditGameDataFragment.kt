package tennis.bot.mobile.profile.editgamedata

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditGameDataBinding
import javax.inject.Inject

@AndroidEntryPoint
class EditGameDataFragment : CoreFragment<FragmentEditGameDataBinding>() {
	override val bindingInflation: Inflation<FragmentEditGameDataBinding> = FragmentEditGameDataBinding::inflate
	@Inject
	lateinit var adapter: EditGameDataAdapter
	private val viewModel: EditGameDataViewModel by viewModels()

	companion object {
		const val GAMEDATA_DIALOG_REQUEST_KEY = "GAMEDATA_DIALOG_REQUEST_KEY"
		const val GAMEDATA_DIALOG_SELECT_ACTION_KEY = "GAMEDATA_DIALOG_SELECT_ACTION_KEY"
		const val GAMEDATA_DIALOG_TITLE = "title"
		const val GAMEDATA_DIALOG_PICKED_POSITION = "position"
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.categoriesContainer.adapter = adapter
		binding.categoriesContainer.layoutManager = LinearLayoutManager(requireContext())

		setFragmentResultListener(GAMEDATA_DIALOG_REQUEST_KEY) { _, result ->
			val title = result.getString(GAMEDATA_DIALOG_TITLE)
			val position = result.getInt(GAMEDATA_DIALOG_PICKED_POSITION)

			Log.d("123456", "Result received: $title, $position")

		}

		adapter.clickListener = { position ->
			startDialogWithKey(position)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: EditGameDataUiState ->
			adapter.submitList(uiState.gameDataCategoriesList)
		}

	}

	private fun startDialogWithKey(key: Int) {
		val bottomSheet = EditGameDataDialog()
		bottomSheet.arguments = bundleOf(GAMEDATA_DIALOG_SELECT_ACTION_KEY to key)
		bottomSheet.show(childFragmentManager, bottomSheet.tag)
	}
}