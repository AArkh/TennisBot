package tennis.bot.mobile.profile.matches

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import javax.inject.Inject

class MatchesAdapter @Inject constructor(): CoreAdapter<MatchItemViewHolder>() {
	override fun onBindViewHolder(holder: MatchItemViewHolder, item: Any) {

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchItemViewHolder {
		val binding = MatchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return MatchItemViewHolder(binding)
	}

}

class MatchItemViewHolder(
	val binding: MatchItemBinding
) : RecyclerView.ViewHolder(binding.root)