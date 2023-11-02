package tennis.bot.mobile.core

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

// DiffUtils
abstract class CoreAdapter<ViewHolder: RecyclerView.ViewHolder, Item: CoreUtilsItem> : RecyclerView.Adapter<ViewHolder>() {

    protected var items: List<Item> = listOf()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBindViewHolder(holder, items[position])

    abstract fun onBindViewHolder(holder: ViewHolder, item: Item)

    @SuppressLint("NotifyDataSetChanged")
    fun setListAndNotify(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }
}

abstract class CoreUtilsItem {

    abstract fun isSameItem(other: CoreUtilsItem): Boolean

    open fun isSameContent(other: CoreUtilsItem): Boolean = this == other
}