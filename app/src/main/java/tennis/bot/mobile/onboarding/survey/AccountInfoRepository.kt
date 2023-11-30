package tennis.bot.mobile.onboarding.survey

import android.content.Context
import androidx.core.content.ContextCompat.getString
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountInfoRepository @Inject constructor(
	@ApplicationContext context: Context
) { // for storing account info throughout the onboarding process

	val questionsTitlesList = listOf(
		context.getString(R.string.survey_questionsTitlesList_1),
		context.getString(R.string.survey_questionsTitlesList_2),
		context.getString(R.string.survey_questionsTitlesList_3),
		context.getString(R.string.survey_questionsTitlesList_4),
		context.getString(R.string.survey_questionsTitlesList_5),
		context.getString(R.string.survey_questionsTitlesList_6),
		context.getString(R.string.survey_questionsTitlesList_7),
		context.getString(R.string.survey_questionsTitlesList_8),
		context.getString(R.string.survey_questionsTitlesList_9)
	)

	val optionsList = listOf(
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set1_1),
			context.getString(R.string.survey_options_item_set1_2),
			context.getString(R.string.survey_options_item_set1_3),
			context.getString(R.string.survey_options_item_set1_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set2_1),
			context.getString(R.string.survey_options_item_set2_2),
			context.getString(R.string.survey_options_item_set2_3),
			context.getString(R.string.survey_options_item_set2_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set3_1),
			context.getString(R.string.survey_options_item_set3_2),
			context.getString(R.string.survey_options_item_set3_3),
			context.getString(R.string.survey_options_item_set3_4)
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			true
		),
		SurveyOptionsItem(
			context.getString(R.string.survey_options_item_set4_no),
			context.getString(R.string.survey_options_item_set4_yes),
			"",
			"",
			true
		)
	)

	val sideNotesList = listOf(
		SideNoteItem(getString(context, R.string.survey_side_note_title_1), getString(context, R.string.survey_side_note_text_1)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_2), getString(context, R.string.survey_side_note_text_2)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_3), getString(context, R.string.survey_side_note_text_3)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_4), getString(context, R.string.survey_side_note_text_4)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_5), getString(context, R.string.survey_side_note_text_5)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_6), getString(context, R.string.survey_side_note_text_6)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_7), getString(context, R.string.survey_side_note_text_7)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_8), getString(context, R.string.survey_side_note_text_8)),
		SideNoteItem(getString(context, R.string.survey_side_note_title_9), getString(context, R.string.survey_side_note_text_9)),
	)

}

data class SideNoteItem(val sideNoteTitle: String, val sideNoteText: String)
