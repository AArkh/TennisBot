package tennis.bot.mobile.feed.activityfeed

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerMediaItemBinding
import tennis.bot.mobile.utils.buildImageRequest
import javax.inject.Inject

class ImageSliderAdapter @Inject constructor(): CoreAdapter<ImageItemViewHolder>() {
	override fun onBindViewHolder(holder: ImageItemViewHolder, item: Any) {
		val feedMediaItem = item as? FeedMediaItem ?: throw IllegalArgumentException("Item must be FeedMediaItem")

		if(feedMediaItem.isVideo) {
//			holder.binding.image.load(feedMediaItem.videoThumbnail)
//			holder.binding.videoDurationTimer.text = feedMediaItem.duration
//			holder.binding.playVideoButton.visibility = View.VISIBLE
//			holder.binding.videoDurationTimer.visibility = View.VISIBLE
			holder.binding.image.visibility = View.INVISIBLE
			holder.binding.videoPlayer.visibility = View.VISIBLE
			val mediaItem = MediaItem.fromUri(feedMediaItem.mediaUrl!!)
			val player = ExoPlayer.Builder(holder.binding.videoPlayer.context).build()
			holder.binding.videoPlayer.player = player
			player.setMediaItem(mediaItem)
			player.prepare()
		} else {
			holder.binding.image.load(buildImageRequest(holder.binding.image.context, feedMediaItem.mediaUrl)) { crossfade(true) }
			holder.binding.videoDurationTimer.visibility = View.INVISIBLE
			holder.binding.playVideoButton.visibility = View.INVISIBLE
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
		val binding = PagerMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ImageItemViewHolder(binding)

	}
}

class ImageItemViewHolder(
	val binding: PagerMediaItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class FeedMediaItem (
	val mediaUrl: String? = null,
	val videoThumbnail: Bitmap? = null,
	val duration: String? = null,
	val isVideo: Boolean = false
): CoreUtilsItem()