package tennis.bot.mobile.core.authentication

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import tennis.bot.mobile.core.CoreFragment

abstract class AuthorizedCoreFragment<BINDING: ViewBinding>: CoreFragment<BINDING>() {

	private val authViewModel: AuthViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		authViewModel.setFragment(this as AuthorizedCoreFragment<ViewBinding>) // test if that's viable
	}
}