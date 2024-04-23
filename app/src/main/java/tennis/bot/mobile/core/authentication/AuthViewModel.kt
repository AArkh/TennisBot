package tennis.bot.mobile.core.authentication

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tennis.bot.mobile.onboarding.initial.LoginProposalFragment
import tennis.bot.mobile.utils.destroyBackstack
import tennis.bot.mobile.utils.goToAnotherSectionFragment
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
	private val repository: AuthTokenRepository
): ViewModel() {
	private var fragment: AuthorizedCoreFragment<ViewBinding>? = null


	fun setFragment(authorizedFragment: AuthorizedCoreFragment<ViewBinding>) {
		fragment = authorizedFragment
		authorizedFragment.lifecycleScope.launch (Dispatchers.Main) {
			authorizedFragment.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
				repository.unAuthEventsFlow.collectLatest {
					Log.d("123456", "something happening here")
					fragment?.parentFragmentManager?.destroyBackstack()
					fragment?.parentFragmentManager?.goToAnotherSectionFragment(LoginProposalFragment())
				}
			}
		}
	}
}