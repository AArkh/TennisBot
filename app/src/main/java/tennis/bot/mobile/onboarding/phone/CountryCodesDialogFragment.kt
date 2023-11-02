package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentCountryCodesBinding
import javax.inject.Inject

@AndroidEntryPoint
class CountryCodesDialogFragment : CoreBottomSheetDialogFragment<FragmentCountryCodesBinding>() {
    override val bindingInflation: Inflation<FragmentCountryCodesBinding> = FragmentCountryCodesBinding::inflate
    @Inject
    lateinit var countryAdapter: PhoneInputAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countriesListRv.adapter = countryAdapter
        binding.countriesListRv.layoutManager = LinearLayoutManager(requireContext())
        binding.closeButtonIv.setOnClickListener {
            dialog?.dismiss()
        }
//        dialog?.show()
        val countriesList = listOf(
            CountryItem(R.drawable.russia_big, "Россия", "+7"),
            CountryItem(R.drawable.ukraine, "Украина", "+380"),
            CountryItem(R.drawable.belarus, "Беларусь", "+375"),
            CountryItem(R.drawable.kazakhstan, "Казахстан", "+7"),
            CountryItem(R.drawable.canada, "Канада", "+1")
        )
        countryAdapter.setListAndNotify(countriesList)
    }
}