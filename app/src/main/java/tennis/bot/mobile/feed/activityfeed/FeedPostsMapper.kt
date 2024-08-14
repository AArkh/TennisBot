package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.onboarding.location.LocationDataMapper
import tennis.bot.mobile.onboarding.location.LocationRepository
import tennis.bot.mobile.profile.account.AccountPageViewModel
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.profile.matches.TennisSetNetwork
import tennis.bot.mobile.profile.matches.ratingChange
import tennis.bot.mobile.utils.formatDateForMatchPostItem
import tennis.bot.mobile.utils.view.AvatarImage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedPostsMapper @Inject constructor(
	@ApplicationContext private val context: Context,
	private val userProfileRepository: UserProfileAndEnumsRepository,
	private val locationRepository: LocationRepository,
	private val locationDataMapper: LocationDataMapper,
) {

	suspend fun convertToNewPlayerPostItem(postData: PostData): NewPlayerPostItem {
			val newPlayerPost = postData.post as? PostParent.NewPlayerPost ?: throw IllegalArgumentException("Item must be NewPlayerPost")

			val infoPanel = context.getString(
				R.string.player_new_info_panel,
				newPlayerPost.rating,
				userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, newPlayerPost.experience)),
				formatLocationDataForPost(newPlayerPost.cityId, null))

			return NewPlayerPostItem(
				id = postData.id,
				postType = postData.postType,
				totalLikes = postData.totalLikes,
				liked = postData.liked,
				addedAt = postData.addedAt,
				infoPanel = infoPanel,
				playerName = newPlayerPost.name,
				isMale = newPlayerPost.isMale,
				playerPhoto = newPlayerPost.picFile
			)
	}

	suspend fun convertToMatchRequestPostItem(postData: PostData): MatchRequestPostItem {
		val matchRequestPost = postData.post as? PostParent.MatchRequestPost ?: throw IllegalArgumentException("Item must be MatchRequestPost")

		return MatchRequestPostItem(
			id = postData.id,
			gameOrderId = matchRequestPost.gameOrderId,
			postType = matchRequestPost.type,
			totalLikes = postData.totalLikes,
			liked = postData.liked,
			addedAt = postData.addedAt,
			matchDate = matchRequestPost.date?.let { formatDateForMatchPostItem(it) },
			playerId = matchRequestPost.playerId,
			playerPhoto = matchRequestPost.playerPhoto,
			playerName = matchRequestPost.playerName,
			playerRating = matchRequestPost.playerRating,
			locationSubTitle = formatLocationDataForPost(matchRequestPost.cityId, matchRequestPost.districtId),
			experience = userProfileRepository.getEnumById(Pair(AccountPageViewModel.EXPERIENCE_TITLE, matchRequestPost.playerExperience)),
			comment = matchRequestPost.comment
		)
	}

	fun convertToScorePostItem(postData: PostData): ScorePostItem {
		val scorePost = postData.post as? PostParent.ScorePost ?: throw IllegalArgumentException("Item must be ScorePost")

		return ScorePostItem(
			id = postData.id,
			postType = scorePost.type,
			totalLikes = postData.totalLikes,
			liked = postData.liked,
			addedAt = postData.addedAt,
			creatorId = scorePost.creatorId,
			photo = scorePost.photo,
			video = scorePost.video,
			player1 = scorePost.player1,
			player2 = scorePost.player2,
			player3 = scorePost.player3,
			player4 = scorePost.player4,
			surface = scorePost.surface,
			duration = scorePost.duration,
			matchWon = scorePost.matchWon,
			sets = scorePost.sets,
			feedMediaItemsList = createListOfMedia(scorePost),
			matchResultsList = formMatchResultsList(scorePost, context)
		)
	}

	private fun createListOfMedia(item: PostParent.ScorePost): List<FeedMediaItem> {
		val theList = mutableListOf<FeedMediaItem>()
		if (item.video != null && item.photo != null) {
			theList.add(
				FeedMediaItem(
					mediaUrl = item.video,
					isVideo = true
				)
			)
			theList.add(FeedMediaItem(item.photo))
		} else if (item.video != null) {
			theList.add(
				FeedMediaItem(
					mediaUrl = item.video,
					isVideo = true
				)
			)
		} else if (item.photo != null) {
			theList.add(FeedMediaItem(item.photo))
		}
		theList.add(FeedMediaItem(item.player1.photo))
		theList.add(FeedMediaItem(item.player2.photo))
		if (item.player3 != null) {
			theList.add(FeedMediaItem(item.player3.photo))
			theList.add(FeedMediaItem(item.player4?.photo))
		}

		return theList.toList()
	}

	suspend fun formatLocationDataForPost(cityId: Int, districtId: Int?): String? {
		val locations = locationRepository.getLocations()
		val city = locationDataMapper.findCityString(locations, cityId)
		val district = if (districtId != null) {
			locationDataMapper.findDistrictStringFromCity(locations, cityId, districtId)
		} else {
			null
		}
		val location = if (district == null) {
			city
		} else {
			"$city(${district})"
		}

		return location
	}

	private fun formMatchResultsList(item: PostParent.ScorePost, context: Context): List<CoreUtilsItem> {
		val theList: MutableList<CoreUtilsItem> = mutableListOf()

		val (winner, loser) = if (item.matchWon) {
			if (item.player3 == null) item.player1 to item.player2 else item.player1 to item.player3
		} else {
			if (item.player3 == null) item.player2 to item.player1 else item.player3 to item.player1
		}

		val (winnerNames, loserNames) = if (item.matchWon) {
			context.getString(
				R.string.post_three_doubles_title,
				item.player1.name.substringBefore(" "),
				item.player2.name.substringBefore(" ")
			) to context.getString(
				R.string.post_three_doubles_title,
				item.player3?.name?.substringBefore(" "),
				item.player4?.name?.substringBefore(" "))
		} else {
			context.getString(
				R.string.post_three_doubles_title,
				item.player3?.name?.substringBefore(" "),
				item.player4?.name?.substringBefore(" ")) to context.getString(
				R.string.post_three_doubles_title,
				item.player1.name.substringBefore(" "),
				item.player2.name.substringBefore(" ")
			)
		}
		theList.add(
			MatchScoreItem(
				winnerName = if (item.player3 == null) winner.name else winnerNames,
				loserName = if (item.player3 == null) loser.name else loserNames,
				sets = item.sets.formSetsList(item.matchWon)
			)
		)
		theList.add(
			RatingItem(
				player1Rating = winner.powerNew,
				player2Rating = loser.powerNew,
				player1RatingDifference = ratingChange(winner.powerNew.toString(), winner.powerOld.toString()),
				player2RatingDifference = ratingChange(loser.powerNew.toString(), loser.powerOld.toString())
			)
		)
		if (item.player3 == null) {
			theList.add(
				HeadToHeadItem(
					score = "${item.player1.headToHead} - ${item.player2.headToHead}",
					playersLeftPhotos = listOf(AvatarImage(item.player1.photo)),
					playersRightPhotos = listOf(AvatarImage(item.player2.photo)),
				)
			)
		} else {
			theList.add(
				HeadToHeadItem(
					score = "${item.player1.headToHead} - ${item.player2.headToHead}",
					playersLeftPhotos = listOf(AvatarImage(item.player1.photo), AvatarImage(item.player2.photo)),
					playersRightPhotos = listOf(AvatarImage(item.player3.photo), AvatarImage(item.player4?.photo)),
				)
			)
		}
		theList.add(
			BonusItem(
				player1Bonus = winner.bonus,
				player2Bonus = loser.bonus,
				player1BonusDifference = winner.bonusChange,
				player2BonusDifference = loser.bonusChange
			)
		)

		return theList
	}

	private fun List<TennisSetNetwork>.formSetsList(isWinner: Boolean): List<TennisSetNetwork> {
		val newList: MutableList<TennisSetNetwork> = mutableListOf()

		if (isWinner) {
			return this
		} else {
			for (set in this) {
				newList.add(
					TennisSetNetwork(
					score1 = set.score2,
					score2 = set.score1,
					scoreTie1 = set.scoreTie2,
					scoreTie2 = set.scoreTie1
				)
				)
				return newList
			}
		}
		return emptyList()
	}
}

suspend fun formatLocationDataForPost(cityId: Int?, districtId: Int?, locationRepository: LocationRepository, locationDataMapper: LocationDataMapper): String? {
	val locations = locationRepository.getLocations()
	val city = if (cityId != null) {
		locationDataMapper.findCityString(locations, cityId)
	} else {
		null
	}
	val district = if (cityId != null && districtId != null) {
		locationDataMapper.findDistrictStringFromCity(locations, cityId, districtId)
	} else {
		null
	}
	val location = if (city == null && district == null) {
		null
	} else if (district == null) {
		city
	} else {
		"$city(${district})"
	}

	return location
}

