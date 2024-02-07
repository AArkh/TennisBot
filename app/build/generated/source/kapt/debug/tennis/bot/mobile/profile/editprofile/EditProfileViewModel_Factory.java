package tennis.bot.mobile.profile.editprofile;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import tennis.bot.mobile.onboarding.location.LocationDataMapper;
import tennis.bot.mobile.onboarding.location.LocationRepository;
import tennis.bot.mobile.onboarding.survey.OnboardingRepository;
import tennis.bot.mobile.profile.account.UserProfileAndEnumsRepository;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class EditProfileViewModel_Factory implements Factory<EditProfileViewModel> {
  private final Provider<UserProfileAndEnumsRepository> userProfileRepoProvider;

  private final Provider<LocationRepository> locationRepoProvider;

  private final Provider<LocationDataMapper> locationDataMapperProvider;

  private final Provider<EditProfileRepository> editProfileRepositoryProvider;

  private final Provider<OnboardingRepository> onboardingRepositoryProvider;

  private final Provider<Context> contextProvider;

  public EditProfileViewModel_Factory(
      Provider<UserProfileAndEnumsRepository> userProfileRepoProvider,
      Provider<LocationRepository> locationRepoProvider,
      Provider<LocationDataMapper> locationDataMapperProvider,
      Provider<EditProfileRepository> editProfileRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider,
      Provider<Context> contextProvider) {
    this.userProfileRepoProvider = userProfileRepoProvider;
    this.locationRepoProvider = locationRepoProvider;
    this.locationDataMapperProvider = locationDataMapperProvider;
    this.editProfileRepositoryProvider = editProfileRepositoryProvider;
    this.onboardingRepositoryProvider = onboardingRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public EditProfileViewModel get() {
    return newInstance(userProfileRepoProvider.get(), locationRepoProvider.get(), locationDataMapperProvider.get(), editProfileRepositoryProvider.get(), onboardingRepositoryProvider.get(), contextProvider.get());
  }

  public static EditProfileViewModel_Factory create(
      Provider<UserProfileAndEnumsRepository> userProfileRepoProvider,
      Provider<LocationRepository> locationRepoProvider,
      Provider<LocationDataMapper> locationDataMapperProvider,
      Provider<EditProfileRepository> editProfileRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider,
      Provider<Context> contextProvider) {
    return new EditProfileViewModel_Factory(userProfileRepoProvider, locationRepoProvider, locationDataMapperProvider, editProfileRepositoryProvider, onboardingRepositoryProvider, contextProvider);
  }

  public static EditProfileViewModel newInstance(UserProfileAndEnumsRepository userProfileRepo,
      LocationRepository locationRepo, LocationDataMapper locationDataMapper,
      EditProfileRepository editProfileRepository, OnboardingRepository onboardingRepository,
      Context context) {
    return new EditProfileViewModel(userProfileRepo, locationRepo, locationDataMapper, editProfileRepository, onboardingRepository, context);
  }
}
