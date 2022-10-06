package com.andromite.fiskertest

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.net.wifi.WifiManager.LocalOnlyHotspotReservation
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.andromite.fiskertest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var wifiManager: WifiManager? = null
    var currentConfig: WifiConfiguration? = null
    var hotspotReservation: LocalOnlyHotspotReservation? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.openHS.setOnClickListener {
            turnOnHotspot()
        }

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun turnOnHotspot() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_WIFI_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        binding.progressbar.visibility = View.VISIBLE
        binding.openHS.visibility = View.GONE

        wifiManager!!.startLocalOnlyHotspot(object : LocalOnlyHotspotCallback() {
            override fun onStarted(reservation: LocalOnlyHotspotReservation) {
                super.onStarted(reservation)
                hotspotReservation = reservation
                currentConfig = hotspotReservation!!.wifiConfiguration
                hotspotDetaisDialog()
            }

            override fun onStopped() {
                super.onStopped()
                binding.message.text = ""
                binding.message.text = getString(R.string.stopped_message)
                Toast.makeText(this@MainActivity,"Local Hotspot Stopped", Toast.LENGTH_SHORT).show()
                binding.openHS.visibility = View.VISIBLE
            }

            override fun onFailed(reason: Int) {
                super.onFailed(reason)
                binding.message.text = ""
                binding.message.text = getString(R.string.failed_message)
                Toast.makeText(this@MainActivity,"Local Hotspot failed to start", Toast.LENGTH_SHORT).show()
                binding.openHS.visibility = View.VISIBLE
            }
        }, Handler())
    }

    private fun hotspotDetaisDialog() {

        val message = "SSID: ${currentConfig!!.SSID} \nPassword: ${currentConfig!!.preSharedKey}"
        binding.progressbar.visibility = View.GONE
        binding.openHS.visibility = View.GONE
        binding.message.text = message


        val alertDialog: AlertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Hotspot SSID and Password")
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
        alertDialog.show()

        Log.v("asdfasdf",message)
    }


}