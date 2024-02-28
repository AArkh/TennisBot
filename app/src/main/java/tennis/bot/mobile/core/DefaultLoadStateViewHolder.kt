package tennis.bot.mobile.core

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.RecyclerLoadStateItemBinding

class DefaultLoadStateViewHolder(
	private val binding: RecyclerLoadStateItemBinding,
	retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

	init {
		binding.retryButton.setOnClickListener { retry.invoke() }
	}

	fun bind(loadState: LoadState) {
		if (loadState is LoadState.Error) {
			Log.d("LoadState", "Error message: ${loadState.error.localizedMessage}")
		}
		binding.progressBar.isVisible = loadState is LoadState.Loading
		binding.retryButton.isVisible = loadState !is LoadState.Loading
		binding.errorMsg.isVisible = loadState !is LoadState.Loading
	}

	companion object {
		fun create(parent: ViewGroup, retry: () -> Unit): DefaultLoadStateViewHolder {
			val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_load_state_item, parent, false)
			val binding = RecyclerLoadStateItemBinding.bind(view)
			return DefaultLoadStateViewHolder(binding, retry)
		}
	}
}