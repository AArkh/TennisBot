package tennis.bot.mobile.onboarding.initial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.core.CoreUtilsItem
import tennis.bot.mobile.databinding.ItemLoginProposalRoundedImageBinding
import javax.inject.Inject

class LoginProposalImageAdapter @Inject constructor() : CoreAdapter<LoginProposalImageViewHolder>() {

    override fun onBindViewHolder(holder: LoginProposalImageViewHolder, item: Any) {
        val loginProposalImage = item as? LoginProposalImage
            ?: throw IllegalArgumentException("Item must be LoginProposalImage")
        holder.binding.image.setImageResource(loginProposalImage.imageRes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoginProposalImageViewHolder {
        val binding = ItemLoginProposalRoundedImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoginProposalImageViewHolder(binding)
    }
}

class LoginProposalImageViewHolder(
    val binding: ItemLoginProposalRoundedImageBinding
) : RecyclerView.ViewHolder(binding.root)

class LoginProposalImage(@DrawableRes val imageRes: Int) : CoreUtilsItem()