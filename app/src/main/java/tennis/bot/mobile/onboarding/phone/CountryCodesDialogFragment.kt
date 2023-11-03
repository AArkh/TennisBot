package tennis.bot.mobile.onboarding.phone

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    private val viewModel: CountryCodesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countriesListRv.adapter = countryAdapter
        binding.countriesListRv.layoutManager = LinearLayoutManager(requireContext())
        binding.closeButtonIv.setOnClickListener {
            dialog?.dismiss()
        }
        countryAdapter.clickListener = {
            requireActivity().supportFragmentManager.setFragmentResult(COUNTRY_REQUEST_CODE_KEY, bundleOf(SELECTED_COUNTRY_CODE_KEY to it.countryCode, ))
            requireActivity().supportFragmentManager.setFragmentResult(COUNTRY_REQUEST_ICON_KEY, bundleOf(SELECTED_COUNTRY_ICON_KEY to it.icon))
            dialog?.dismiss()
        }

        binding.searchBarEt.addTextChangedListener {
            viewModel.onSearchInput(it.toString())
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiStateFlow.collect { countryList ->
                    countryAdapter.submitList(countryList)
                }
            }
        }

        // todo Диалог вылезает не полностью после того как клава выдвигается
        // todo либо ручками анимировано вызывать setStateExpanded, либо просто на фулскрин выдвигать диалог по-умолчанию

        val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.layoutParams.height = binding.root.height
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }



    companion object {
        const val COUNTRY_REQUEST_CODE_KEY = "COUNTRY_CODE_KEY"
        const val SELECTED_COUNTRY_CODE_KEY = "SELECTED_COUNTRY_CODE_KEY"
        const val COUNTRY_REQUEST_ICON_KEY = "COUNTRY_REQUEST_ICON_KEY"
        const val SELECTED_COUNTRY_ICON_KEY = "SELECTED_COUNTRY_ICON_KEY"
    }
}