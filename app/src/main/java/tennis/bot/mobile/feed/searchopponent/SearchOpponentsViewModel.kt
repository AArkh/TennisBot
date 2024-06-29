package tennis.bot.mobile.feed.searchopponent

import android.content.Context
import android.util.Log
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import tennis.bot.mobile.R
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.utils.view.AvatarImage
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
open class SearchOpponentsViewModel @Inject constructor(
	private val repository: SearchOpponentsRepository,
	@ApplicationContext private val context: Context
): ViewModel() {

	companion object {
		const val OPPONENT_PICKED_REQUEST_KEY = "OPPONENT_PICKED_REQUEST_KEY"
		const val SELECTED_OPPONENT_KEY = "SELECTED_OPPONENT_KEY"
		const val PAGE_SIZE = 20
	}

	private val _uiStateFlow = MutableStateFlow(
		SearchOpponentsUiState(
			hintTitle = null,
			opponentsList = null,
			isNextButtonEnabled = false
		)
	)
	val uiStateFlow = _uiStateFlow.asStateFlow()

	private val _userInput = MutableStateFlow<String?>(null)
	val userInput: StateFlow<String?> = _userInput

	val opponentsPager = Pager(
			config = PagingConfig(
				pageSize = PAGE_SIZE,
				maxSize = PAGE_SIZE + (PAGE_SIZE * 2), // since paging library stores all received pages using memory so we add this thing, to prevent in to store too much. the value is one recommended in docs
				enablePlaceholders = true
			),
			pagingSourceFactory = { OpponentsDataSource() }
		).flow

	fun onReceivingScoreType(scoreType: Int) {
		when(scoreType) {
			AddScoreFragment.SCORE_SINGLE -> {
				_uiStateFlow.value = uiStateFlow.value.copy(
					scoreType = AddScoreFragment.SCORE_SINGLE,
					title = context.getString(R.string.opponent_search),
					hintTitle = context.getString(R.string.opponents_single_hint_title),
					opponentsList = arrayOfNulls(1),
					numberOfOpponents = 1
				)
			}
			AddScoreFragment.SCORE_DOUBLE -> {
				_uiStateFlow.value = uiStateFlow.value.copy(
					scoreType = AddScoreFragment.SCORE_DOUBLE,
					title = context.getString(R.string.opponent_double_title),
					hintTitle = context.getString(R.string.opponents_double_hint_title_1),
					opponentsList = arrayOfNulls(3),
					numberOfOpponents = 3
				)
			}
			AddScoreFragment.SCORE_TOURNAMENT -> {}
			AddScoreFragment.SCORE_FRIENDLY -> {}
		}
	}

	fun onSearchOpponentsInput(text: CharSequence) {
		viewModelScope.launch(Dispatchers.IO) {
			_userInput.emit(text.toString())
		}
	}

	fun onOpponentPicked(opponent: OpponentItem) {
		val currentState = uiStateFlow.value
		when(uiStateFlow.value.scoreType) {
			AddScoreFragment.SCORE_SINGLE -> {
				currentState.opponentsList?.set(0, opponent)
				_uiStateFlow.value = uiStateFlow.value.copy(
					isNextButtonEnabled = true
				)
			}
			AddScoreFragment.SCORE_DOUBLE -> {
				if (currentState.opponentsList?.get(0) == null) {
					currentState.opponentsList?.set(0, opponent)
					val newList = currentState.photosList?.toMutableList()
					newList?.add(0, AvatarImage(opponent.profilePicture))
					Log.d("photosList", "newList: $newList")
					_uiStateFlow.value = currentState.copy(
						photosList = newList?.toList(),
						hintTitle = context.getString(R.string.opponents_double_hint_title_2, 1)
					)
				} else if(uiStateFlow.value.opponentsList?.get(1) == null) {
					val newList = currentState.photosList?.toMutableList()
					newList?.add(1, AvatarImage(opponent.profilePicture))
					_uiStateFlow.value = currentState.copy(
						photosList = newList?.toList(),
						hintTitle = context.getString(R.string.opponents_double_hint_title_2, 2)
					)
					currentState.opponentsList[1] = opponent
				} else {
					val newList = currentState.photosList?.toMutableList()
					if(newList?.size != 2) {
						newList?.set(2, AvatarImage(opponent.profilePicture))
					} else {
						newList.add(2, AvatarImage(opponent.profilePicture))
					}
					currentState.opponentsList[2] = opponent
					_uiStateFlow.value = currentState.copy(
						photosList = newList?.toList(),
						isNextButtonEnabled = true
					)
				}
			}
			AddScoreFragment.SCORE_TOURNAMENT -> {}
			AddScoreFragment.SCORE_FRIENDLY -> {}
		}
	}

	fun onNextButtonClicked(activity: FragmentActivity, navigationCallback: () -> Unit) {
		activity.supportFragmentManager.setFragmentResult(
			OPPONENT_PICKED_REQUEST_KEY,
			bundleOf(
				SELECTED_OPPONENT_KEY to uiStateFlow.value.opponentsList
			)
		)
		navigationCallback.invoke()
	}

	inner class OpponentsDataSource : PagingSource<Int, OpponentItem>() {
		override fun getRefreshKey(state: PagingState<Int, OpponentItem>): Int { return 0 }

		override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OpponentItem> {
			val position = params.key ?: 0

			return try {
				val response = userInput.value?.let { repository.getOpponents(it, position) }
				val opponentItemsList = response?.items?.let { repository.convertToOpponentItemList(it) }
				val nextPosition = position + 20

				val filteredData = opponentItemsList?.filter { item ->
					(uiStateFlow.value.opponentsList)?.contains(item) != true
				}

				Log.d("OpponentsDataSource", "Loading page starting from position: $nextPosition")
				LoadResult.Page(
					data = filteredData ?: emptyList(),
					prevKey = if (position == 0) null else position - params.loadSize,
					nextKey = if (nextPosition >= (response?.totalCount ?: 0)) null else nextPosition
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