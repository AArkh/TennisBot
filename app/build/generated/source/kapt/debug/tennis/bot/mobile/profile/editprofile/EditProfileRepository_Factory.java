package tennis.bot.mobile.profile.editprofile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class EditProfileRepository_Factory implements Factory<EditProfileRepository> {
  private final Provider<EditProfileApi> apiProvider;

  public EditProfileRepository_Factory(Provider<EditProfileApi> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public EditProfileRepository get() {
    return newInstance(apiProvider.get());
  }

  public static EditProfileRepository_Factory create(Provider<EditProfileApi> apiProvider) {
    return new EditProfileRepository_Factory(apiProvider);
  }

  public static EditProfileRepository newInstance(EditProfileApi api) {
    return new EditProfileRepository(api);
  }
}
