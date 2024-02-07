package tennis.bot.mobile.profile.editprofile.location;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import tennis.bot.mobile.onboarding.location.LocationRepository;
import tennis.bot.mobile.onboarding.survey.OnboardingRepository;

@ScopeMetadata
@QualifierMetadata
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
public final class EditLocationViewModel_Factory implements Factory<EditLocationViewModel> {
  private final Provider<LocationRepository> locationRepositoryProvider;

  private final Provider<OnboardingRepository> onboardingRepositoryProvider;

  public EditLocationViewModel_Factory(Provider<LocationRepository> locationRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    this.locationRepositoryProvider = locationRepositoryProvider;
    this.onboardingRepositoryProvider = onboardingRepositoryProvider;
  }

  @Override
  public EditLocationViewModel get() {
    return newInstance(locationRepositoryProvider.get(), onboardingRepositoryProvider.get());
  }

  public static EditLocationViewModel_Factory create(
      Provider<LocationRepository> locationRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    return new EditLocationViewModel_Factory(locationRepositoryProvider, onboardingRepositoryProvider);
  }

  public static EditLocationViewModel newInstance(LocationRepository locationRepository,
      OnboardingRepository onboardingRepository) {
    return new EditLocationViewModel(locationRepository, onboardingRepository);
  }
}
