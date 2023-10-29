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
abstract class CoreFragment<BINDING: ViewBinding> : Fragment() {

    protected abstract val bindingInflation: Inflation<BINDING>

    protected lateinit var binding: BINDING

    // Property to determine if the view goes under the status bar.
    open var drawUnderStatusBar: Boolean = false

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = bindingInflation(inflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insetsCompat: WindowInsetsCompat ->
            val statusBarInsets: Insets = insetsCompat.getInsets(WindowInsetsCompat.Type.systemBars())
            // If shouldGoUnderStatusBar is true, we set the insets to 0, else we apply the actual insets.
            val newStatusBarTop = if (drawUnderStatusBar) 0 else statusBarInsets.top
            v.rootView.updatePadding(
                top = newStatusBarTop,
                bottom = v.rootView.paddingBottom + statusBarInsets.bottom
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