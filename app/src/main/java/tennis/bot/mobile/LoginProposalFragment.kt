package tennis.bot.mobile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import tennis.bot.mobile.databinding.FragmentLoginProposalBinding

class LoginProposalFragment : Fragment() {

    private lateinit var binding: FragmentLoginProposalBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginProposalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonLogin.setOnClickListener { }
        binding.buttonStart.setOnClickListener { }
        binding.buttonWithoutRegistration.setOnClickListener { }
    }
}