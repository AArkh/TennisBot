package tennis.bot.mobile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.core.AuthTokenRepository
import tennis.bot.mobile.databinding.ActivityMainBinding
import tennis.bot.mobile.feed.FeedBottomNavigationFragment
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository
import tennis.bot.mobile.onboarding.initial.LoginProposalFragment
import tennis.bot.mobile.onboarding.location.LocationRepository
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    @Inject
    lateinit var locationRepository: LocationRepository
    @Inject
    lateinit var authTokenRepository: AuthTokenRepository
    @Inject
    lateinit var userProfileAndEnumsRepository: UserProfileAndEnumsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (supportFragmentManager.fragments.isEmpty()) {
            if (authTokenRepository.getRefreshToken() != null) { // blows up if there's no internet + when refresh has problems. todo think on how to act when there's no internet
                supportFragmentManager.beginTransaction()
                    .add(binding.fragmentContainerView.id, LoginProposalFragment())
                    .replace(R.id.fragment_container_view, FeedBottomNavigationFragment())
                    .addToBackStack(FeedBottomNavigationFragment::class.java.name)
                    .commit()
            } else {
                supportFragmentManager.beginTransaction()
                    .add(binding.fragmentContainerView.id, LoginProposalFragment())
                    .commit()
            }
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    locationRepository.precacheLocations()
                } catch (iOException: IOException) {
                    Log.d("1234567", "Network error: ${iOException.message}")
                }
            }
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    userProfileAndEnumsRepository.precacheEnums()
                } catch (iOException: IOException) {
                    Log.d("1234567", "Network error: ${iOException.message}")
                }
            }
        }
        lifecycleScope.launch {
            authTokenRepository.unAuthEventsFlow.collectLatest {
                Log.e("1234567", "activity caught an unauth event, trying to navigate back to login screen")
                val transaction = supportFragmentManager.beginTransaction()
                supportFragmentManager.fragments.forEach {
                    transaction.remove(it)
                }
                transaction.add(binding.fragmentContainerView.id, LoginProposalFragment()).commit()
            }
        }
    }
}