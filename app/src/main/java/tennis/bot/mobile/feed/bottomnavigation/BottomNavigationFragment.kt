package tennis.bot.mobile.feed.bottomnavigation

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentBottomNavigationBinding
import tennis.bot.mobile.feed.activityfeed.FeedFragment
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.feed.game.GameTabFragment
import tennis.bot.mobile.feed.requestcreation.RequestCreationFragment
import tennis.bot.mobile.profile.account.AccountPageFragment
import tennis.bot.mobile.utils.basicdialog.BasicDialogViewModel
import tennis.bot.mobile.utils.dpToPx
import tennis.bot.mobile.utils.goToAnotherSectionFragment
import tennis.bot.mobile.utils.view.AvatarImage

@AndroidEntryPoint
class BottomNavigationFragment : AuthorizedCoreFragment<FragmentBottomNavigationBinding>(), NavigationBarView.OnItemSelectedListener {

	override val bindingInflation: Inflation<FragmentBottomNavigationBinding> = FragmentBottomNavigationBinding::inflate
	private val viewModel: BottomNavigationViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (savedInstanceState == null) {
			replaceFragment(FeedFragment())
		}
		viewModel.onCheckingAppVersion(getAppVersionName()) { isBroken ->
			val dialog = VersionControlDialog()
			val args = Bundle()
			args.putString(BasicDialogViewModel.SELECT_DIALOG_TITLE, "Вышло новое обновление!")
			args.putString(BasicDialogViewModel.SELECT_DIALOG_TEXT, "Ваша версия устарела. Чтобы использовать Tennis Bot дальше - обновите приложение")
			args.putString(BasicDialogViewModel.SELECT_DIALOG_TOP_BUTTON_TEXT, "Обновить")
			args.putString(BasicDialogViewModel.SELECT_DIALOG_BOTTOM_BUTTON_TEXT, "Позже")
			if (isBroken) {
				args.putInt(BasicDialogViewModel.SELECT_DIALOG_ANIMATION, R.raw.close)
				args.putBoolean(BasicDialogViewModel.SELECT_DIALOG_IS_ONE_BUTTON, true)
				args.putBoolean(BasicDialogViewModel.SELECT_DIALOG_IS_CANCELABLE, false)
				args.putBoolean(BasicDialogViewModel.SELECT_DIALOG_IS_CANCELABLE_ON_TOUCH_OUTSIDE, false)
			}

			dialog.arguments = args
			dialog.show(childFragmentManager, dialog.tag)
		}

		binding.bottomNavBar.setOnItemSelectedListener(this)

		binding.playerPhoto.setOnClickListener {
			parentFragmentManager.goToAnotherSectionFragment(AccountPageFragment())
		}

		binding.addScoreButton.setOnClickListener {
			showAddScorePopup(it, viewModel.addScoreOptions)
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: BottomNavigationUiState ->
			binding.title.text = uiState.title
			binding.bottomNavBar.selectedItemId = uiState.currentItemId
			binding.playerPhoto.setImage(AvatarImage(uiState.playerPicture))
			binding.playerPhoto.drawableSize = requireContext().dpToPx(32)
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.feed_item -> {
				replaceFragment(FeedFragment())
				viewModel.onItemChosen(BottomNavigationViewModel.FRAGMENT_FEED)
				true
			}
			R.id.game_item -> {
				replaceFragment(GameTabFragment())
				viewModel.onItemChosen(BottomNavigationViewModel.FRAGMENT_GAME)
				true
			}
//			R.id.chat_item -> {
//				viewModel.replaceTitle(BottomNavigationViewModel.FRAGMENT_EMPTY)
//				replaceFragment(InDevelopmentBottomNavFragment())
//				true
//			}
//			R.id.tournament_item -> {
//				viewModel.replaceTitle(BottomNavigationViewModel.FRAGMENT_EMPTY)
//				replaceFragment(InDevelopmentBottomNavFragment())
//				true
//			}
			else -> false
		}
	}

	private fun replaceFragment(fragment: Fragment) {
			parentFragmentManager.beginTransaction().replace(R.id.container_view, fragment).commit()
	}

	private fun showAddScorePopup(view: View, items: List<String>) {
		val menu = PopupMenu(requireContext(), view, Gravity.END, 0, R.style.PopupMenuStyle)
		for (item in items) {
			menu.menu.add(item)
		}
		menu.setOnMenuItemClickListener { menuItem ->
			when(items.indexOf(menuItem.title.toString())) {
				FeedFragment.ADD_SCORE_INDEX -> {
					parentFragmentManager.goToAnotherSectionFragment(AddScoreFragment())
				}
				FeedFragment.CREATE_GAME_ITEM -> {
					parentFragmentManager.goToAnotherSectionFragment(RequestCreationFragment())
				}
				else -> {}
			}
			true
		}
		menu.show()
	}
}