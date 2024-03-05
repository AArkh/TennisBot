package tennis.bot.mobile.feed.searchopponent

data class SearchOpponentsUiState(
	val scoreType: Int? = null,
	val hintTitle: String?,
	val opponentsList: Array<OpponentItem?>?,
	val numberOfOpponents: Int = 1,
	val isNextButtonEnabled: Boolean

) { // studio wanted me to generate this
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as SearchOpponentsUiState

		if (hintTitle != other.hintTitle) return false
		if (opponentsList != null) {
			if (other.opponentsList == null) return false
			if (!opponentsList.contentEquals(other.opponentsList)) return false
		} else if (other.opponentsList != null) return false
		return numberOfOpponents == other.numberOfOpponents
	}

	override fun hashCode(): Int {
		var result = hintTitle?.hashCode() ?: 0
		result = 31 * result + (opponentsList?.contentHashCode() ?: 0)
		result = 31 * result + numberOfOpponents
		return result
	}
}