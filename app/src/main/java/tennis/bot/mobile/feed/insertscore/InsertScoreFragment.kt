package tennis.bot.mobile.feed.insertscore

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentInsertScoreBinding
import tennis.bot.mobile.feed.activityfeed.FeedFragment
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsFragment
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.traverseToAnotherFragment
import tennis.bot.mobile.utils.view.AvatarImage
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
		private const val IMAGE_SIZE_DP = 66
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

		@Suppress("UNCHECKED_CAST")
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

		setFragmentResultListener(RESULT_DIALOG_REQUEST_KEY) { _, _ ->
			parentFragmentManager.clearBackStack(InsertScoreFragment::class.java.name) // do i need to clear them?
			parentFragmentManager.clearBackStack(SearchOpponentsFragment::class.java.name)
			parentFragmentManager.clearBackStack(AddScoreFragment::class.java.name)
			parentFragmentManager.traverseToAnotherFragment(FeedFragment())
		}

		subscribeToFlowOn(viewModel.uiStateFlow){uiState: InsertScoreUiState ->
			setsAdapter.submitList(uiState.setsList)
			mediaAdapter.submitList(uiState.mediaItemList)
			viewModel.isMatchValid()
			viewModel.isSuperTieBreakButtonActive()
			viewModel.isAddSetButtonActive()
			viewModel.appointActiveSetItem()

			if(uiState.player3 != null && uiState.player4 != null) {
				bindPlayerPhotosAndNames(true, uiState)
			} else {
				bindPlayerPhotosAndNames(false, uiState)
			}

			binding.buttonSend.isEnabled = uiState.isSendButtonActive
			binding.addSetButton.isEnabled = uiState.isAddSetButtonActive
			binding.addSuperTieBreakButton.isEnabled = uiState.isAddSuperTieBreakActive
			buttonVisibilityController(uiState.isLoading)
		}
	}

	private fun bindPlayerPhotosAndNames(isDouble: Boolean, uiState: InsertScoreUiState) {
		if (!isDouble){
			binding.playersLeftPhotos.setImages(listOf(AvatarImage(uiState.player1Image)), 0)
			binding.playersLeftPhotos.drawableSize = requireContext().dpToPx(IMAGE_SIZE_DP)
			binding.playersRightPhotos.setImages(listOf(AvatarImage(uiState.player2?.profilePicture)), 0)
			binding.playersRightPhotos.drawableSize = requireContext().dpToPx(IMAGE_SIZE_DP)
			binding.playersLeftNames.text = uiState.player1Name
			binding.playersRightNames.text = uiState.player2?.nameSurname?.substringBefore(" ")
		} else {
			binding.playersLeftPhotos.setImages(
				listOf(AvatarImage(uiState.player1Image), AvatarImage(uiState.player2?.profilePicture)), 0)
			binding.playersLeftPhotos.drawableSize = requireContext().dpToPx(IMAGE_SIZE_DP)
			binding.playersRightPhotos.setImages(
				listOf(AvatarImage(uiState.player3?.profilePicture), AvatarImage(uiState.player4?.profilePicture)), 0)
			binding.playersRightPhotos.drawableSize = requireContext().dpToPx(IMAGE_SIZE_DP)
			binding.playersLeftNames.text = getString(
				R.string.insert_score_doubles_names,
				uiState.player1Name,
				uiState.player2?.nameSurname?.substringBefore(" ")
			)
			binding.playersRightNames.text = getString(
				R.string.insert_score_doubles_names,
				uiState.player3?.nameSurname?.substringBefore(" "),
				uiState.player4?.nameSurname?.substringBefore(" ")
			)
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