package tennis.bot.mobile.profile.editprofile;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class EditProfileFragment_MembersInjector implements MembersInjector<EditProfileFragment> {
  private final Provider<EditProfileAdapter> adapterProvider;

  public EditProfileFragment_MembersInjector(Provider<EditProfileAdapter> adapterProvider) {
    this.adapterProvider = adapterProvider;
  }

  public static MembersInjector<EditProfileFragment> create(
      Provider<EditProfileAdapter> adapterProvider) {
    return new EditProfileFragment_MembersInjector(adapterProvider);
  }

  @Override
  public void injectMembers(EditProfileFragment instance) {
    injectAdapter(instance, adapterProvider.get());
  }

  @InjectedFieldSignature("tennis.bot.mobile.profile.editprofile.EditProfileFragment.adapter")
  public static void injectAdapter(EditProfileFragment instance, EditProfileAdapter adapter) {
    instance.adapter = adapter;
  }
}
