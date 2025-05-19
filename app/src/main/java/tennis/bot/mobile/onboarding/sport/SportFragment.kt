package tennis.bot.mobile.onboarding.sport

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSportBinding
import tennis.bot.mobile.onboarding.namegender.NameGenderFragment
import javax.inject.Inject

@AndroidEntryPoint
class SportFragment : CoreFragment<FragmentSportBinding>() {
	override val bindingInflation: Inflation<FragmentSportBinding> = FragmentSportBinding::inflate
	@Inject
	lateinit var sportAdapter: SportAdapter
	private val viewModel: SportViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.inDevelopmentRv.adapter = sportAdapter
		binding.inDevelopmentRv.layoutManager = LinearLayoutManager(requireContext())
		sportAdapter.submitList(viewModel.sportItemsList)

		binding.backButton.setOnClickListener {
			parentFragmentManager.popBackStack()
		}

		binding.buttonNext.setOnClickListener {
			parentFragmentManager.beginTransaction()
				.replace(R.id.fragment_container_view, NameGenderFragment())
				.addToBackStack(NameGenderFragment::class.java.name)
				.commit()
		}



	}

}