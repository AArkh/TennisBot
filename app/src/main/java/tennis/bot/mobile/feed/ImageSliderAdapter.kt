package tennis.bot.mobile.feed

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerImageItemBinding
import tennis.bot.mobile.utils.CONTENT_LINK
import tennis.bot.mobile.utils.buildImageRequest
import javax.inject.Inject

class ImageSliderAdapter @Inject constructor(): CoreAdapter<ImageItemViewHolder>() {
	override fun onBindViewHolder(holder: ImageItemViewHolder, item: Any) {
		val mediaItem = item as? MediaItem ?: throw IllegalArgumentException("Item must be MediaItem")

		if(mediaItem.isVideo) {
			holder.binding.image.getVideoThumbnailFromString(CONTENT_LINK + mediaItem.media)
			holder.binding.videoDurationTimer.text = getVideoDuration(CONTENT_LINK + mediaItem.media)
			holder.binding.playVideoButton.visibility = View.VISIBLE
			holder.binding.videoDurationTimer.visibility = View.VISIBLE
		} else {
			holder.binding.image.load(buildImageRequest(holder.binding.image.context, mediaItem.media)) { crossfade(true) }
			holder.binding.videoDurationTimer.visibility = View.INVISIBLE
			holder.binding.playVideoButton.visibility = View.INVISIBLE
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
		val binding = PagerImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ImageItemViewHolder(binding)

	}

	private fun ImageView.getVideoThumbnailFromString(videoUrl: String?) {
		val retriever = MediaMetadataRetriever()
		retriever.setDataSource(videoUrl)
		val bitmap: Bitmap? = retriever.frameAtTime
		retriever.release()

		load(bitmap)
	}

	private fun getVideoDuration(videoUrl: String?): String {
		val retriever = MediaMetadataRetriever()
		retriever.setDataSource(videoUrl)

		try {
		val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
		val durationInMillis = durationString?.toLongOrNull() ?: 0

		val minutes = (durationInMillis / 1000 / 60).toInt()
		val seconds = (durationInMillis / 1000 % 60).toInt()

		retriever.release()

		return String.format("%02d:%02d", minutes, seconds)
	} catch (e: Exception) {
		e.printStackTrace()
	} finally {
		retriever.release()
	}
	return "00:00"
	}
}

class ImageItemViewHolder(
	val binding: PagerImageItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class MediaItem (
	val media: String?,
	val isVideo: Boolean = false
): CoreUtilsItem()