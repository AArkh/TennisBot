package tennis.bot.mobile.onboarding.initial

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import tennis.bot.mobile.R
import tennis.bot.mobile.core.Inflation
import tennis.bot.mobile.core.CoreFragment
import tennis.bot.mobile.databinding.FragmentLoginProposalBinding
import tennis.bot.mobile.onboarding.location.LocationFragment
import tennis.bot.mobile.onboarding.phone.PhoneInputFragment
import tennis.bot.mobile.onboarding.photopick.PhotoPickFragment
import tennis.bot.mobile.utils.showToast
import javax.inject.Inject

@SuppressLint("ClickableViewAccessibility")
@AndroidEntryPoint
class LoginProposalFragment : CoreFragment<FragmentLoginProposalBinding>() {

    override val bindingInflation: Inflation<FragmentLoginProposalBinding> = FragmentLoginProposalBinding::inflate
    @Inject lateinit var textAdapter: LoginProposalViewPagerAdapter
    @Inject lateinit var imageAdapter: LoginProposalImageAdapter
    @Inject lateinit var decoration: LoginProposalImageDecoration
    @Inject lateinit var touchListener: DraggingTouchListener

    override var drawUnderStatusBar: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.descriptionViewPager.adapter = textAdapter
        binding.imageList.layoutManager = GridLayoutManager(requireContext(), 4)
        binding.imageList.adapter = imageAdapter
        binding.imageList.setHasFixedSize(true)
        binding.imageList.itemAnimator = null
        binding.imageList.addItemDecoration(decoration)
        binding.imageList.setOnTouchListener(touchListener)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            binding.imageList.overScrollMode = View.OVER_SCROLL_NEVER
        }

        TabLayoutMediator(binding.tabLayout, binding.descriptionViewPager) { _, _ -> }.attach()

        binding.buttonStart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack(this::class.java.name)
                .replace(R.id.fragment_container_view, PhoneInputFragment())
                .commit()
        }
        binding.buttonLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack(this::class.java.name)
                .replace(R.id.fragment_container_view, PhotoPickFragment())
                .commit()
        }
        binding.buttonWithoutRegistration.setOnClickListener {
            requireContext().showToast("To be implemented yet")
        }

        textAdapter.setListAndNotify(listOf(
            TitledText(
                requireContext().getString(R.string.onboarding_text_title),
                requireContext().getString(R.string.onboarding_text)
            ),
            TitledText(
                requireContext().getString(R.string.onboarding_text_title),
                requireContext().getString(R.string.onboarding_text)
            ),
            TitledText(
                requireContext().getString(R.string.onboarding_text_title),
                requireContext().getString(R.string.onboarding_text)
            ),
        ))

        imageAdapter.setListAndNotify(listOf(
            LoginProposalImage(R.drawable.login_image_8),
            LoginProposalImage(R.drawable.login_image_2),
            LoginProposalImage(R.drawable.login_image_9),
            LoginProposalImage(R.drawable.login_image_10),
            LoginProposalImage(R.drawable.login_image_5),
            LoginProposalImage(R.drawable.login_image_1),
            LoginProposalImage(R.drawable.login_image_3),
            LoginProposalImage(R.drawable.login_image_7),
            LoginProposalImage(R.drawable.login_image_blank),
            LoginProposalImage(R.drawable.login_image_0),
            LoginProposalImage(R.drawable.login_image_4),
            LoginProposalImage(R.drawable.login_image_6),
        ))
    }
}