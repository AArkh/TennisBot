package tennis.bot.mobile.onboarding.survey

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSurveyBinding
import javax.inject.Inject

@AndroidEntryPoint
class SurveyFragment : CoreFragment<FragmentSurveyBinding>() {
	override val bindingInflation: Inflation<FragmentSurveyBinding> = FragmentSurveyBinding::inflate

	@Inject
	lateinit var surveyAdapter: SurveyAdapter
	private val viewModel: SurveyViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.viewpager.adapter = surveyAdapter
		binding.viewpager.isUserInputEnabled = false

		val dialog = Dialog(requireContext())
		dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
		dialog.setContentView(R.layout.survey_welcome_dialog)
		dialog.window?.setBackgroundDrawableResource(R.drawable.survey_dialog_rounded)
		val dialogButton = dialog.findViewById<Button>(R.id.dialog_buttonStart)
		dialog.show()
		dialogButton.setOnClickListener { dialog.dismiss() }

		binding.backButton.setOnClickListener {
			if (viewModel.surveyUiState.value.selectedPage > 0) {
				viewModel.onBackClicked()
			} else {
				binding.backButton.setOnClickListener {
					parentFragmentManager.popBackStack()
				}
			}
		}

		surveyAdapter.clickListener = { selectedOptionId ->
			if (viewModel.surveyUiState.value.selectedPage in 0..7) {
				viewModel.onPickedOption(selectedOptionId)
			} else if (viewModel.surveyUiState.value.selectedPage == 8) {
				viewModel.onLastPickedOption(selectedOptionId)
				parentFragmentManager.beginTransaction()
					.replace(R.id.fragment_container_view, SurveyResultsFragment())
					.addToBackStack(SurveyResultsFragment::class.java.name)
					.commit()
			}

		}

		subscribeToFlowOn(viewModel.surveyUiState) { uiState: SurveyUiState ->
			binding.title.text = uiState.title
			binding.progressBar.setProgress(uiState.progress, true)
			binding.viewpager.setCurrentItem(uiState.selectedPage, true)
			surveyAdapter.submitList(uiState.surveyPages)
		}
	}
}