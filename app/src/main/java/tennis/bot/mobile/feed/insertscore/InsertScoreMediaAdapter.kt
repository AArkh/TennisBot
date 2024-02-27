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
import tennis.bot.mobile.databinding.RecyclerEmptyItemBinding
import tennis.bot.mobile.databinding.RecyclerMediaItemBinding
import tennis.bot.mobile.databinding.RecyclerMediaTitleBinding
import tennis.bot.mobile.databinding.RecyclerSideNoteItemBinding
import tennis.bot.mobile.profile.account.EmptyItemViewHolder
import javax.inject.Inject

class InsertScoreMediaAdapter @Inject constructor(): CoreAdapter<RecyclerView.ViewHolder>() {
	var clickListener: ((item: String) -> Unit)? = null

	companion object {
		private const val TITLE = 0
		private const val MEDIA = 1
		private const val SIDE_NOTE = 2
		private const val OTHER = 3
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any) {
		when(holder) {
			is InsertScoreMediaTitleViewHolder -> {}
			is InsertScoreMediaItemViewHolder -> {
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
			is InsertScoreMediaSideNoteViewHolder -> {
				val sideNoteItem = item as? SideNoteItem ?: throw IllegalArgumentException("Item must be SideNoteItem")

				if (sideNoteItem.title != null) holder.binding.sideNoteTitle.text = sideNoteItem.title
				if (sideNoteItem.text != null) holder.binding.sideNoteText.text = sideNoteItem.title
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(parent.context)

		return when(viewType) {
			TITLE -> {
				val binding = RecyclerMediaTitleBinding.inflate(inflater, parent, false)
				InsertScoreMediaTitleViewHolder(binding)
			}
			MEDIA -> {
				val binding = RecyclerMediaItemBinding.inflate(inflater, parent, false)
				InsertScoreMediaItemViewHolder(binding)
			}
			SIDE_NOTE -> {
				val binding = RecyclerSideNoteItemBinding.inflate(inflater, parent, false)
				InsertScoreMediaSideNoteViewHolder(binding)
			}
			else -> {
				val binding = RecyclerEmptyItemBinding.inflate(inflater, parent, false)
				EmptyItemViewHolder(binding)
			}
		}
	}

	override fun getItemViewType(position: Int): Int {
		return when(position) {
			0 -> TITLE
			1 -> MEDIA
			2 -> SIDE_NOTE
			else -> OTHER
		}
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
			holder.binding.addPhotoHolder.load(addPhotoOutline)
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
			holder.binding.addVideoHolder.load(R.drawable.dotted_background_corners_8dp)
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

class InsertScoreMediaTitleViewHolder(
	val binding: RecyclerMediaTitleBinding
) : RecyclerView.ViewHolder(binding.root)

class InsertScoreMediaItemViewHolder(
	val binding: RecyclerMediaItemBinding
) : RecyclerView.ViewHolder(binding.root)

class InsertScoreMediaSideNoteViewHolder(
	val binding: RecyclerSideNoteItemBinding
) : RecyclerView.ViewHolder(binding.root)

object MediaTitle: CoreUtilsItem()

data class InsertScoreMediaItem(
	val pickedPhoto: Uri? = null,
	val pickedVideo: Uri? = null,
	val isPhotoBackgroundActive: Boolean = false
): CoreUtilsItem()

data class SideNoteItem(
	val title: String? = null,
	val text: String? = null
): CoreUtilsItem()