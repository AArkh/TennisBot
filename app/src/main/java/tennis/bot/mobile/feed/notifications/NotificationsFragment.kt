package tennis.bot.mobile.feed.notifications

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment :  CoreFragment<FragmentRequestBinding>() {
	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate
	private val viewModel: NotificationsViewModel by viewModels()
	@Inject
	lateinit var adapter: NotificationsAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.buttonCreate.isVisible = false
		binding.title.text = getString(R.string.notification_title)

		binding.container.adapter = adapter
		binding.container.layoutManager = LinearLayoutManager(context)


		adapter.submitList(viewModel.list)

	}
}