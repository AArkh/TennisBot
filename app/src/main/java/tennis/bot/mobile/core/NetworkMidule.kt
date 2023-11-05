package tennis.bot.mobile.core

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import retrofit2.Retrofit
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://bugz.su:8443/")
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .addConverterFactory(Json.asConverterFactory())
            .build()
    }

    @Provides
    fun providesInstance(retrofit: Retrofit): apiService {
        return retrofit.create(apiService::class.java)
    }
}