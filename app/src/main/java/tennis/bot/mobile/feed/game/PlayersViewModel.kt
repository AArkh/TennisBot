package tennis.bot.mobile.feed.game

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.requestcreation.RequestCreationRepository
import tennis.bot.mobile.feed.requestcreation.RequestNetwork
import tennis.bot.mobile.feed.searchopponent.OpponentItem
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsRepository
import tennis.bot.mobile.feed.searchopponent.SearchOpponentsViewModel
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.utils.getCurrentFormattedDateAndTimeForNetwork
import tennis.bot.mobile.utils.showToast
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
open class PlayersViewModel @Inject constructor(
	private val repository: SearchOpponentsRepository,
	private val requestRepository: RequestCreationRepository,
	private val userProfileAndEnumsRepository: UserProfileAndEnumsRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	val playersPager = Pager(
		config = PagingConfig(
			pageSize = SearchOpponentsViewModel.PAGE_SIZE,
			maxSize = SearchOpponentsViewModel.PAGE_SIZE + (SearchOpponentsViewModel.PAGE_SIZE * 2), // since paging library stores all received pages using memory so we add this thing, to prevent in to store too much. the value is one recommended in docs
			enablePlaceholders = true
		),
		pagingSourceFactory = { OpponentsDataSource() }
	).flow
	private var opponentItem: OpponentItem? = null
	private var searchInput: String = ""

	fun updateSearchInput(input: String) {
		searchInput = input
	}

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {
		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int {
			return 0
		}

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {
			val position = params.key ?: 0

			return try {
				val response = repository.getOpponents(searchInput, position, checkInvites = true)
				val opponentItemsList = response?.items?.let { repository.convertToOpponentItemList(it, true) }
				val nextPosition = position + 20

				val filteredData = opponentItemsList?.filter { item -> // filtering your own profile
					userProfileAndEnumsRepository.getProfile().id != item.id
				}

				Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
				LoadResult.Page(
					data = filteredData ?: emptyList(),
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response?.totalCount ?: 0)) null else nextPosition
				)

			}  catch (exception: IOException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: HttpException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			} catch (exception: NullPointerException) {
				FirebaseCrashlytics.getInstance().recordException(exception)
				return LoadResult.Error(exception)
			}
		}
	}

	fun checkPermissionToInvite(opponentItem: OpponentItem, navigationCallback: (isPermitted: Boolean) -> Unit) {
		viewModelScope.launch {
			if (requestRepository.getPermissionToCreate() == true) {
				navigationCallback.invoke(true)
				this@PlayersViewModel.opponentItem = opponentItem
			} else {
				navigationCallback.invoke(false)
			}
		}
	}

	fun onSendingPlayerRequestResponse(targetPlayerId: Long?, comment: String?, successCallback: (opponentItem: OpponentItem) -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			val profile = userProfileAndEnumsRepository.getProfile()
			kotlin.runCatching {
				if (!requestRepository.postAddRequest(
					RequestNetwork(
						targetPlayerId = targetPlayerId,
						cityId = profile.cityId,
						districtId = profile.districtId,
						date = getCurrentFormattedDateAndTimeForNetwork(),
						gameType = 1, // default - single
						paymentTypeId = 1, // default - 50/50
						comment = comment ?: ""
					)
				)) throw IllegalArgumentException("Failed to postAddRequest")
			}.onFailure {
				FirebaseCrashlytics.getInstance().recordException(it)
				context.showToast(context.getString(R.string.error_text))
			}.onSuccess {
				successCallback.invoke(opponentItem!!)
			}
		}
	}
}