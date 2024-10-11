package tennis.bot.mobile.feed.bottomnavigation

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.google.android.material.navigation.NavigationBarView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.authentication.AuthorizedCoreFragment
import tennis.bot.mobile.databinding.FragmentBottomNavigationBinding
import tennis.bot.mobile.feed.activityfeed.FeedFragment
import tennis.bot.mobile.feed.addscore.AddScoreFragment
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.FRAGMENT_TYPE_NUMBER
import tennis.bot.mobile.feed.bottomnavigation.BottomNavigationViewModel.Companion.FRAGMENT_TYPE_REQUEST_KEY
import tennis.bot.mobile.feed.game.GameTabFragment
import tennis.bot.mobile.feed.notifications.NotificationsFragment
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
	private var fetchJob: Job? = null

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

		binding.searchButton.setOnClickListener {
			viewModel.onSearchBarActivation(
				binding.searchLayout.root,
				binding.searchLayout.cancelSearch,
				binding.searchLayout.searchBarEt,
				requireActivity())
		}
		binding.notificationsBell.setOnClickListener {
			parentFragmentManager.goToAnotherSectionFragment(NotificationsFragment())
		}

		setFragmentResultListener(FRAGMENT_TYPE_REQUEST_KEY) { _, result ->
			viewModel.onSearchBarType(result.getInt(FRAGMENT_TYPE_NUMBER))
			binding.searchLayout.root.isVisible = false
		}

		binding.bottomNavBar.setOnItemSelectedListener(this)

		binding.playerPhoto.setOnClickListener {
			parentFragmentManager.goToAnotherSectionFragment(AccountPageFragment())
		}

		binding.addScoreButton.setOnClickListener {
			showAddScorePopup(it, viewModel.addScoreOptions)
		}

		fetchJob = CoroutineScope(Dispatchers.Main).launch { // todo ask Andrey if that's a good practice
			while (isActive) {
				viewModel.onCheckingNotifications()
				delay(BottomNavigationViewModel.refreshInterval)
			}
		}

		subscribeToFlowOn(viewModel.uiStateFlow) { uiState: BottomNavigationUiState ->
			binding.title.text = uiState.title
			binding.bottomNavBar.selectedItemId = uiState.currentItemId
			displayNotificationIndicators(
				uiState.allNotifications,
				uiState.feedNotifications,
				uiState.gameNotifications)
			binding.playerPhoto.setImage(AvatarImage(uiState.playerPicture))
			binding.playerPhoto.drawableSize = requireContext().dpToPx(32)
		}
	}

	override fun onNavigationItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.feed_item -> {
				replaceFragment(FeedFragment())
				viewModel.onCheckingNotifications()
				viewModel.onItemChosen(BottomNavigationViewModel.FRAGMENT_FEED)
				true
			}
			R.id.game_item -> {
				replaceFragment(GameTabFragment())
				viewModel.onCheckingNotifications()
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

	private fun displayNotificationIndicators(allNotifications: Int, feedNotifications: Int, gameNotifications: Int) {
		val feedIndicator = binding.bottomNavBar.getOrCreateBadge(R.id.feed_item)
		val gameIndicator = binding.bottomNavBar.getOrCreateBadge(R.id.game_item)

		binding.notificationsCounter.isVisible = allNotifications != 0
		feedIndicator.isVisible = feedNotifications != 0
		gameIndicator.isVisible = gameNotifications != 0

		binding.notificationsCounter.text = allNotifications.toString()
		feedIndicator.number = feedNotifications
		gameIndicator.number = gameNotifications
	}

	override fun onDestroy() {
		super.onDestroy()

		fetchJob?.cancel()
	}
}