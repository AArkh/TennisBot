package tennis.bot.mobile.onboarding

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import tennis.bot.mobile.R
import tennis.bot.mobile.databinding.FragmentLoginProposalBinding

class LoginProposalFragment : Fragment() {

    private lateinit var binding: FragmentLoginProposalBinding
    private val adapter = LoginProposalViewPagerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginProposalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.descriptionViewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.descriptionViewPager) { _, _ -> }.attach()

        binding.buttonLogin.setOnClickListener { }
        binding.buttonStart.setOnClickListener { }
        binding.buttonWithoutRegistration.setOnClickListener { }

        adapter.setListAndNotify(listOf(
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
    }
}