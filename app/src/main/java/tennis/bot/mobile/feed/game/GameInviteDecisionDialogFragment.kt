package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.game.GameFragment.Companion.GAME_INVITE_IS_ACCEPTED
import tennis.bot.mobile.feed.game.GameFragment.Companion.GAME_INVITE_RESPONSE_KEY
import tennis.bot.mobile.utils.basicdialog.BasicDialogFragment

@AndroidEntryPoint
class GameInviteDecisionDialogFragment: BasicDialogFragment() {
	private var gameOrderId: Long = 0
	private var targetPlayerId: Long = 0

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		arguments?.let {
			gameOrderId = it.getLong(GameFragment.GAME_ORDER_ID)
			targetPlayerId = it.getLong(GameFragment.TARGET_PLAYER_ID)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.dialogTitle.text = "Приглашение"
		binding.dialogText.text = "Вы принимаете вызов?"

		binding.buttonGreen.text = getString(R.string.survey_options_item_set4_yes)
		binding.buttonGreen.setOnClickListener {
			sendTheInviteResponse(requireActivity(), true)
			dialog?.dismiss()
		}
		binding.buttonGrey.text = getString(R.string.survey_options_item_set4_no)
		binding.buttonGrey.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.tb_red)
		binding.buttonGrey.setOnClickListener {
			sendTheInviteResponse(requireActivity(), false)
			dialog?.dismiss()
		}
	}

	private fun sendTheInviteResponse(activity: FragmentActivity, response: Boolean) {
		activity.supportFragmentManager.setFragmentResult(
			GAME_INVITE_RESPONSE_KEY,
			bundleOf(
				GAME_INVITE_IS_ACCEPTED to response,
				GameFragment.GAME_ORDER_ID to gameOrderId,
				GameFragment.TARGET_PLAYER_ID to targetPlayerId
			)
		)
	}
}