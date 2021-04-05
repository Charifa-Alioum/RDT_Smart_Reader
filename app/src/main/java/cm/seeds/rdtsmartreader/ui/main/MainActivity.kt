package cm.seeds.rdtsmartreader.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
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
import cm.seeds.rdtsmartreader.data.ViewModelFactory
import cm.seeds.rdtsmartreader.helper.*
import cm.seeds.rdtsmartreader.modeles.Coordonnee
import cm.seeds.rdtsmartreader.ui.main.informations.InformationsViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var informationsViewModel : InformationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        informationsViewModel = ViewModelProvider(this, ViewModelFactory(application)).get(InformationsViewModel::class.java)

        setContentView(R.layout.activity_main)

        setupNavController()

        prepareAndGetLocalisation()
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

    @SuppressLint("MissingPermission")
    private fun listenLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    locationManager.isLocationEnabled
                } else {
                    true
                }
        ){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_TIME_LOCATION_UPDATE, MIN_DISATNCE_LOCATION_UPDATE
            ) { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                saveLocation(
                    this@MainActivity,
                    Coordonnee(latitude = latitude.toFloat(), longitude = longitude.toFloat())
                )
            }

            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)?.let {
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

                R.id.navigation_home -> {
                    navView.visibility = VISIBLE
                }

                R.id.navigation_settings ->{
                    navView.visibility = VISIBLE
                }

                R.id.navigation_informations ->{
                    navView.visibility = VISIBLE
                }

                R.id.cameraFragment ->{
                    navView.visibility = GONE
                }

                R.id.scanFragment ->{
                    navView.visibility = GONE
                }

            }

        }
    }
}