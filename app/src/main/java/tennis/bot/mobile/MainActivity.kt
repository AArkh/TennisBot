package tennis.bot.mobile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tennis.bot.mobile.databinding.ActivityMainBinding
import tennis.bot.mobile.onboarding.initial.LoginProposalFragment
import tennis.bot.mobile.onboarding.location.LocationRepo
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    @Inject
    lateinit var locationRepo: LocationRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (supportFragmentManager.fragments.isEmpty()) {
            supportFragmentManager.beginTransaction()
                .add(binding.fragmentContainerView.id, LoginProposalFragment())
                .commit()
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    locationRepo.precacheLocations()
                } catch (iOException: IOException) {
                    Log.d("1234567", "Network error: ${iOException.message}")
                }
            }
        }
    }
}