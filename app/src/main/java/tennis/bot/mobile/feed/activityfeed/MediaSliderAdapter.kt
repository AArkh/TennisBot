package tennis.bot.mobile.feed.activityfeed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import coil.decode.VideoFrameDecoder
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
			val player = initiateVideoPlayer(feedMediaItem.mediaUrl!!, holder.binding.videoPlayer.context)
			holder.binding.videoPlayer.player = player
			player.addListener(object : Player.Listener {
				override fun onPlaybackStateChanged(state: Int) {
					super.onPlaybackStateChanged(state)
					if (state == Player.STATE_READY) {
//						holder.binding.videoDurationTimer.text = formatMillisToMinutesAndSeconds(player.duration)
//						holder.binding.videoDurationTimer.visibility = View.VISIBLE
						holder.binding.image.visibility = View.INVISIBLE
						holder.binding.playVideoButton.visibility = View.INVISIBLE
						holder.binding.videoPlayer.visibility = View.VISIBLE
						player.removeListener(this)
					}
				}
			})
			holder.binding.playVideoButton.visibility = View.VISIBLE

			holder.binding.image.load(feedMediaItem.mediaUrl) {
				decoderFactory { result, options, _ -> VideoFrameDecoder(result.source, options) }
				crossfade(true)
			}
			holder.binding.playVideoButton.setOnClickListener {

			}

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

	private fun initiateVideoPlayer(videoUrl: String, context: Context): Player {
		val mediaItem = MediaItem.fromUri(videoUrl)
		val player = ExoPlayer.Builder(context).build()
		player.setMediaItem(mediaItem)
		player.playWhenReady = true
		player.prepare()

		return player
	}
	fun formatMillisToMinutesAndSeconds(milliseconds: Long): String {
		val totalSeconds = milliseconds / 1000
		val minutes = totalSeconds / 60
		val seconds = totalSeconds % 60
		return String.format("%02d:%02d", minutes, seconds)
	}

}

class ImageItemViewHolder(
	val binding: PagerMediaItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class FeedMediaItem(
	val mediaUrl: String? = null,
	val isVideo: Boolean = false
): CoreUtilsItem()