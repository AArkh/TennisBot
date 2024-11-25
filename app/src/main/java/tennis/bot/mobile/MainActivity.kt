package tennis.bot.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.core.authentication.AuthTokenRepository
import tennis.bot.mobile.databinding.ActivityMainBinding
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationFragment
import tennis.bot.mobile.feed.notifications.NotificationsRepository
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
    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportFragmentManager.fragments.isEmpty()) {
            if (authTokenRepository.getRefreshToken() != null) {
                supportFragmentManager.beginTransaction()
                    .add(binding.fragmentContainerView.id, BottomNavigationFragment())
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
                } catch (nullPointerException: NullPointerException) {
                    locationRepository.getLocations()
                }
            }
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    userProfileAndEnumsRepository.precacheEnums()
                } catch (iOException: IOException) {
                    Log.d("1234567", "Network error: ${iOException.message}")
                } catch (nullPointerException: NullPointerException) {
                    userProfileAndEnumsRepository.getEnums()
                }
            }
        }
        lifecycleScope.launch {
            authTokenRepository.unAuthEventsFlow.debounce(500L).collectLatest {
                Log.e("1234567", "activity caught an unauth event, trying to navigate back to login screen")
                val transaction = supportFragmentManager.beginTransaction()
                supportFragmentManager.fragments.forEach {
                    transaction.remove(it)
                }

                transaction.replace(binding.fragmentContainerView.id, LoginProposalFragment()).commit()
            }
        }

        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                lifecycleScope.launch {
                    notificationsRepository.precacheToken(token)
                }
            } else {
                FirebaseCrashlytics.getInstance().log("Failed to record FCM token")

            }
        }

        fun navigateToSpecificFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, fragment)
                .addToBackStack(null)
                .commit()
        }

        fun handlePushNotificationIntent(intent: Intent?) {
            val type = intent?.getStringExtra("notification_type")

            when (type) {
                "NEW_MESSAGE" -> {
//                    navigateToSpecificFragment()
                }
                "PROMOTION" -> {
//                    navigateToSpecificFragment()
                }
                else -> {
                    // Handle default case
                }
            }
        }


    }
}