package tennis.bot.mobile.feed.searchopponent

import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.feed.PostData
import tennis.bot.mobile.feed.PostParent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedPostsMapper @Inject constructor() {


}

data class NewPlayerPostItem( // 1
    val id: Long, // i don't want to store two is's so for now let's tick to the post one
    val postType: Int,
    val totalLikes: Int,
    val liked: Boolean,
    val addedAt: String?,
    val infoPanel: String, // location, experience and rating are here
    val newPlayerPost: PostParent.NewPlayerPost,
    val name: String,
    val isMale: Boolean,
    val picFile: String?
): CoreUtilsItem()

data class MatchRequestPostItem( // 2
    val id: Long, // i don't want to store two is's so for now let's tick to the post one
    val postType: Int,
    val totalLikes: Int,
    val liked: Boolean,
    val addedAt: String?,
    val locationSubTitle: String,
    val experience: String,

    val matchRequestPost: PostParent.MatchRequestPost,
): CoreUtilsItem()