package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentGameOrderResponseBinding
import tennis.bot.mobile.feed.game.GameFragment.Companion.GAME_ORDER_COMMENT
import tennis.bot.mobile.feed.game.GameFragment.Companion.GAME_ORDER_ID
import tennis.bot.mobile.feed.game.GameFragment.Companion.GAME_ORDER_RESPONSE_KEY
import tennis.bot.mobile.feed.game.GameFragment.Companion.TARGET_PLAYER_ID

@AndroidEntryPoint
class GameOrderResponseDialogFragment : CoreBottomSheetDialogFragment<FragmentGameOrderResponseBinding>() {
	override val bindingInflation: Inflation<FragmentGameOrderResponseBinding> = FragmentGameOrderResponseBinding::inflate
	private var gameOrderId: Long = 0
	private var targetPlayerId: Long? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			gameOrderId = it.getLong(GAME_ORDER_ID)
			targetPlayerId = it.getLong(TARGET_PLAYER_ID)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonSend.setOnClickListener {
			sendTheResponseResult(requireActivity(), gameOrderId, targetPlayerId, binding.comment.text.toString())
			dialog?.dismiss()
		}
	}
}

private fun sendTheResponseResult(activity: FragmentActivity, gameOrderId: Long, targetPlayerId: Long? = null, comment: String?) {
	activity.supportFragmentManager.setFragmentResult(
		GAME_ORDER_RESPONSE_KEY,
		if (targetPlayerId == null) {
			bundleOf(
				GAME_ORDER_ID to gameOrderId,
				GAME_ORDER_COMMENT to comment
			)
		} else {
			bundleOf(
				TARGET_PLAYER_ID to targetPlayerId,
				GAME_ORDER_COMMENT to comment
			)
		}
	)
}