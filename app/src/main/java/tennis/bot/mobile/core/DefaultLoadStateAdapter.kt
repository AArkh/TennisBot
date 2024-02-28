package tennis.bot.mobile.core

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class DefaultLoadStateAdapter(
	private val retry: () -> Unit
): LoadStateAdapter<DefaultLoadStateViewHolder>() {
	override fun onBindViewHolder(holder: DefaultLoadStateViewHolder, loadState: LoadState) {
		holder.bind(loadState)
	}

	override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): DefaultLoadStateViewHolder {
		return DefaultLoadStateViewHolder.create(parent, retry)
	}
}