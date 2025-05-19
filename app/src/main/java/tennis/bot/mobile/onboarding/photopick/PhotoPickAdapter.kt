package tennis.bot.mobile.onboarding.photopick

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerCircledPhotoItemBinding
import javax.inject.Inject

open class PhotoPickAdapter @Inject constructor(): CoreAdapter<RecyclerCircledPhotoItemViewHolder>() {

    var clickListener: ((item: CircledImage) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerCircledPhotoItemViewHolder, item: Any) {
        val circledImage = item as? CircledImage ?: throw IllegalArgumentException("Item must be CircledImage")
        holder.binding.image.setImageResource(circledImage.imageRes)
        holder.itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerCircledPhotoItemViewHolder {
        val binding = RecyclerCircledPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerCircledPhotoItemViewHolder(binding)
    }
}

class RecyclerCircledPhotoItemViewHolder(
    val binding: RecyclerCircledPhotoItemBinding
) : RecyclerView.ViewHolder(binding.root)

data class CircledImage(
    @DrawableRes val imageRes: Int,
    val isSelected: Boolean = false
): CoreUtilsItem()