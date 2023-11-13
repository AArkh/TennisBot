package tennis.bot.mobile.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tennis.bot.mobile.onboarding.location.Location
import tennis.bot.mobile.onboarding.location.LocationCityConverter
import tennis.bot.mobile.onboarding.location.LocationDao
import tennis.bot.mobile.onboarding.location.LocationDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
            locationCityConverter: LocationCityConverter
        ): LocationDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            "location_database"
        )
            .addTypeConverter(locationCityConverter)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLocationCityConverter(): LocationCityConverter{
        return LocationCityConverter()
    }



    @Provides
    fun provideLocationDao(
        locationDatabase: LocationDatabase
    ): LocationDao {
        return locationDatabase.locationDao()
    }

}