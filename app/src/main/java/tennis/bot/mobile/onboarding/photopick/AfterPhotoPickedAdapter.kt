package tennis.bot.mobile.onboarding.photopick

import tennis.bot.mobile.R
import javax.inject.Inject

class AfterPhotoPickedAdapter @Inject constructor(): PhotoPickAdapter() {

	override fun onBindViewHolder(holder: RecyclerCircledPhotoItemViewHolder, item: Any) {
		super.onBindViewHolder(holder, item)
		if ((item as CircledImage).isSelected) holder.itemView.setBackgroundResource(R.drawable.circle_photo_outline);
		holder.binding.image.alpha = 0.4f
	}
}