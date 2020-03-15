package asia.groovelab.blesample

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import asia.groovelab.blesample.databinding.ActivityMainBinding
import asia.groovelab.blesample.extension.isDisabled
import asia.groovelab.blesample.extension.toast
import asia.groovelab.blesample.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ACCESS_FINE_LOCATION = 1000
        private const val REQUEST_ENABLE_BT = 1001
    }

    private val viewModel: MainViewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ViewModelProvider(this, factory).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  setup binding
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        //  setup view model
        viewModel.action.observe(this, Observer { action ->
            when (action) {
                MainViewModel.Action.Central -> Intent(this, CentralActivity::class.java)
                MainViewModel.Action.Peripheral -> Intent(this, PeripheralActivity::class.java)
                else -> null
            }?.let {
                startActivity(it)
            }
        })

        if (savedInstanceState == null) {
            //  BLE
            enableBleIfNeed()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    toast("BLEを利用できません")
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun enableBleIfNeed() {
        if (!isLocationPermissionsGranted(this)) {
            requestLocationPermission()
        }
        if (!isBleEnabled()) {
            requestBleEnable()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_ACCESS_FINE_LOCATION
        )
    }

    private fun requestBleEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }

    private fun isBleEnabled() =
        BluetoothAdapter.getDefaultAdapter()?.isDisabled ?: false

    private fun isLocationPermissionsGranted(context: Context) =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
}
