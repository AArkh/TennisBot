package tennis.bot.mobile.core

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding

abstract class AuthorizedFragment<BINDING : ViewBinding> : CoreFragment<BINDING>() {

    val viewModel: AuthKeyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setFragment(this)
    }
}