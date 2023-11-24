package tennis.bot.mobile.onboarding.photopick

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.RecyclerCircledPhotoItemBinding
import javax.inject.Inject

class PhotoPickAdapter @Inject constructor(): CoreAdapter<RecyclerCircledPhotoItemViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerCircledPhotoItemViewHolder, item: Any) {
        val circledImage = item as? CircledImage
            ?: throw IllegalArgumentException("Item must be LoginProposalImage")
        holder.binding.image.setImageResource(circledImage.imageRes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerCircledPhotoItemViewHolder {
        val binding = RecyclerCircledPhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerCircledPhotoItemViewHolder(binding)
    }
}

class RecyclerCircledPhotoItemViewHolder(
    val binding: RecyclerCircledPhotoItemBinding
) : RecyclerView.ViewHolder(binding.root)

class CircledImage(@DrawableRes val imageRes: Int): CoreUtilsItem()