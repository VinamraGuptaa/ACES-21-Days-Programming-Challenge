package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherService
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var mProgressDialog:Dialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (!isLocationEnabled()) {
            Toast.makeText(this, "The Location services are turned OFF", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        else  {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            ).withListener( object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        requestLocationData()
                    }
                    if (report!!.isAnyPermissionPermanentlyDenied) {
                        Toast.makeText(
                            this@MainActivity,
                            "You have denied location permission. Please allow it is mandatory.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread().check()


        }











    }

        fun isLocationEnabled(): Boolean {
            val location: LocationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return location.isProviderEnabled(LocationManager.GPS_PROVIDER) || location.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
            // This Provides access to Location Service
        }
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog,
                                           _ ->
                dialog.dismiss()
            }.show()
    }

    // START
    /**
     * A function to request the current location. Using the fused location provider client.
     */
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }
    // END
    // START
    /**
     * A location callback object of fused location provider client where we will get the current location details.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val latitude = mLastLocation.latitude
            Log.i("Current Latitude", "$latitude")

            val longitude = mLastLocation.longitude
            Log.i("Current Longitude", "$longitude")
            getAllWeatherDetails(latitude,longitude)
        }
    }
    private fun getAllWeatherDetails(latitude:Double,longitude:Double) = if(Constants.isNetworkAvailable(this)){
        val retrofit:Retrofit=Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        val service:WeatherService=retrofit.create(WeatherService::class.java)
        val listCall:Call<WeatherResponse> = service.getWeather(latitude,longitude,Constants.METRIC_UNIT,Constants.APP_ID)
        showDialogbox()
        listCall.enqueue(object :Callback<WeatherResponse>{
            override fun onFailure(call: Call<WeatherResponse>?, t: Throwable?) {


             hideDialogBox()
             Log.i("Error",t!!.message.toString())
            }

            override fun onResponse(
                call: Call<WeatherResponse>?,
                response: Response<WeatherResponse>?
            ) {
                if(response!!.isSuccessful){
                    hideDialogBox()
                    val weatherList=response.body()
                    setupUI(weatherList)
                }
                else{
                    val rc =response.code()
                    when(rc){
                        400->Log.i("Error 400","Bad Connection")
                        404->Log.i("Error 404","Website not Found")
                        else->{
                            Log.i("Error","Generic Error")
                        }
                    }

                }
            }
        })

    }
    else{
        Toast.makeText(this@MainActivity,"You are not connected to the Internet",Toast.LENGTH_SHORT).show()
    }
    private fun showDialogbox(){
          mProgressDialog=Dialog(this)
        mProgressDialog!!.setContentView(R.layout.customprogressdialog)
        mProgressDialog!!.show()
    }
    private fun hideDialogBox(){
        if(mProgressDialog!=null){
            mProgressDialog!!.dismiss()
        }
    }

    fun setupUI(weatherList:WeatherResponse){
        for(i in weatherList.weather.indices){
           tv_main.text=weatherList.weather[i].main
            tv_main_description.text=weatherList.weather[i].description
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                tv_temp.text=weatherList.main.temp.toString() + getUnit(application.resources.configuration.locales.toString())
            }
            tv_sunrise_time.text=getTime(weatherList.sys.sunrise)
            tv_sunset_time.text=getTime(weatherList.sys.sunset)
            tv_max.text=weatherList.main.temp_max.toString()
            tv_min.text=weatherList.main.temp_min.toString()
            tv_speed.text=weatherList.wind.speed.toString()
            tv_speed_unit.text=Constants.METRIC_UNIT
            tv_name.text=weatherList.name
            tv_country.text=weatherList.sys.country
        }
    }
    fun getUnit(value:String):String?{
        var value="C"
        if("US"==value||"LR"==value||"MM"==value){
            value="F"
        }
        return value
    }

    fun getTime(Timex:Long):String?{
        val date= Date(Timex*1000L)
        val sdf= java.text.SimpleDateFormat("HH:mm:ss",Locale.UK)
        sdf.timeZone=TimeZone.getDefault()
        return sdf.format(date)


    }



}