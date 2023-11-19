package tennis.bot.mobile.onboarding.location

import android.view.View
import tennis.bot.mobile.onboarding.phone.CountryCodeItemViewHolder
import tennis.bot.mobile.onboarding.phone.CountryItem
import tennis.bot.mobile.onboarding.phone.PhoneInputAdapter
import javax.inject.Inject

// fixme Сделай тут свой собственный адаптер. Помимо CountryCodeItemViewHolder нам нужна поддержка ещё двух
// типов холдеров - загрузка данных и ошибка. Для этого тебе придется отнаследоваться от CoreAdapter<Any>(), и в
// onBindViewHolder и onCreateViewHolder ты будешь получать Any, для холдера и для item. Придется их кастить и для каждого
// поддерживаемого типа свою логику писать. Например, для загрузки данных ты будешь показывать ProgressBar,
// а для ошибки - TextView с красным сообщением. Не забудь в адаптере переопределить getItemViewType, как ты делал
// в учебной прилажке с фотками с APOD и с марсохода.
class LocationAdapter @Inject constructor() : PhoneInputAdapter() {
    override fun onBindViewHolder(holder: CountryCodeItemViewHolder, item: Any) {
        val countryItem = item as? CountryItem ?: throw IllegalArgumentException("Item must be CountryItem")
        holder.binding.countryIconIv.visibility = View.GONE
        holder.binding.countryNameTv.text = countryItem.countryName
        holder.binding.countryCodeTv.visibility = View.GONE
        holder.itemView.setOnClickListener {
            clickListener?.invoke(item)
        }
    }
}