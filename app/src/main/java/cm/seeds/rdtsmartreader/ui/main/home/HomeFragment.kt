package cm.seeds.rdtsmartreader.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.FragmentHomeBinding
import cm.seeds.rdtsmartreader.helper.ServerListener
import cm.seeds.rdtsmartreader.helper.navOptions
import cm.seeds.rdtsmartreader.service.Server
import cm.seeds.rdtsmartreader.ui.MainViewModel
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var dataBinding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.explode)
        sharedElementReturnTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.explode)

        homeViewModel = ViewModelProvider(this, ViewModelFactory(requireActivity().application)).get(
                HomeViewModel::class.java
            )

        mainViewModel = ViewModelProvider(requireActivity(), ViewModelFactory(requireActivity().application)).get(
                MainViewModel::class.java
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addActionsOnViews()

        attachObservers()
    }

    private fun attachObservers() {

        mainViewModel.serverState.observe(viewLifecycleOwner,{
            if(it!=null){
                when(it.state){
                    ServerListener.State.CONNECTED -> {
                        dataBinding.stateConnexionDispositif.text = "Connecté au dispositif"
                    }
                    ServerListener.State.LAUNCHED -> {
                        dataBinding.stateConnexionDispositif.text = "Serveur lancé"
                    }
                    ServerListener.State.STOPPED -> {
                        dataBinding.stateConnexionDispositif.text = "Non connecté au dispositif"
                    }
                }
            }else{
                dataBinding.stateConnexionDispositif.text = "Non connecté au dispositif"
            }
        })

        homeViewModel.allUsers.observe(viewLifecycleOwner, {


            dataBinding.textviewNombreTest.text = "${it.size} \n ${getString(R.string.testes)}"

            val numberPositif =
                it.count { user -> user.test?.conclusion.equals(getString(R.string.positif), true) }

            val numberNegatif =
                it.count { user -> user.test?.conclusion.equals(getString(R.string.negatif), true) }

            dataBinding.textviewNombreTestNegatifs.text =
                "$numberNegatif \n ${getString(R.string.negatifs)}"

            dataBinding.textviewNombreTestPositifs.text =
                "$numberPositif \n ${getString(R.string.positifs)}"


            val numberNotSync = it.count { item -> !item.synchronised }
            dataBinding.textNumbersItemNotSync.text = numberNotSync.toString()

            if (numberNotSync <= 0) {
                dataBinding.layoutNotSyncInformations.visibility = GONE
            } else {
                dataBinding.layoutNotSyncInformations.visibility = VISIBLE
            }

        })

    }

    private fun addActionsOnViews() {

        dataBinding.buttonConnectToServer.setOnClickListener {
            requireContext().startService(Intent(requireContext(), Server::class.java).apply {
                action = Server.TOGGLE_CONNECTION_DECONNEXION
            })
        }

        dataBinding.layoutAllTest.setOnClickListener {
            val navExtras = FragmentNavigatorExtras(
                dataBinding.layoutAllTest to dataBinding.layoutAllTest.transitionName
            )

            findNavController().navigate(R.id.informationsFragment, Bundle().apply {
                putInt(
                    InformationsFragment.TYPE_DATA_TO_SHOW,
                    InformationsFragment.TYPE_OF_DATA_ALL_TEST
                )
            }, null, navExtras)
        }

        dataBinding.layoutPostiveTests.setOnClickListener {
            val navExtras = FragmentNavigatorExtras(
                dataBinding.layoutPostiveTests to dataBinding.layoutPostiveTests.transitionName
            )
            findNavController().navigate(R.id.informationsFragment, Bundle().apply {
                putInt(
                    InformationsFragment.TYPE_DATA_TO_SHOW,
                    InformationsFragment.TYPE_OF_DATA_POSITIVE_TEST
                )
            }, null, navExtras)
        }

        dataBinding.layoutNegativeTest.setOnClickListener {
            val navExtras = FragmentNavigatorExtras(
                dataBinding.layoutNegativeTest to dataBinding.layoutNegativeTest.transitionName
            )
            findNavController().navigate(R.id.informationsFragment, Bundle().apply {
                putInt(
                    InformationsFragment.TYPE_DATA_TO_SHOW,
                    InformationsFragment.TYPE_OF_DATA_NEGATIVE_TEST
                )
            }, null, navExtras)
        }

        dataBinding.buttonMoreAction.setOnClickListener {
            if (dataBinding.buttonCapture.isVisible) {
                rotateFab(dataBinding.buttonMoreAction, false)
                dataBinding.buttonCapture.hide()
                dataBinding.buttonScan.hide()
            } else {
                rotateFab(dataBinding.buttonMoreAction, true)
                dataBinding.buttonCapture.show()
                dataBinding.buttonScan.show()
            }
        }

        dataBinding.buttonCapture.setOnClickListener {
            findNavController().navigate(R.id.cameraFragment, null, navOptions)
        }

        dataBinding.buttonScan.setOnClickListener {
            findNavController().navigate(R.id.scanFragment, null, navOptions)
        }

        dataBinding.buttonSync.setOnClickListener {
            mainViewModel.synchronise()
        }

    }


    private fun rotateFab(fab: FloatingActionButton, shouldRotate: Boolean) {
        fab.animate().setDuration(200).rotation(if (shouldRotate) 135f else 0f)
    }

}