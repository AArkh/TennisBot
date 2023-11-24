package tennis.bot.mobile.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import tennis.bot.mobile.R

abstract class CoreBottomSheetDialogFragment<BINDING : ViewBinding> : BottomSheetDialogFragment() {

    protected abstract val bindingInflation: Inflation<BINDING>
    protected lateinit var binding: BINDING

    override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflation(inflater)
        return binding.root
    }

    fun <FlowType> subscribeToFlowOn(
        flow: Flow<FlowType>,
        state: Lifecycle.State = Lifecycle.State.CREATED,
        action: suspend (FlowType) -> Unit
    ) {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(state) {
                flow.collect { state -> action(state) }
            }
        }
    }
}