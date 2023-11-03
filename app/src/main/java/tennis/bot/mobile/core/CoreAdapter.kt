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
        items.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}
}

abstract class CoreUtilsItem {
    val fields = this::class.java.declaredFields

    open fun isSameItem(other: CoreUtilsItem): Boolean = this == other

    open fun isSameContent(other: CoreUtilsItem): Boolean = 
        this.fields.contentEquals(other.fields)
}

class CoreDiffCallback (
    private val oldList: List<CoreUtilsItem>,
    private val newList: List<CoreUtilsItem>
): DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].isSameItem(newList[newItemPosition]) 
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].isSameContent(newList[newItemPosition]) -> true
            else -> false
        }
    }
    override fun getOldListSize(): Int {
        return oldList.size
    }
    override fun getNewListSize(): Int {
        return newList.size
    }
}