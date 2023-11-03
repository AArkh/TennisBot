package tennis.bot.mobile.core

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

// DiffUtils
abstract class CoreAdapter<ViewHolder: RecyclerView.ViewHolder> : RecyclerView.Adapter<ViewHolder>() {

    protected var items: List<CoreUtilsItem> = listOf()

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = onBindViewHolder(holder, items[position])

    abstract fun onBindViewHolder(holder: ViewHolder, item: Any)

    @SuppressLint("NotifyDataSetChanged")
    fun setListAndNotify(items: List<CoreUtilsItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun submitList(newList: List<CoreUtilsItem>) {
        val diffResult = DiffUtil.calculateDiff(CoreDiffCallback(ArrayList(items), newList))
        //todo make CoreDiffCallback instance resuable?
        items = newList
        diffResult.dispatchUpdatesTo(this)
    }
}

abstract class CoreUtilsItem {

    open fun isSameItem(other: CoreUtilsItem): Boolean = this == other

    open fun isSameContent(other: CoreUtilsItem): Boolean = other::class.java.name == this::class.java.name
}

class CoreDiffCallback(
    private val oldList: List<CoreUtilsItem>,
    private val newList: List<CoreUtilsItem>
): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isSameItem(newList[newItemPosition]) 
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isSameContent(newList[newItemPosition])
    }

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
}