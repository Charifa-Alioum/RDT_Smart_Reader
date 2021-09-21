package cm.seeds.rdtsmartreader.ui.main.settings

import android.content.Intent
import  android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cm.seeds.rdtsmartreader.databinding.FragmentSettingsBinding
import cm.seeds.rdtsmartreader.ui.form.all_form.AllFormActivity

class UserProfilFragment : Fragment() {

    private lateinit var dataBinding : FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentSettingsBinding.inflate(inflater,container,false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addActionsOnViews()

        startActivity(Intent(requireContext(),AllFormActivity::class.java))
    }

    private fun addActionsOnViews() {

    }

}