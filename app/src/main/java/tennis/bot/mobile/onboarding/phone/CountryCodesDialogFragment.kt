package tennis.bot.mobile.onboarding.phone

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tennis.bot.mobile.core.CoreBottomSheetDialogFragment
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.databinding.FragmentCountryCodesBinding
import javax.inject.Inject


@AndroidEntryPoint
open class CountryCodesDialogFragment : CoreBottomSheetDialogFragment<FragmentCountryCodesBinding>() {
    override val bindingInflation: Inflation<FragmentCountryCodesBinding> =
        FragmentCountryCodesBinding::inflate

    @Inject
    open lateinit var countryAdapter: CountryCodesAdapter
    private val viewModel: CountryCodesViewModel by viewModels()

    protected open var hasFixedHeight = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.countriesListRv.adapter = countryAdapter
        binding.countriesListRv.layoutManager = LinearLayoutManager(requireContext())
        binding.closeButtonIv.setOnClickListener {
            dialog?.dismiss()
        }
        countryAdapter.clickListener = {
            requireActivity().supportFragmentManager.setFragmentResult(
                COUNTRY_REQUEST_CODE_KEY,
                bundleOf(SELECTED_COUNTRY_CODE_KEY to it.countryCode, SELECTED_COUNTRY_ICON_KEY to it.icon)
            )
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

        if (hasFixedHeight) {
            val listener = object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.root.layoutParams.height = binding.root.height
                    binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(listener)
        }

        dialog?.setOnShowListener {
            val dialog: BottomSheetDialog = dialog as BottomSheetDialog
            val inputMethodManager = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val bottomSheetView = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (inputMethodManager.isAcceptingText) {
                BottomSheetBehavior.from(bottomSheetView as View).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    companion object {
        const val COUNTRY_REQUEST_CODE_KEY = "COUNTRY_CODE_KEY"
        const val SELECTED_COUNTRY_CODE_KEY = "SELECTED_COUNTRY_CODE_KEY"
        const val SELECTED_COUNTRY_ICON_KEY = "SELECTED_COUNTRY_ICON_KEY"
    }
}