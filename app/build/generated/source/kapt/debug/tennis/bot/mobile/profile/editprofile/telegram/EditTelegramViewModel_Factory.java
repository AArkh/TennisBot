package tennis.bot.mobile.profile.editprofile.telegram;

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
public final class EditTelegramViewModel_Factory implements Factory<EditTelegramViewModel> {
  @Override
  public EditTelegramViewModel get() {
    return newInstance();
  }

  public static EditTelegramViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EditTelegramViewModel newInstance() {
    return new EditTelegramViewModel();
  }

  private static final class InstanceHolder {
    private static final EditTelegramViewModel_Factory INSTANCE = new EditTelegramViewModel_Factory();
  }
}
