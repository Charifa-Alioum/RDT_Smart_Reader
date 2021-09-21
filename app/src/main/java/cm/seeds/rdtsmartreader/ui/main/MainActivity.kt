package cm.seeds.rdtsmartreader.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.data.Status
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.databinding.LayoutSynchronisationBinding
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Coordonnee
import cm.seeds.rdtsmartreader.service.Server
import cm.seeds.rdtsmartreader.ui.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var synchronisationDialog : Dialog
    private lateinit var synchronisationDataBinding : LayoutSynchronisationBinding

    private lateinit var mainViewModel : MainViewModel
    private var locationManager : LocationManager? = null
    private var server : Server? = null
    private var connectionLoadingDialog : Dialog? = null
    private val serverListener = object : ServerListener {
        override fun onServerStateChange(newState: ServerListener.State) {
            runOnUiThread {
                mainViewModel.serverState.value = mainViewModel.serverState.value?.apply {
                    state = newState
                }
                when(newState){
                    ServerListener.State.CONNECTED -> {
                        connectionLoadingDialog?.dismiss()
                        showToast(this@MainActivity, "connecté au dispositif")
                    }
                    ServerListener.State.LAUNCHED -> {
                        connectionLoadingDialog?.dismiss()
                        showToast(this@MainActivity, "Serveur lancé")
                    }
                    ServerListener.State.STOPPED -> {
                        connectionLoadingDialog?.dismiss()
                        showToast(this@MainActivity,"Dispositif arrété, ou impossible de se concnecter")
                    }
                    ServerListener.State.CONNECTING -> {
                        connectionLoadingDialog?.show()
                    }
                }
            }
        }
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as Server.LocalBinder
            server = binder.getService()
            server?.setListener(serverListener)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            server = null
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, Server::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        unbindService(connection)
        super.onStop()
    }

    private val locationListener = LocationListener { location ->
        val latitude = location.latitude
        val longitude = location.longitude
        saveLocation(
                this@MainActivity,
                Coordonnee(latitude = latitude.toFloat(), longitude = longitude.toFloat())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLoadingDialog()

        mainViewModel = ViewModelProvider(this, ViewModelFactory(application)).get(MainViewModel::class.java)

        setContentView(R.layout.activity_main)

        setupNavController()

        attachObserevers()

        setupSynchronisationDialog()

        //prepareAndGetLocalisation()
    }

    private fun initLoadingDialog() {
        connectionLoadingDialog = getLoadingDialog(this)
    }

    private fun setupSynchronisationDialog() {
        synchronisationDialog = Dialog(this)
        synchronisationDataBinding = LayoutSynchronisationBinding.inflate(LayoutInflater.from(synchronisationDialog.context))
        synchronisationDialog.setContentView(synchronisationDataBinding.root)
        synchronisationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        synchronisationDialog.setCancelable(false)
    }

    private fun attachObserevers() {
        mainViewModel.synchronisationStatusLiveData.observe(this,{

            when(it.status){

                Status.LOADING ->{
                    if(!synchronisationDialog.isShowing){
                        synchronisationDialog.show()
                    }

                    if(it.data!=null){
                        val progress = it.data.userSynched.toFloat() / it.data.userToSynch.toFloat()
                        synchronisationDataBinding.progressHorizontal.progress = (progress * 100).toInt()
                        synchronisationDataBinding.texviewNumberSynched.text = it.data.userSynched.toString()
                        synchronisationDataBinding.texviewNumberToSynch.text = it.data.userToSynch.toString()
                    }
                }

                Status.ERROR ->{

                    synchronisationDialog.dismiss()
                    showMessage(this@MainActivity,"ERROR",it.message)
                }

                Status.SUCCESS -> {
                    synchronisationDialog.dismiss()
                    showToast(this@MainActivity, "Synchronisation Terminée")
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        prepareAndGetLocalisation()
    }

    override fun onPause() {
        super.onPause()
        stopLocationListener()
    }

    private fun prepareAndGetLocalisation() {
        when {

            (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) -> {
                listenLocation()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                getDialogForPermissionDetails(this, Manifest.permission.ACCESS_FINE_LOCATION,true
                ) { dialog, which ->

                    when(which){

                        DialogInterface.BUTTON_POSITIVE ->{
                            dialog.dismiss()
                            requestPermissions()
                        }

                        DialogInterface.BUTTON_NEGATIVE ->{
                            dialog.dismiss()
                        }
                    }

                }
            }

            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                getDialogForPermissionDetails(this, Manifest.permission.ACCESS_COARSE_LOCATION,true
                ) { dialog, which ->

                    when(which){

                        DialogInterface.BUTTON_POSITIVE ->{
                            dialog.dismiss()
                            requestPermissions()
                        }

                        DialogInterface.BUTTON_NEGATIVE ->{
                            dialog.dismiss()
                        }
                    }

                }
            }

            else -> {
                requestPermissions()
            }
        }
    }

    fun stopLocationListener(){
        if(locationManager!=null){
            locationManager?.removeUpdates(locationListener)
        }
    }

    @SuppressLint("MissingPermission")
    private fun listenLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    locationManager?.isLocationEnabled == true
                } else {
                    true
                }
        ){
            locationManager?.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    MIN_TIME_LOCATION_UPDATE, MIN_DISATNCE_LOCATION_UPDATE,
                    locationListener)
            locationManager?.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)?.let {
                saveLocation(this, Coordonnee(latitude = it.latitude.toFloat(), longitude = it.longitude.toFloat()))
            }
        }
    }

    private fun requestPermissions(){
       ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_LOCATION_PERMISSION)
    }

    private fun setupNavController() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)

        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when(destination.id){

                R.id.homeFragment -> {
                    navView.visibility = VISIBLE
                }

                R.id.settingsFragment ->{
                    navView.visibility = VISIBLE
                }

                R.id.informationsFragment ->{
                    navView.visibility = VISIBLE
                }

                R.id.cameraFragment ->{
                    //navView.visibility = GONE
                }

                R.id.scanFragment ->{
                    navView.visibility = GONE
                }

            }

        }
    }
}