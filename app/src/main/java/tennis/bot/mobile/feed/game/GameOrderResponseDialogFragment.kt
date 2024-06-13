package tennis.bot.mobile.feed.game

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentGameOrderResponseBinding

@AndroidEntryPoint
class GameOrderResponseDialogFragment : CoreBottomSheetDialogFragment<FragmentGameOrderResponseBinding>() {
	override val bindingInflation: Inflation<FragmentGameOrderResponseBinding> = FragmentGameOrderResponseBinding::inflate
	private var gameOrderId: Long? = null
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
			requireActivity().supportFragmentManager.setFragmentResult(
				GAME_ORDER_RESPONSE_KEY,
				bundleOf(
					GAME_ORDER_ID to gameOrderId,
					GAME_ORDER_COMMENT to binding.comment.text.toString()
				)
			)
			dialog?.dismiss()
		}
	}
}