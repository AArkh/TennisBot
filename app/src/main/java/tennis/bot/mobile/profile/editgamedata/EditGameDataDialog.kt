package tennis.bot.mobile.profile.editgamedata

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentEditGamedataDialogBinding
import javax.inject.Inject

@AndroidEntryPoint
class EditGameDataDialog: CoreBottomSheetDialogFragment<FragmentEditGamedataDialogBinding>() {
	override val bindingInflation: Inflation<FragmentEditGamedataDialogBinding> = FragmentEditGamedataDialogBinding::inflate
	@Inject
	lateinit var adapter: EditGameDataDialogAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.closeButton.setOnClickListener {
			dialog?.dismiss()
		}

		binding.optionsContainer.adapter = adapter
		binding.optionsContainer.layoutManager = LinearLayoutManager(requireContext())

		adapter.clickListener = { position ->
			activity?.supportFragmentManager?.setFragmentResult(
				EditGameDataFragment.GAMEDATA_DIALOG_REQUEST_KEY,
				Bundle().apply {
					putString("title", binding.title.text.toString())
					putInt("position", position)
				}
			)
			dialog?.dismiss()
		}

		adapter.submitList(listOf(TextOnlyItem("option1"),
			TextOnlyItem("option2"),
			TextOnlyItem("option3"),
			TextOnlyItem("option4"))
		)
	}

	fun submitDataToGameDataDialog(data: List<TextOnlyItem>) {
		adapter.submitList(data)
	}
}