package tennis.bot.mobile.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

typealias Inflation<T> = (LayoutInflater) -> T

/**
 * Requires android:fitsSystemWindows="true" to work
 */
abstract class CoreFragment<BINDING : ViewBinding> : Fragment() {

    protected abstract val bindingInflation: Inflation<BINDING>

    protected lateinit var binding: BINDING
    // Property to determine if the view goes under the status bar.
    open var drawUnderStatusBar: Boolean = false
    open var adjustToKeyboard: Boolean = false
    private var initialBottomPadding: Int = -1

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindingInflation(inflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insetsCompat: WindowInsetsCompat ->
            val statusBarInsets: Insets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboardInsets: Insets = insetsCompat.getInsets(WindowInsetsCompat.Type.ime())
            val newStatusBarTop = if (drawUnderStatusBar) 0 else statusBarInsets.top
            val keyboardBottom = if (adjustToKeyboard) keyboardInsets.bottom else 0
            val bottomPadding = if (initialBottomPadding == -1) {
                initialBottomPadding = binding.root.paddingBottom
                initialBottomPadding + statusBarInsets.bottom + keyboardBottom
            } else {
                initialBottomPadding + statusBarInsets.bottom + keyboardBottom
            }
            binding.root.updatePadding(
                top = newStatusBarTop,
                bottom = bottomPadding
            )
            val newInsets = Insets.of(statusBarInsets.left, newStatusBarTop, statusBarInsets.right, statusBarInsets.bottom)
            WindowInsetsCompat.Builder()
                .setInsets(WindowInsetsCompat.Type.systemBars(), newInsets)
                .build()
        }
        ViewCompat.requestApplyInsets(binding.root)
        return binding.root
    }
}