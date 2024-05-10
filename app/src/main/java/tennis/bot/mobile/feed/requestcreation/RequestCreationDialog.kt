package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditGamedataDialogBinding
import tennis.bot.mobile.profile.editgamedata.EditGameDataDialogAdapter
import tennis.bot.mobile.profile.editgamedata.EditGameDataDialogUiState
import javax.inject.Inject

@AndroidEntryPoint
class RequestCreationDialog: CoreBottomSheetDialogFragment<FragmentEditGamedataDialogBinding>() {
	override val bindingInflation: Inflation<FragmentEditGamedataDialogBinding> = FragmentEditGamedataDialogBinding::inflate
	@Inject
	lateinit var adapter: EditGameDataDialogAdapter
	private val viewModel: RequestCreationDialogViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.closeButton.setOnClickListener {
			dialog?.dismiss()
		}

		binding.optionsContainer.adapter = adapter
		binding.optionsContainer.layoutManager = LinearLayoutManager(requireContext())

		adapter.clickListener = { option ->
			viewModel.onOptionPicked(requireActivity(), binding.title.text.toString(), option)
			dialog?.dismiss()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: EditGameDataDialogUiState ->
			binding.title.text = uiState.title
			adapter.submitList(uiState.optionsList)
		}
	}
}