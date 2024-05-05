package tennis.bot.mobile.feed.requestcreation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
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

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState ->
			adapter.submitList(uiState.layoutItemsList)
		}
	}


}