package cm.seeds.rdtsmartreader.ui.main.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import  android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.get
import cm.seeds.rdtsmartreader.R
import cm.seeds.rdtsmartreader.helper.*

class SettingsFragment : PreferenceFragmentCompat() {

    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if(key.equals(getString(R.string.localisation_preference_key),true)){
            val value = sharedPreferences.getBoolean(key,false)
            if(value){
                prepareAndGetLocalisation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION){

            if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                //getLocalisation()
            }else{
                showToast(requireContext(),"Nous avons besoin de ces deux permissions pour continuer")
                uncheckedLocationPreference()
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun uncheckedLocationPreference() {
        val switchPreferenceCompat = preferenceScreen.get<SwitchPreferenceCompat>(getString(R.string.localisation_preference_key))
        switchPreferenceCompat?.isChecked = false
    }

    private fun prepareAndGetLocalisation() {
        when {

            (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) -> {
                //getLocalisation()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION) -> {
                getDialogForPermissionDetails(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION,true
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

            ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                getDialogForPermissionDetails(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION,true
                ) { dialog, which ->

                    when(which){

                        DialogInterface.BUTTON_POSITIVE ->{
                            dialog.dismiss()
                            requestPermissions()
                        }

                        DialogInterface.BUTTON_NEGATIVE ->{
                            dialog.dismiss()
                            uncheckedLocationPreference()
                        }
                    }

                }
            }

            else -> {
                requestPermissions()
            }
        }
    }

    private fun requestPermissions(){
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_CODE_LOCATION_PERMISSION)
    }

    @SuppressLint("MissingPermission")
    private fun getLocalisation(location: Location) {
        /*val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if( if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                locationManager.isLocationEnabled
            } else {
                true
            }
        ){
            val switchPreferenceCompat = preferenceScreen.get<SwitchPreferenceCompat>(getString(R.string.localisation_preference_key))
            if(location!=null){
                switchPreferenceCompat?.summaryOn =  "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                listenPositionChange(locationManager)
            }else{
                switchPreferenceCompat?.summaryOn = "Recherche de la position en cours..."
                //uncheckedLocationPreference()
                listenPositionChange(locationManager)
            }
        }*/
    }

    @SuppressLint("MissingPermission")
    private fun listenPositionChange(locationManager: LocationManager) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            MIN_TIME_LOCATION_UPDATE, MIN_DISATNCE_LOCATION_UPDATE,object : LocationListener{
                override fun onLocationChanged(location: Location) {
                    getLocalisation(location)
                }
            })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onStart() {
        super.onStart()
        registerPreferenceListener()
    }

    override fun onStop() {
        super.onStop()
        unregisteredPreferenceListener()
    }

    private fun unregisteredPreferenceListener() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceListener)
    }

    private fun registerPreferenceListener() {
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }


}