package tennis.bot.mobile.feed.insertscore

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentInsertScoreBottomDialogBinding

@AndroidEntryPoint
class InsertScoreDialogFragment : CoreBottomSheetDialogFragment<FragmentInsertScoreBottomDialogBinding>() {
	override val bindingInflation: Inflation<FragmentInsertScoreBottomDialogBinding> = FragmentInsertScoreBottomDialogBinding::inflate
	private val viewModel: InsertScoreDialogViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.closeButton.setOnClickListener {
			dialog?.dismiss()
		}

		binding.cancelDialog.setOnClickListener {
			dialog?.dismiss()
		}

		binding.basicScore.setupWithCustomValues(viewModel.basicScoreVariants)
		binding.leftTieBreak.setupWithCustomValues(viewModel.leftTieBreakScoreVariants)
		binding.rightTieBreak.setupWithCustomValues(viewModel.rightTieBreakScoreVariants)

		binding.basicScore.setOnValueChangedListener { _, _, newVal ->
			when(newVal){
				7 -> {
					binding.rightTieBreak.visibility = View.VISIBLE
					binding.leftTieBreak.visibility = View.GONE
				}
				8 -> {
					binding.leftTieBreak.visibility = View.VISIBLE
					binding.rightTieBreak.visibility = View.GONE
				}
				else -> {
					binding.rightTieBreak.visibility = View.GONE
					binding.leftTieBreak.visibility = View.GONE
				}
			}
		}

		binding.buttonChoose.setOnClickListener {
			viewModel.onScorePicked(binding.basicScore.value, requireActivity())
			dialog?.dismiss()
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: InsertScoreDialogUiState ->
			binding.title.text = requireContext().getString(R.string.insert_score_set_item_title, uiState.setNumber)
		}
	}
}