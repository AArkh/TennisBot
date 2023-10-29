package tennis.bot.mobile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.databinding.ActivityMainBinding
import tennis.bot.mobile.onboarding.LoginProposalFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

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
    }
}