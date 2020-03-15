package asia.groovelab.blesample

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import asia.groovelab.blesample.adapter.PeripheralListAdapter
import asia.groovelab.blesample.databinding.ActivityCentralBinding
import asia.groovelab.blesample.extension.isDisabled
import asia.groovelab.blesample.extension.toast
import asia.groovelab.blesample.viewmodel.CentralViewModel


class CentralActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_ACCESS_FINE_LOCATION = 1000
        private const val REQUEST_ENABLE_BT = 1001
    }

    private val viewModel: CentralViewModel by lazy {
        val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ViewModelProvider(this, factory).get(CentralViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //  setup binding
        val binding: ActivityCentralBinding = DataBindingUtil.setContentView(this, R.layout.activity_central)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.listView.adapter = PeripheralListAdapter(this)
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            viewModel.getPeripheral(position)?.let {
                startActivity(CentralPeripheralActivity.intent(this, it))
            }
        }
        viewModel.failedToScan.observe(this, Observer { isScanFailed ->
            if (isScanFailed) {
                toast(getString(R.string.error_scan))
            }
        })

        //  toolbar
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            //  BLE
            enableBleIfNeed()
            viewModel.startToScan()
        }
    }

    override fun onRestart() {
        super.onRestart()

        viewModel.reScan()
    }

    override fun onStop() {
        super.onStop()

        viewModel.stopToScan()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_central, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.scan_button -> {
                viewModel.reScan()
                true
            }
            R.id.sort_button -> {
                viewModel.sortPeripherals()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.firstOrNull() == PackageManager.PERMISSION_DENIED) {
                    toast(getString(R.string.error_start_to_scan))
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
