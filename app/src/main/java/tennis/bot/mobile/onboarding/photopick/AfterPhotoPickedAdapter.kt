package tennis.bot.mobile.onboarding.photopick

import androidx.appcompat.content.res.AppCompatResources
import tennis.bot.mobile.R
import javax.inject.Inject

class AfterPhotoPickedAdapter @Inject constructor(): PhotoPickAdapter() {

	override fun onBindViewHolder(holder: RecyclerCircledPhotoItemViewHolder, item: Any) {
		super.onBindViewHolder(holder, item)
		if ((item as CircledImage).isSelected) holder.itemView.foreground =
			AppCompatResources.getDrawable(holder.itemView.context, R.drawable.circle_photo_outline)
		else holder.binding.image.alpha = 0.4f
	}
}