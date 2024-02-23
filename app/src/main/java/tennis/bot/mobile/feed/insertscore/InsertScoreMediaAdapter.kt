package tennis.bot.mobile.feed.insertscore

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.FragmentInsertScoreMediaItemBinding
import javax.inject.Inject

class InsertScoreMediaAdapter @Inject constructor(): CoreAdapter<InsertScoreMediaItemViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null

	override fun onBindViewHolder(holder: InsertScoreMediaItemViewHolder, item: Any) {
		val mediaItem = item as? InsertScoreMediaItem ?: throw IllegalArgumentException("Item must be InsertScoreMediaItem")

		onPhotoAdded(holder, mediaItem.isPhotoBackgroundActive, mediaItem.pickedPhoto)
		onVideoAdded(holder, mediaItem.pickedVideo)

		holder.binding.addPhotoHolder.setOnClickListener {
			clickListener?.invoke(InsertScoreFragment.ADD_PHOTO)
		}

		holder.binding.addVideoHolder.setOnClickListener {
			clickListener?.invoke(InsertScoreFragment.ADD_VIDEO)
		}

		holder.binding.deletePhotoButton.setOnClickListener {
			holder.binding.addPhotoHolder.dispose()
			holder.binding.addPhotoHolder.load(null)
			clickListener?.invoke(InsertScoreFragment.DELETE_PHOTO)
		}

		holder.binding.deleteVideoButton.setOnClickListener {
			holder.binding.addVideoHolder.dispose()
			holder.binding.addVideoHolder.load(null)
			clickListener?.invoke(InsertScoreFragment.DELETE_VIDEO)
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsertScoreMediaItemViewHolder {
		val binding = FragmentInsertScoreMediaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return InsertScoreMediaItemViewHolder(binding)
	}

	private fun onPhotoAdded(holder: InsertScoreMediaItemViewHolder, isPhotoBackgroundActive: Boolean, photoUri: Uri?) {
		if (photoUri != null) {
			holder.binding.addPhotoHolder.load(photoUri)
			holder.binding.photoHint.visibility = View.INVISIBLE
			holder.binding.deletePhotoButton.visibility = View.VISIBLE
		} else {
			val addPhotoOutline = if (isPhotoBackgroundActive) {
				R.drawable.outline_8dp_2dp_active
			} else {
				R.drawable.dotted_background_corners_8dp
			}
			holder.binding.addPhotoHolder.setBackgroundResource(addPhotoOutline)
			holder.binding.photoHint.visibility = View.VISIBLE
			holder.binding.deletePhotoButton.visibility = View.INVISIBLE
		}
	}

	private fun onVideoAdded(holder: InsertScoreMediaItemViewHolder, videoUri: Uri?) {
		if (videoUri != null) {
			holder.binding.addVideoHolder.load(getVideoThumbnail(holder.binding.addVideoHolder.context, videoUri))
			holder.binding.videoHint.visibility = View.INVISIBLE
			holder.binding.videoDurationTimer.text = getVideoDuration(holder.binding.videoDurationTimer.context, videoUri)
			holder.binding.videoDurationTimer.visibility = View.VISIBLE
			holder.binding.deleteVideoButton.visibility = View.VISIBLE
		} else {
			holder.binding.videoHint.visibility = View.VISIBLE
			holder.binding.videoDurationTimer.visibility = View.INVISIBLE
			holder.binding.deleteVideoButton.visibility = View.INVISIBLE
		}
	}

	private fun getVideoThumbnail(context: Context, videoUri: Uri): Bitmap? {
		val retriever = MediaMetadataRetriever()

		try {
			retriever.setDataSource(context, videoUri)
			return retriever.frameAtTime
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			retriever.release()
		}

		return null
	}

	private fun getVideoDuration(context: Context, videoUri: Uri?): String  {
		val retriever = MediaMetadataRetriever()

		try {
			retriever.setDataSource(context, videoUri)
			val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
			val durationInMillis = durationString?.toLong() ?: 0L

			val minutes = (durationInMillis / 1000 / 60).toInt()
			val seconds = (durationInMillis / 1000 % 60).toInt()

			return String.format("%02d:%02d", minutes, seconds)
		} catch (e: Exception) {
			e.printStackTrace()
		} finally {
			retriever.release()
		}
		return "00:00"
	}
}

class InsertScoreMediaItemViewHolder(
	val binding: FragmentInsertScoreMediaItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class InsertScoreMediaItem(
	val pickedPhoto: Uri? = null,
	val pickedVideo: Uri? = null,
	val isPhotoBackgroundActive: Boolean = false
): CoreUtilsItem()