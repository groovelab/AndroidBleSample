package asia.groovelab.blesample.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asia.groovelab.blesample.model.Peripheral
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.time.Duration


class CentralViewModel(val app: Application): AndroidViewModel(app) {
    companion object {
        private val scanDelay = Duration.ofMillis(400)
    }

    val peripherals = MutableLiveData<MutableList<Peripheral>>()
    val failedToScan = MutableLiveData<Boolean>()

    private var isScanning: Boolean = false
    private val scanner: BluetoothLeScannerCompat
        get() = BluetoothLeScannerCompat.getScanner()
    private val scanSetting: ScanSettings by lazy {
        ScanSettings.Builder()
            .setLegacy(false)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(scanDelay.toMillis())
            .setUseHardwareBatchingIfSupported(false)
            .build()
    }
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            addPeripheral(Peripheral(result))
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            val peripherals = results.map { Peripheral(it) }
            addPeripherals(peripherals)
        }

        override fun onScanFailed(errorCode: Int) {
            failedToScan.postValue(true)
            isScanning = false
        }
    }

    init {
        peripherals.value = mutableListOf()
        failedToScan.value = false
    }

    fun addPeripherals(peripheralList: List<Peripheral>) {
        peripherals.value?.let { list ->
            peripheralList.forEach { peripheral ->
                val index = list.indexOfFirst { it.address == peripheral.address }
                if (index == -1) {
                    list.add(peripheral)
                } else {
                    list[index] = peripheral
                }
            }

            peripherals.postValue(list)
        }
    }

    fun addPeripheral(peripheral: Peripheral) = addPeripherals(listOf(peripheral))

    fun getPeripheral(position: Int) = peripherals.value?.get(position)

    private fun removeAllPeripherals() {
        peripherals.postValue(mutableListOf())
    }

    fun sortPeripherals() {
        peripherals.value?.sortedByDescending { it.rssi }?.let {
            peripherals.postValue(it.toMutableList())
        }
    }

    fun startToScan() {
        if (isScanning) {
            return
        }

        scanner.startScan(null, scanSetting, scanCallback)
        isScanning = true
    }

    fun stopToScan() {
        if (!isScanning) {
            return
        }

        scanner.stopScan(scanCallback)
        isScanning = false
    }

    fun reScan() {
        stopToScan()
        removeAllPeripherals()
        viewModelScope.launch {
            delay(scanDelay.toMillis())
            startToScan()
        }
    }
}