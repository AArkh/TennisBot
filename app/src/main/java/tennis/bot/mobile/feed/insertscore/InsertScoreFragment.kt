package tennis.bot.mobile.feed.insertscore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import tennis.bot.mobile.feed.FeedBottomNavigationFragment
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.profile.account.AccountPageAdapter
import tennis.bot.mobile.profile.account.getDefaultDrawableResourceId
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.traverseToAnotherFragment
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class InsertScoreFragment : CoreFragment<FragmentInsertScoreBinding>() {
	override val bindingInflation: Inflation<FragmentInsertScoreBinding> = FragmentInsertScoreBinding::inflate
	private val viewModel: InsertScoreViewModel by viewModels()
	@Inject
	lateinit var setsAdapter: InsertScoreAdapter
	@Inject
	lateinit var mediaAdapter: InsertScoreMediaAdapter
	private val pickPhoto = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			Log.d("PhotoPicker", "Selected URI: $uri")
			viewModel.onPickedPhoto(pickedImageUri = uri)
		} else {
			Log.d("PhotoPicker", "No media selected")
		}
	}
	private val pickVideo = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
		if (uri != null) {
			Log.d("PhotoPicker", "Selected URI: $uri")
			viewModel.onPickedVideo(pickedVideoUri = uri)
		} else {
			Log.d("PhotoPicker", "No media selected")
		}
	}


	companion object {
		const val SELECTED_SET_NUMBER = "SELECTED_SET_NUMBER"
		const val SELECTED_SET_CURRENT_VALUE = "SELECTED_SET_CURRENT_VALUE"
		const val SELECTED_SET_IS_SUPER_TIE = "SELECTED_SET_IS_SUPER_TIE"
		const val ADD_PHOTO = "ADD_PHOTO"
		const val ADD_VIDEO = "ADD_VIDEO"
		const val DELETE_PHOTO = "DELETE_PHOTO"
		const val DELETE_VIDEO = "DELETE_VIDEO"
		const val RESULT_DIALOG_REQUEST_KEY = "RESULT_DIALOG_REQUEST_KEY"
		const val RESULT_DIALOG_SELECTED_OPTION_KEY = "RESULT_DIALOG_SELECTED_OPTION_KEY"

	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.setsContainer.adapter = setsAdapter
		binding.setsContainer.layoutManager = LinearLayoutManager(requireContext())
		setsAdapter.clickListener = { position, value, isSuperTieBreak ->
			when(position) {
				in -5..-1 -> {
					viewModel.onDeletingSetItem(abs(position))
				}
				else -> {
					val bottomDialog = InsertScoreDialogFragment()
					bottomDialog.arguments = bundleOf(
						SELECTED_SET_NUMBER to position + 1,
						SELECTED_SET_CURRENT_VALUE to value,
						SELECTED_SET_IS_SUPER_TIE to isSuperTieBreak
					)
					bottomDialog.show(childFragmentManager, bottomDialog.tag)
				}
			}
		}

		binding.mediaContainer.adapter = mediaAdapter
		binding.mediaContainer.itemAnimator = null
		binding.mediaContainer.layoutManager = LinearLayoutManager(requireContext())
		mediaAdapter.clickListener = { command ->
			when(command) {
				ADD_PHOTO -> {
					pickPhoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
				}
				ADD_VIDEO -> {
					pickVideo.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly))
				}
				DELETE_PHOTO -> {
					viewModel.onDeletePickedPhoto()
				}
				DELETE_VIDEO -> {
					viewModel.onDeletePickedVideo()
				}
			}
		}

		binding.addSetButton.setOnClickListener {
			viewModel.onAddingSetItem()
		}

		binding.addSuperTieBreakButton.setOnClickListener {
			viewModel.onAddingSuperTieBreakItem()
		}

		binding.buttonSend.setOnClickListener {
			viewModel.onSendButtonClicked(requireContext()) {
				val dialog = InsertScoreResultDialog()
				dialog.show(childFragmentManager, dialog.tag)
			}
		}

		setFragmentResultListener(SearchOpponentsViewModel.OPPONENT_PICKED_REQUEST_KEY) { _, result ->
			(result.getParcelableArray(SearchOpponentsViewModel.SELECTED_OPPONENT_KEY) as? Array<OpponentItem>)?.let { // not deprecated version blows up + i used safe cast
				viewModel.onInitial(it)
			}

		}

		setFragmentResultListener(InsertScoreDialogViewModel.REQUEST_SCORE_KEY) { _, result ->
			val score = result.getString(InsertScoreDialogViewModel.SELECTED_SCORE_KEY)
			val setNumber = result.getInt(InsertScoreDialogViewModel.SELECTED_SET_KEY)
			Log.d("123456", "received result $score and $setNumber")
			viewModel.onScoreReceived(setNumber, score ?: "")
		}

		setFragmentResultListener(RESULT_DIALOG_REQUEST_KEY) { _, result ->
			parentFragmentManager.clearBackStack(InsertScoreFragment::class.java.name) // do i need to clear them?
			parentFragmentManager.clearBackStack(SearchOpponentsFragment::class.java.name)
			parentFragmentManager.clearBackStack(AddScoreFragment::class.java.name)
			parentFragmentManager.traverseToAnotherFragment(FeedBottomNavigationFragment())
		}

		subscribeToFlowOn(viewModel.uiStateFlow){uiState: InsertScoreUiState ->
			setsAdapter.submitList(uiState.setsList)
			mediaAdapter.submitList(uiState.mediaItemList)
			viewModel.isMatchValid()
			viewModel.isSuperTieBreakButtonActive()
			viewModel.isAddSetButtonActive()
			viewModel.appointActiveSetItem()

			binding.player1Image.loadPlayerImage(uiState.player1Image, binding.player1Photo)
			binding.player2Image.loadPlayerImage(uiState.player2Image, binding.player2Photo)
			binding.player1Name.text = uiState.player1Name
			binding.player2Name.text = uiState.player2Name

			binding.buttonSend.isEnabled = uiState.isSendButtonActive
			binding.addSetButton.isEnabled = uiState.isAddSetButtonActive
			binding.addSuperTieBreakButton.isEnabled = uiState.isAddSuperTieBreakActive
			buttonVisibilityController(uiState.isLoading)
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

	private fun buttonVisibilityController(isLoading: Boolean) {
		val buttonSendBackground = if (binding.buttonSend.isEnabled) {
			R.drawable.btn_bkg_enabled
		} else {
			R.drawable.btn_bkg_disabled
		}
		binding.buttonSend.setBackgroundResource(buttonSendBackground)
		if (isLoading) {
			binding.buttonSend.text = ""
			binding.buttonLoadingAnim.visibility = View.VISIBLE
		} else {
			binding.buttonSend.text = context?.getString(R.string.button_send)
			binding.buttonLoadingAnim.visibility = View.GONE
		}

		if (!binding.addSetButton.isEnabled) {
			binding.addSetButton.alpha = 0.3F
		} else {
			binding.addSetButton.alpha = 1F
		}

		if(!binding.addSuperTieBreakButton.isEnabled) {
			binding.addSuperTieBreakButton.alpha = 0.3F
		} else {
			binding.addSuperTieBreakButton.alpha = 1F
		}
	}
}