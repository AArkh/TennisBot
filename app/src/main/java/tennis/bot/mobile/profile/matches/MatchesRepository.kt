package tennis.bot.mobile.profile.matches

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import tennis.bot.mobile.core.AuthTokenRepository
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.showToast
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchesRepository @Inject constructor(
	private val api: MatchesApi,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	private val tokenRepo: AuthTokenRepository,
	@ApplicationContext private val context: Context
) {

	private val dateTimeFormatter = SimpleDateFormat( "yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.getDefault())
	private val someOtherFormatter = SimpleDateFormat("d MMMM, HH:mm", Locale("ru", "RU"))

	@WorkerThread
	suspend fun getMatches() : MatchBasicResponse? {
		val response = api.getScores(userProfileAndEnumsRepository.getProfile().id)
		if (response.code() == 200) return response.body()
		if (response.code() == 404) context.showToast("Something went wrong")

		return MatchBasicResponse(0, emptyList())
	}

	@WorkerThread
	suspend fun getMatchItems(): List<MatchItem> {
		val networkMatches = getMatches()
		val matchResponseItems = networkMatches?.items
		return if (!matchResponseItems.isNullOrEmpty()) matchResponseItems.convertToMatchItemList()
		else emptyList()
	}

	private fun List<MatchResponseItem>.convertToMatchItemList(): List<MatchItem> {
		return map { matchResponseItem ->
			val playerOneProfilePic = matchResponseItem.players.getOrNull(0)?.photo
			val playerTwoProfilePic = matchResponseItem.players.getOrNull(1)?.photo

			val timeStampMs = dateTimeFormatter.parse(matchResponseItem.playedAt)
			val dateTime = someOtherFormatter.format(timeStampMs) ?: ""

			MatchItem(
				matchResponseItem.win,
				matchResponseItem.isDouble,
				playerOneProfilePic,
				matchResponseItem.players.getOrNull(0)?.name ?: "",
				matchResponseItem.players.getOrNull(0)?.rating.toString(),
				matchResponseItem.players.getOrNull(0)?.oldRating.toString(),
				playerTwoProfilePic,
				matchResponseItem.players.getOrNull(1)?.name ?: "",
				matchResponseItem.players.getOrNull(1)?.rating.toString(),
				matchResponseItem.players.getOrNull(1)?.oldRating.toString(),
				"${matchResponseItem.headToHead1} - ${matchResponseItem.headToHead2}",
				matchResponseItem.gameSets,
				dateTime
			)
		}
	}

	inner class MyDataSource : PagingSource<Int, MatchItem>() {

		override fun getRefreshKey(state: PagingState<Int, MatchItem>): Int? {
			TODO("Not yet implemented")
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MatchItem> {
			val position = params.key ?: 0
			return try {
				val response = api.getScores(userProfileAndEnumsRepository.getProfile().id, position)
				val matchItemsList = response.body()?.items?.convertToMatchItemList()
				if (position >= response.body()!!.totalCount) {
					return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
				} else {
					LoadResult.Page(
						data = matchItemsList!!,
						prevKey = if (position == 0) null else position,
						nextKey = if (response.body()?.totalCount == 0) null else position + 1
					)
				}
			} catch (exception: IOException) {
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				return LoadResult.Error(exception)
			}
		}
	}
}