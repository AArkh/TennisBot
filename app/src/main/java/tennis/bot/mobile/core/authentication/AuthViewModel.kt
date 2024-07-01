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
					Log.d("AuthViewModel", "unAuthEventsFlow is triggered")
					repository.triggerUnAuthFlow(false) // keep watching whether it's properly working here. turned off for now (debounce here didn't work)
				}
			}
		}
	}
}