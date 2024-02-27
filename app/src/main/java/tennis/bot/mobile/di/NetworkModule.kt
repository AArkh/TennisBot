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
import tennis.bot.mobile.feed.insertscore.InsertScoreApi
import tennis.bot.mobile.feed.searchopponent.OpponentsApi
import tennis.bot.mobile.profile.account.EnumsApi
import tennis.bot.mobile.profile.account.UserProfileApi
import tennis.bot.mobile.onboarding.location.LocationApi
import tennis.bot.mobile.onboarding.phone.SmsApi
import tennis.bot.mobile.onboarding.photopick.PhotoPickApi
import tennis.bot.mobile.onboarding.survey.RegisterAndLoginApi
import tennis.bot.mobile.onboarding.survey.NewPlayerApi
import tennis.bot.mobile.profile.editgamedata.EditGameDataApi
import tennis.bot.mobile.profile.editprofile.EditProfileApi
import tennis.bot.mobile.profile.matches.MatchesApi
import tennis.bot.mobile.utils.LoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    companion object {
        const val WITH_AUTHENTICATION = "WITH_AUTHENTICATION"
        const val SMS_CODES = "BALANCES_ALLOWANCES"
        const val NEW_REGISTRATION = "NEW_REGISTRATION"
        const val NEW_PLAYER = "NEW_PLAYER"
        const val USER_PROFILE = "USER_PROFILE"
        const val PROFILE_PICTURE = "PROFILE_PICTURE"
        const val SCORES = "SCORES"
        const val EDIT_PROFILE = "EDIT_PROFILE"
        const val EDIT_GAMEDATA = "EDIT_GAMEDATA"
        const val SEARCH_OPPONENTS = "SEARCH_OPPONENTS"
        const val INSERT_SCORE = "INSERT_SCORE"
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
    @Named(WITH_AUTHENTICATION)
    fun provideNewRegistrationOkHttpClient(
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
    @Named(NEW_REGISTRATION)
    @Singleton
    fun provideAccountInfoRetrofit(
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
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(USER_PROFILE)
    @Singleton
    fun provideUserProfileRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(PROFILE_PICTURE)
    @Singleton
    fun provideProfilePictureRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(SCORES)
    @Singleton
    fun provideScoresRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(EDIT_PROFILE)
    @Singleton
    fun provideEditProfileRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(EDIT_GAMEDATA)
    @Singleton
    fun provideEditGameDataRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(SEARCH_OPPONENTS)
    @Singleton
    fun provideSearchOpponentsRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
        converterFactory: Converter.Factory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://bugz.su:8443/core/") //todo вынести debug && prod url в gradle build config
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Named(INSERT_SCORE)
    @Singleton
    fun provideInsertScoreRetrofit(
        @Named(WITH_AUTHENTICATION) okHttpClient: OkHttpClient,
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
    ): RegisterAndLoginApi = retrofit.create(RegisterAndLoginApi::class.java)

    @Provides
    @Singleton
    fun provideNewPlayerApiClient(
        @Named(NEW_PLAYER) retrofit: Retrofit
    ): NewPlayerApi = retrofit.create(NewPlayerApi::class.java)

    @Provides
    @Singleton
    fun provideUserProfileApiClient(
        @Named(USER_PROFILE) retrofit: Retrofit
    ): UserProfileApi = retrofit.create(UserProfileApi::class.java)

    @Provides
    @Singleton
    fun providePhotoPickApiClient(
        @Named(PROFILE_PICTURE) retrofit: Retrofit
    ): PhotoPickApi = retrofit.create(PhotoPickApi::class.java)

    @Provides
    @Singleton
    fun provideEnumsApiClient(
        @Named(NEW_REGISTRATION) retrofit: Retrofit
    ): EnumsApi = retrofit.create(EnumsApi::class.java)

    @Provides
    @Singleton
    fun provideMatchesApiClient(
        @Named(SCORES) retrofit: Retrofit
    ): MatchesApi = retrofit.create(MatchesApi::class.java)

    @Provides
    @Singleton
    fun provideEditProfileApiClient(
        @Named(EDIT_PROFILE) retrofit: Retrofit
    ): EditProfileApi = retrofit.create(EditProfileApi::class.java)

    @Provides
    @Singleton
    fun provideEditGameDataApiClient(
        @Named(EDIT_GAMEDATA) retrofit: Retrofit
    ): EditGameDataApi = retrofit.create(EditGameDataApi::class.java)

    @Provides
    @Singleton
    fun provideSearchOpponentsApiClient(
        @Named(SEARCH_OPPONENTS) retrofit: Retrofit
    ): OpponentsApi = retrofit.create(OpponentsApi::class.java)

    @Provides
    @Singleton
    fun provideInsertScoreApiClient(
        @Named(INSERT_SCORE) retrofit: Retrofit
    ): InsertScoreApi = retrofit.create(InsertScoreApi::class.java)
}

