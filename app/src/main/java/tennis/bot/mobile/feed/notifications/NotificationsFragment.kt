package tennis.bot.mobile.feed.notifications

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.DefaultLoadStateAdapter
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment :  AuthorizedCoreFragment<FragmentRequestBinding>() {
	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate
	private val viewModel: NotificationsViewModel by viewModels()
	@Inject
	lateinit var adapter: NotificationsAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonCreate.isVisible = false
		binding.title.text = getString(R.string.notification_title)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.container.adapter = adapter.withLoadStateHeaderAndFooter(
			header = DefaultLoadStateAdapter { adapter.retry() },
			footer = DefaultLoadStateAdapter { adapter.retry() }
		)
		binding.container.layoutManager = LinearLayoutManager(context)

		lifecycleScope.launch(Dispatchers.IO) {
			viewModel.getNotificationsPaginationFlow().collectLatest {
				adapter.submitData(it)
			}
		}

//		binding.swipeRefreshLayout.setOnRefreshListener { //todo implement swipe to refresh
//			adapter.refresh()
//			binding.swipeRefreshLayout.isRefreshing = false
//		}

		adapter.clickListenerTransfer = { id ->

		}
		adapter.clickListenerTelegram = { isTelegram, payload ->
			if (isTelegram) {
				openIntent(Intent.ACTION_VIEW, "https://t.me/$payload")
			} else {
				openIntent(Intent.ACTION_DIAL, "tel:$payload")
			}
		}

		adapter.addLoadStateListener { loadState ->
			binding.errorLayout.errorLayout.isVisible = loadState.source.refresh is LoadState.Error
			binding.loadingBar.isVisible = loadState.source.refresh is LoadState.Loading
		}

	}

	private fun openIntent(intentAction: String, url: String) {
		val intent = Intent(intentAction, Uri.parse(url))
		requireContext().startActivity(intent)
	}
}

