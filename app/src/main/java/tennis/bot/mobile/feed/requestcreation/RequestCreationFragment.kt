package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentRequestBinding
import javax.inject.Inject

@AndroidEntryPoint
class RequestCreationFragment : AuthorizedCoreFragment<FragmentRequestBinding>() {

	override val bindingInflation: Inflation<FragmentRequestBinding> = FragmentRequestBinding::inflate
	private val viewModel: RequestCreationViewModel by viewModels()
	@Inject
	lateinit var adapter: RequestAdapter

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.container.adapter = adapter
		binding.container.layoutManager = LinearLayoutManager(requireContext())
		viewModel.onStartup()

		adapter.onRateChange = { rating ->
			// todo мы так делаем, чтобы лишний раз не дергать обновление всего списка и не терять touch event
			// при adapter.submitList
			binding.root.findViewById<TextView>(R.id.comment_text).text = "some text + $rating"
			viewModel.updateRating(rating) // не должно быть сайдэффекта с обновлением viewModel.uiStateFlow
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			adapter.submitList(uiState.layoutItemsList)
		}
	}
}