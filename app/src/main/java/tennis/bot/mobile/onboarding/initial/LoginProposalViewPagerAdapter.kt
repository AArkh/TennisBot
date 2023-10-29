package tennis.bot.mobile.onboarding.initial

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tennis.bot.mobile.core.CoreAdapter
import tennis.bot.mobile.databinding.ItemTitledTextBinding
import javax.inject.Inject

class LoginProposalViewPagerAdapter @Inject constructor() : CoreAdapter<TitledTextViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitledTextViewHolder {
        val binding = ItemTitledTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TitledTextViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TitledTextViewHolder, item: Any) {
        val titledText = item as? TitledText ?: throw IllegalArgumentException("Item must be TitledText")
        holder.binding.title.text = titledText.title
        holder.binding.description.text = titledText.text
    }
}

class TitledTextViewHolder(
    val binding: ItemTitledTextBinding,
) : RecyclerView.ViewHolder(binding.root)

data class TitledText(
    val title: String,
    val text: String,
)