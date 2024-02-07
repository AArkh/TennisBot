package tennis.bot.mobile.profile.editprofile.namesurname;

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
public final class EditNameSurnameViewModel_Factory implements Factory<EditNameSurnameViewModel> {
  @Override
  public EditNameSurnameViewModel get() {
    return newInstance();
  }

  public static EditNameSurnameViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static EditNameSurnameViewModel newInstance() {
    return new EditNameSurnameViewModel();
  }

  private static final class InstanceHolder {
    private static final EditNameSurnameViewModel_Factory INSTANCE = new EditNameSurnameViewModel_Factory();
  }
}
