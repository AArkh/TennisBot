package tennis.bot.mobile.onboarding.survey

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentSurveyBinding
import tennis.bot.mobile.onboarding.photopick.PhotoPickUiState
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

		surveyAdapter.clickListener = { pickedOption ->
			// save the answer somewhere. find out which one is triggered
		}



		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SurveyUiState ->
			when(uiState) {
				is SurveyUiState.Loading -> {
					viewModel.onOverallGameSkill()
				}
				is SurveyUiState.OverallGameSkill -> {
					surveyAdapter.submitList(uiState.options)

					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					binding.backButton.setOnClickListener {
						binding.viewpager.currentItem -= 1
						viewModel.onOverallGameSkill()
					}
					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem += 1
						viewModel.onForehandLevel()
					}
				}
				is SurveyUiState.ForehandLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 2
						viewModel.onBackhandLevel()
					}
				}
				is SurveyUiState.BackhandLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 3
						viewModel.onSliceShotLevel()
					}
				}
				is SurveyUiState.SliceShotLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 4
						viewModel.onServeLevel()
					}
				}
				is SurveyUiState.ServeLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 5
						viewModel.onNetGameLevel()
					}
				}
				is SurveyUiState.NetGameLevel -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 6
						viewModel.onGameSpeedLevel()
					}
				}
				is SurveyUiState.GameSpeedLevel -> { // in this one and the next some option is pre-picked for some reason
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						binding.viewpager.currentItem = 7
						viewModel.onTournamentParticipation()
					}
				}
				is SurveyUiState.TournamentParticipation -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

					surveyAdapter.clickListener = { pickedOption ->
						// save the answer somewhere
						binding.viewpager.currentItem = 8
						viewModel.onTournamentTopPlaces()
					}
				}
				is SurveyUiState.TournamentTopPlaces -> {
					binding.title.text = uiState.questionTitle
					binding.progressBar.progress = uiState.progressPercent
					binding.sideNoteTitle.text = uiState.sideNoteTitle
					binding.sideNoteText.text = uiState.sideNoteText

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