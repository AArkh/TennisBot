package tennis.bot.mobile.feed

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.PagerImageItemBinding
import tennis.bot.mobile.utils.buildImageRequest
import javax.inject.Inject

class ImageSliderAdapter @Inject constructor(): CoreAdapter<ImageItemViewHolder>() {
	override fun onBindViewHolder(holder: ImageItemViewHolder, item: Any) {
		val mediaItem = item as? MediaItem ?: throw IllegalArgumentException("Item must be MediaItem")

		if(mediaItem.isVideo) {
			holder.binding.image.load(mediaItem.videoThumbnail)
			holder.binding.videoDurationTimer.text = mediaItem.duration
			holder.binding.playVideoButton.visibility = View.VISIBLE
			holder.binding.videoDurationTimer.visibility = View.VISIBLE
		} else {
			holder.binding.image.load(buildImageRequest(holder.binding.image.context, mediaItem.mediaUrl)) { crossfade(true) }
			holder.binding.videoDurationTimer.visibility = View.INVISIBLE
			holder.binding.playVideoButton.visibility = View.INVISIBLE
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder {
		val binding = PagerImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ImageItemViewHolder(binding)

	}
}

class ImageItemViewHolder(
	val binding: PagerImageItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class MediaItem (
	val mediaUrl: String? = null,
	val videoThumbnail: Bitmap? = null,
	val duration: String? = null,
	val isVideo: Boolean = false
): CoreUtilsItem()