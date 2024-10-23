package tennis.bot.mobile.feed.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentActionableNotificationBinding
import tennis.bot.mobile.feed.activityfeed.MatchRequestPostItem
import tennis.bot.mobile.feed.activityfeed.showPlayerPhoto
import tennis.bot.mobile.feed.game.GameAdapter
import tennis.bot.mobile.feed.game.bindInfoPanel
import tennis.bot.mobile.utils.showToast

@AndroidEntryPoint
class ActionableNotificationFragment: AuthorizedCoreFragment<FragmentActionableNotificationBinding>() {
	override val bindingInflation: Inflation<FragmentActionableNotificationBinding> = FragmentActionableNotificationBinding::inflate
	private val viewModel: ActionableNotificationViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		viewModel.getAppropriateItem {
			parentFragmentManager.popBackStack()
			requireContext().showToast(getString(R.string.match_no_longer_available))
		}

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		subscribeToFlowOn(viewModel.immutableUiStateFlow) { uiState ->
			binding.title.text = uiState.title
			uiState.itemToDisplay?.let { bindGameRequestItem(it) }
		}
	}

	private fun clickListener (command: String, id: Int, targetPlayerId: Long) {
		when(command) {
			GameAdapter.REQUEST_RESPONSE_ACCEPT -> {
				viewModel.onAcceptingInvite(
					id = id,
					targetPlayerId = targetPlayerId
				) { parentFragmentManager.popBackStack() } // return to NotificationsFragment
			}
			GameAdapter.REQUEST_RESPONSE_DECLINE -> {
				viewModel.onDecliningInvite(
					id = id,
					targetPlayerId = targetPlayerId
				) { parentFragmentManager.popBackStack() }
			}
		}
	}

	private fun bindGameRequestItem(item: Any) { // think about putting it back in NotificationsFragment and passing the data here only if we do have the data
		val matchRequestItem = item as? MatchRequestPostItem ?: error("Item must be MatchRequestPostItem")
		val context = requireContext()

		with(binding.gameItemLayout) {
			root.isVisible = true

			playerPhoto.showPlayerPhoto(matchRequestItem.playerPhoto, itemPicture)
			nameSurname.text = matchRequestItem.playerName
			locationSubTitle.text = matchRequestItem.locationSubTitle

			postType.text = when {
				matchRequestItem.targetPlayerId != null -> context.getString(if (matchRequestItem.isOwned == true) R.string.my_invite else R.string.invite)
				matchRequestItem.isResponsed == true -> context.getString(R.string.my_response)
				matchRequestItem.liked -> context.getString(R.string.my_request)
				matchRequestItem.isOwned == true -> context.getString(R.string.request_response)
				else -> context.getString(R.string.post_type_2).substringBefore(" ")
			}

			postType.setTextColor(context.getColor(
				when (postType.text) {
					context.getString(R.string.request_response),
					context.getString(R.string.invite),
					context.getString(R.string.my_response) -> R.color.tb_primary_green
					else -> if (matchRequestItem.isOwned == true) R.color.tb_red_new else R.color.tb_gray_dark
				}
			))

			acceptButton.isVisible = postType.text in listOf(context.getString(R.string.request_response), context.getString(R.string.invite))
			acceptButton.setOnClickListener {
				clickListener(GameAdapter.REQUEST_RESPONSE_ACCEPT, matchRequestItem.id, matchRequestItem.playerId)
			}

			declineButton.isVisible = acceptButton.isVisible
			declineButton.setOnClickListener {
				clickListener(GameAdapter.REQUEST_RESPONSE_DECLINE, matchRequestItem.id, matchRequestItem.playerId)
			}

			optionsDots.isVisible = false

			bindInfoPanel(matchRequestItem.matchDate, matchRequestItem)
			requestComment.text = matchRequestItem.comment.takeUnless { postType.text == context.getString(R.string.request_response) }
				?: matchRequestItem.responseComment
			requestComment.isVisible = requestComment.text.isNotEmpty()
			date.isVisible = !acceptButton.isVisible
			date.text = matchRequestItem.addedAt
		}
	}

	companion object {
		const val IS_INCOMING_ARGUMENT = "IS_INCOMING_ARGUMENT"
		const val ID_ARGUMENT = "ID_ARGUMENT"
		fun newInstance(isIncoming: Boolean, id: Int): ActionableNotificationFragment {
			val fragment = ActionableNotificationFragment()
			val args = Bundle()
			args.putBoolean(IS_INCOMING_ARGUMENT, isIncoming)
			args.putInt(ID_ARGUMENT, id)
			fragment.arguments = args
			return fragment
		}
	}
}