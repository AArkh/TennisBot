package tennis.bot.mobile.feed.insertscore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.core.view.setPadding
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentInsertScoreBinding
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.utils.dpToPx
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class InsertScoreFragment : CoreFragment<FragmentInsertScoreBinding>() {
	override val bindingInflation: Inflation<FragmentInsertScoreBinding> = FragmentInsertScoreBinding::inflate
	private val viewModel: InsertScoreViewModel by viewModels()
	@Inject
	lateinit var adapter: InsertScoreAdapter

	companion object {
		const val SELECTED_SET_NUMBER = "SELECTED_SET_NUMBER"
		const val SELECTED_SET_CURRENT_VALUE = "SELECTED_SET_CURRENT_VALUE"

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.setsContainer.adapter = adapter
		binding.setsContainer.layoutManager = LinearLayoutManager(requireContext())
		adapter.clickListener = { position ->
			when(position) {
				in -5..-1 -> {
					viewModel.onDeletingSetItem(abs(position))
				}
				else -> {
					val bottomDialog = InsertScoreDialogFragment()
					bottomDialog.arguments = bundleOf(
						SELECTED_SET_NUMBER to position + 1
					)
					bottomDialog.show(childFragmentManager, bottomDialog.tag)
				}
			}
		}

		binding.addSetButton.setOnClickListener {
			viewModel.onAddingSetItem(requireContext())
		}

		setFragmentResultListener(SearchOpponentsViewModel.OPPONENT_PICKED_REQUEST_KEY) { _, result ->
			viewModel.onInitial(
				result.getLong(SearchOpponentsViewModel.SELECTED_OPPONENT_ID_KEY),
				result.getString(SearchOpponentsViewModel.SELECTED_OPPONENT_PHOTO_KEY),
				result.getString(SearchOpponentsViewModel.SELECTED_OPPONENT_NAME_KEY) ?: ""
			)

		}

		setFragmentResultListener(InsertScoreDialogViewModel.REQUEST_SCORE_KEY) { _, result ->
			val score = result.getString(InsertScoreDialogViewModel.SELECTED_SCORE_KEY)
			val setNumber = result.getInt(InsertScoreDialogViewModel.SELECTED_SET_KEY)
			Log.d("123456", "received result $score and $setNumber")
			viewModel.onScoreReceived(setNumber, score ?: "") {

			}

		}

		subscribeToFlowOn(viewModel.uiStateFlow){uiState: InsertScoreUiState ->
			adapter.submitList(uiState.setsList)
			binding.player1Image.loadPlayerImage(uiState.player1Image, binding.player1Photo)
			binding.player2Image.loadPlayerImage(uiState.player2Image, binding.player2Photo)
			binding.player1Name.text = uiState.player1Name
			binding.player2Name.text = uiState.player2Name
			binding.buttonSend.isEnabled = uiState.isSendButtonActive

		}
	}

	private fun ImageView.loadPlayerImage(playerImage: String?, frame: FrameLayout) {
		if (playerImage == null) return

		load(R.drawable.user) { crossfade(true) }
		frame.setPadding(frame.context.dpToPx(20))

		if (playerImage.contains("default")) {
			val resourceId = getDefaultDrawableResourceId(context, playerImage.removeSuffix(".png"))
			if (resourceId != null) load(resourceId)
			frame.setPadding(0)
		} else {
			load(AccountPageAdapter.IMAGES_LINK + playerImage) { crossfade(true) }
			frame.setPadding(0)
		}
	}

}