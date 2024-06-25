package tennis.bot.mobile.profile.matches

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import retrofit2.HttpException
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchesRepository @Inject constructor(
	private val api: MatchesApi,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository
) {

	private val someOtherFormatter = SimpleDateFormat("d MMMM, HH:mm", Locale("ru", "RU"))
	private val formats = listOf(
		"yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ",
		"yyyy-MM-dd'T'hh:mm:ss'Z'"
	)

	private fun List<MatchResponseItem>.convertToMatchItemList(): List<MatchItem> {
		return map { matchResponseItem ->
			val dateTime = convertDateAndTime(matchResponseItem.playedAt)

			MatchItem(
				id = matchResponseItem.id,
				isWin = matchResponseItem.win,
				isDouble = matchResponseItem.isDouble,
				player1 = matchResponseItem.players.getOrNull(0)!!,
				player2 = matchResponseItem.players.getOrNull(1)!!,
				player3 = matchResponseItem.players.getOrNull(2),
				player4 = matchResponseItem.players.getOrNull(3),
				score = "${matchResponseItem.headToHead1} - ${matchResponseItem.headToHead2}",
				tennisSets = matchResponseItem.gameSets,
				dateTime = dateTime
			)
		}
	}

	private fun convertDateAndTime(dateString: String): String {
		for (format in formats) {
			try {
				val dateTimeFormatter = SimpleDateFormat(format, Locale.getDefault())
				val timeStampMs = dateTimeFormatter.parse(dateString)
				return someOtherFormatter.format(timeStampMs) ?: ""
			} catch (_: Exception) {}
		}

		return ""
	}

	inner class MatchesDataSource : PagingSource<Int, MatchItem>() {

		override fun getRefreshKey(state: PagingState<Int, MatchItem>): Int {
			return 0
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MatchItem> {
			val position = params.key ?: 0
			return try {
				val response = api.getScores(userProfileAndEnumsRepository.getProfile().id, position)
				val matchItemsList = response.body()?.items?.convertToMatchItemList()
				val nextPosition = position + 20

				Log.d("MatchesDataSource", "Loading page starting from position: $nextPosition")
				LoadResult.Page(
					data = matchItemsList!!,
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response.body()?.totalCount ?: 0)) null else nextPosition
				)
			} catch (exception: IOException) {
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				return LoadResult.Error(exception)
			} catch (exception: NullPointerException) {
				return LoadResult.Error(exception)
			}
		}
	}
}