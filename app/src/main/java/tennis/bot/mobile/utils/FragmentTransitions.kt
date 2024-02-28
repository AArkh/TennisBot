package tennis.bot.mobile.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import tennis.bot.mobile.R

fun FragmentManager.goToAnotherSectionFragment(fragment: Fragment) {
	beginTransaction()
		.setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
		.replace(R.id.fragment_container_view, fragment)
		.addToBackStack(fragment::class.java.name)
		.commit()
}

fun FragmentManager.traverseToAnotherFragment(fragment: Fragment) {
	beginTransaction()
		.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
		.replace(R.id.fragment_container_view, fragment)
		.addToBackStack(fragment::class.java.name)
		.commit()
}

fun FragmentManager.destroyBackstack() {
	for (i in 0 until backStackEntryCount) {
		popBackStack()
	}
}