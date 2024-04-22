package tennis.bot.mobile

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class App : Application() {

    init {
        ctx = this
    }

    companion object {
        /**
         * For static utils function only. This is the only Context we can store in static field.
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context
    }
}