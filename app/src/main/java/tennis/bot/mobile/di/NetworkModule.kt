package tennis.bot.mobile.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import tennis.bot.mobile.core.AuthInterceptor
import tennis.bot.mobile.onboarding.location.LocationApi
import tennis.bot.mobile.onboarding.phone.SmsApi
import tennis.bot.mobile.onboarding.survey.AccountInfoApi
import tennis.bot.mobile.utils.LoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val SMS_CODES = "BALANCES_ALLOWANCES"
        const val NEW_REGISTRATION = "NEW_REGISTRATION"
        const val NEW_PLAYER = "NEW_PLAYER"
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: LoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named(NEW_PLAYER)
    fun provideNewRegisrationOkHttpClient(
        loggingInterceptor: LoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true //need to ignore keys, which are not in response class
        isLenient = true //ignores RFC-4627 specification by default to parse parse quite freely-formatted data
        explicitNulls = false //enables nullable fields for responses
    }

    @Provides
    @Singleton
    fun provideJsonConverterFactory(json: Json): Converter.Factory {
        return json.asConverterFactory("application/json".toMediaType())
    }

    @Provides
    @Named(SMS_CODES)
    @Singleton
    fun provideSmsCodesRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(NEW_PLAYER)
    @Singleton
    fun provideNewPlayerRetrofit(
        @Named(NEW_PLAYER) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideBalancesAllowancesApiClient(
        @Named(SMS_CODES) retrofit: Retrofit
    ): SmsApi = retrofit.create(SmsApi::class.java)

    @Provides
    @Singleton
    fun provideLocationsApiClient(
        @Named(SMS_CODES) retrofit: Retrofit
    ): LocationApi = retrofit.create(LocationApi::class.java)

    @Provides
    @Singleton
    fun provideAccountInfoApiClient(
        @Named(NEW_REGISTRATION) retrofit: Retrofit
    ): AccountInfoApi = retrofit.create(AccountInfoApi::class.java)
}

