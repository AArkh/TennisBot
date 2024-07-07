package tennis.bot.mobile.utils.basicdialog

import androidx.annotation.ColorRes
import androidx.annotation.RawRes

data class BasicDialogUiState(
	@RawRes val animation: Int?,
	val title: String?,
	val text: String?,
	val topButtonText: String?,
	@ColorRes val topButtonColor: Int?,
	val bottomButtonText: String?,
	@ColorRes val bottomButtonColor: Int?,
	val isOneButton: Boolean = false,
	val isCancelable: Boolean = true,
	val isCanceledOnTouchOutside: Boolean = true
)
