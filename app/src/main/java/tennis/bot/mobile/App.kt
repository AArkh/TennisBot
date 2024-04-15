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

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
    }

    private fun updateBaseContextLocale(context: Context): Context {
        val preferredLocale = getPreferredLocale(context)

        // Create a configuration object and set the locale
        val config = Configuration(context.resources.configuration)
        config.setLocale(preferredLocale)

        // Return the updated context with the new locale
        return context.createConfigurationContext(config)
    }

    private fun getPreferredLocale(context: Context): Locale {
        val supportedLocales = arrayOf(Locale("en"), Locale("ru")) // Add more supported locales if needed

        val locale = Locale.getDefault()
        if (supportedLocales.contains(locale)) {
            // Use the device's preferred locale if it's supported
            return locale
        }

        // Fallback to the Russian locale if the device's language is not supported
        return Locale("ru")
    }
}