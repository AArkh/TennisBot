package tennis.bot.mobile.onboarding.survey

import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.core.CoreUtilsItem
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
		val dialogButton = dialog.findViewById<Button>(R.id.dialog_buttonStart)
		dialog.show()
		dialogButton.setOnClickListener { dialog.dismiss() }

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: SurveyUiState ->
			when(uiState) {
				is SurveyUiState.OverallGameSkill -> {
					surveyAdapter.submitList(uiState.options)

					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(0, pickedOption))
						binding.viewpager.currentItem += 1
						viewModel.onForehandLevel()
					}
				}
				is SurveyUiState.ForehandLevel -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(1, pickedOption))
						binding.viewpager.currentItem = 2
						viewModel.onBackhandLevel()
					}
				}
				is SurveyUiState.BackhandLevel -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(2, pickedOption))
						binding.viewpager.currentItem = 3
						viewModel.onSliceShotLevel()
					}
				}
				is SurveyUiState.SliceShotLevel -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(3, pickedOption))
						binding.viewpager.currentItem = 4
						viewModel.onServeLevel()
					}
				}
				is SurveyUiState.ServeLevel -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(4, pickedOption))
						binding.viewpager.currentItem = 5
						viewModel.onNetGameLevel()
					}
				}
				is SurveyUiState.NetGameLevel -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(5, pickedOption))
						binding.viewpager.currentItem = 6
						viewModel.onGameSpeedLevel()
					}
				}
				is SurveyUiState.GameSpeedLevel -> { // in this one and the next some option is pre-picked for some reason
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere. find out which one is triggered
						surveyAdapter.submitList(viewModel.onPickedOption(6, pickedOption))
						binding.viewpager.currentItem = 7
						viewModel.onTournamentParticipation()
					}
				}
				is SurveyUiState.TournamentParticipation -> { // todo сделать как тут и ниже, только красиво
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere
						surveyAdapter.submitList(viewModel.onPickedOption(7, pickedOption))
						binding.viewpager.currentItem = 8
						viewModel.onTournamentTopPlaces()
					}
				}
				is SurveyUiState.TournamentTopPlaces -> {
					updateUiData(uiState) { pickedOption ->
						// save the answer somewhere
						surveyAdapter.submitList(viewModel.onPickedOption(8, pickedOption))
						binding.viewpager.currentItem = 0
						viewModel.onOverallGameSkill()
					}
				}
			}
		}
	}

	private fun updateUiData(uiState: SurveyUiState, clickListener: (id: Int) -> Unit) {
		binding.title.text = uiState.questionTitle
		binding.progressBar.setProgress(uiState.progressPercent, true) // fixme разобраться
		ObjectAnimator.ofInt(binding.progressBar, "progress", uiState.progressPercent)
			.setDuration(300)
			.start()

		binding.backButton.setOnClickListener {
			binding.viewpager.currentItem -= 1
			viewModel.onPreviousItem(uiState.prevState!!)
		}
		surveyAdapter.clickListener = clickListener
	}
}