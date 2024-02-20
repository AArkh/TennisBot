package tennis.bot.mobile.core

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Унаследовать все фрагменты(включая диалоговые), которые находятся ВНУТРИ сценария авторизации от этого класса
@HiltViewModel
class AuthKeyViewModel(
    private val refreshKeyRepo: Repo // эту хуйню прокинуть в место, где токен протухает и в его flow прокидывать эти события протухания
) : ViewModel() {

    private var fragment: AuthorizedFragment? = null


    init {
        refreshKeyRepo.protuhKeyEventFlow // это не stateFlow, это просто flow, у которого нет значения по умолчанию. SharedFlow сойдет думаю
    }

    fun setFragment(authorizedFragment: AuthorizedFragment) {
        fragment = authorizedFragment
        authorizedFragment.lifecycleScope.launch(Dispatchers.Main)  {
            authorizedFragment.lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                // тут можно подписаться на flow
                refreshKeyRepo.protuhKeyEventFlow.collectLatest { unit ->
                    authorizedFragment.parentFragmentManager // тут чистим все фрагменты и открываем главный экран.
                    // На главный экран передаем что-то в аргументах, чтобы там показать тост или что-то ещё, извещающее, мол
                    // чувак, сессия протухла, авторизуйся
                }
            }
        }
    }
}