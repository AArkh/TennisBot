package tennis.bot.mobile.core

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class CoreBottomSheetDialogFragment<BINDING : ViewBinding>() : BottomSheetDialogFragment() {

    protected abstract val bindingInflation: Inflation<BINDING>
    protected lateinit var binding: BINDING

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflation(inflater)
        return binding.root
    }
}