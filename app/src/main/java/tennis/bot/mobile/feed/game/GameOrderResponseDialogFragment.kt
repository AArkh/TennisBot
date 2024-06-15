package tennis.bot.mobile.feed.game

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentGameOrderResponseBinding

@AndroidEntryPoint
class GameOrderResponseDialogFragment : CoreBottomSheetDialogFragment<FragmentGameOrderResponseBinding>() {
	override val bindingInflation: Inflation<FragmentGameOrderResponseBinding> = FragmentGameOrderResponseBinding::inflate
	private var gameOrderId: Long = 0
	companion object {
		const val GAME_ORDER_RESPONSE_KEY = "GAME_ORDER_RESPONSE_KEY"
		const val GAME_ORDER_ID = "GAME_ORDER_ID"
		const val GAME_ORDER_COMMENT = "GAME_ORDER_COMMENT"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			gameOrderId = it.getLong(GAME_ORDER_ID)
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonSend.setOnClickListener {
			Log.d("GameDialog", "Button clicked, sending result") // doesn't connect with the fragment. why, idk
			sendTheResponseResult(requireActivity(), gameOrderId, binding.comment.text.toString())
			dialog?.dismiss()
		}
	}
}

private fun sendTheResponseResult(activity: FragmentActivity, gameOrderId: Long, comment: String?) {
	activity.supportFragmentManager.setFragmentResult(
		GameOrderResponseDialogFragment.GAME_ORDER_RESPONSE_KEY,
		bundleOf(
			GameOrderResponseDialogFragment.GAME_ORDER_ID to gameOrderId,
			GameOrderResponseDialogFragment.GAME_ORDER_COMMENT to comment
		)
	)
}