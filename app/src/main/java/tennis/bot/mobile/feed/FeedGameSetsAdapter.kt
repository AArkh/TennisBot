package tennis.bot.mobile.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.RecyclerSetResultItemAltBinding
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import javax.inject.Inject

class FeedGameSetsAdapter @Inject constructor(): CoreAdapter<FeedGameSetItemViewHolder>(){
	override fun onBindViewHolder(holder: FeedGameSetItemViewHolder, item: Any) {
		val set = item as? TennisSetNetwork ?: throw IllegalArgumentException("Item must be TennisSetNetwork")

		holder.binding.player1Score.text = set.score1.toString()
		holder.binding.player2Score.text = set.score2.toString()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedGameSetItemViewHolder {
		val binding = RecyclerSetResultItemAltBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return FeedGameSetItemViewHolder(binding)
	}
}

class FeedGameSetItemViewHolder (
	val binding: RecyclerSetResultItemAltBinding
) : RecyclerView.ViewHolder(binding.root)