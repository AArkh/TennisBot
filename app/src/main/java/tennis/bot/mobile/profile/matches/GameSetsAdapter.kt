package tennis.bot.mobile.profile.matches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.RecyclerSetResultItemBinding
import javax.inject.Inject

class GameSetsAdapter @Inject constructor(): CoreAdapter<GameSetItemViewHolder>() {
	override fun onBindViewHolder(holder: GameSetItemViewHolder, item: Any) {
		val set = item as? TennisSetNetwork ?: throw IllegalArgumentException("Item must be TennisSetNetwork")

		holder.binding.player1Score.text = set.score1.toString()
		holder.binding.player2Score.text = set.score2.toString()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameSetItemViewHolder {
		val binding = RecyclerSetResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return GameSetItemViewHolder(binding)
	}
}

class GameSetItemViewHolder (
	val binding: RecyclerSetResultItemBinding
) : RecyclerView.ViewHolder(binding.root)

