package tennis.bot.mobile.profile.editprofile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class EditProfileAdapter_Factory implements Factory<EditProfileAdapter> {
  @Override
  public EditProfileAdapter get() {
    return newInstance();
  }

  public static EditProfileAdapter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EditProfileAdapter newInstance() {
    return new EditProfileAdapter();
  }

  private static final class InstanceHolder {
    private static final EditProfileAdapter_Factory INSTANCE = new EditProfileAdapter_Factory();
  }
}
