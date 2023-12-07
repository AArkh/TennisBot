package tennis.bot.mobile.onboarding.survey

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
		dialog.window?.setBackgroundDrawableResource(R.drawable.survey_dialog_rounded)
		dialog.setContentView(R.layout.survey_welcome_dialog)
		dialog.show()

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SurveyUiState ->
			when(uiState) {
				is SurveyUiState.Loading -> {
					Log.d("1234567", "is SurveyUiState.Loading")
					viewModel.onOverallGameSkill()
					Log.d("1234567", "viewModel.onOverallGameSkill() is triggered")
					
				}
				is SurveyUiState.OverallGameSkill -> {
					surveyAdapter.submitList(uiState.options)

					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 0
						viewModel.onOverallGameSkill()
						Log.d("1234567", "OverallGameSkill's backButton is clickedListened")
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						Log.d("1234567", "OverallGameSkill's option is picked")
						binding.viewpager.currentItem = 1
						viewModel.onForehandLevel()
						Log.d("1234567", "OverallGameSkill's stuff is triggered")
					}
				}
				is SurveyUiState.ForehandLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						Log.d("1234567", "ForehandLevel's backButton is clickedListened")
						binding.viewpager.currentItem = 1
						viewModel.onForehandLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						Log.d("1234567", "ForehandLevel's option is picked")
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 2
						viewModel.onBackhandLevel()
						Log.d("1234567", "ForehandLevel's stuff is triggered")
					}
				}
				is SurveyUiState.BackhandLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 2
						viewModel.onBackhandLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 3
						viewModel.onSliceShotLevel()
					}
				}
				is SurveyUiState.SliceShotLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 3
						viewModel.onSliceShotLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 4
						viewModel.onServeLevel()
					}
				}
				is SurveyUiState.ServeLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 4
						viewModel.onServeLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 5
						viewModel.onNetGameLevel()
					}
				}
				is SurveyUiState.NetGameLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 5
						viewModel.onNetGameLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 6
						viewModel.onGameSpeedLevel()
					}
				}
				is SurveyUiState.GameSpeedLevel -> { // in this one and the next some option is pre-picked for some reason
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 6
						viewModel.onGameSpeedLevel()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 7
						viewModel.onTournamentParticipation()
					}
				}
				is SurveyUiState.TournamentParticipation -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem = 7
						viewModel.onTournamentParticipation()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere
						binding.viewpager.currentItem = 8
						viewModel.onTournamentTopPlaces()
					}
				}
				is SurveyUiState.TournamentTopPlaces -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem -= 1
						viewModel.onTournamentTopPlaces()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere.
						binding.viewpager.currentItem = 0
						viewModel.onOverallGameSkill() // temporary. supposed to lead to the results page
					}
				}
				is SurveyUiState.Error -> {}
			}
		}
	}
}