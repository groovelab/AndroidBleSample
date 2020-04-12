package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.data.Data
import java.time.Duration

class SampleBleManager(context: Context) : BleManager(context) {
    companion object {
        private val connectionTimeout = Duration.ofSeconds(10)
        private const val connectionRetryTime = 3
        private val connectionRetryDelay = Duration.ofMillis(100)
    }

    var discoveredServicesHandler: ((List<BluetoothGattService>) -> Unit)? = null
    var isConnecting = false
        private set
    var wasConnected = false
        private set

    private var gattCallback: BleManagerGattCallback? = null
    private inner class SampleBleManagerGattCallback: BleManagerGattCallback() {
        public override fun isRequiredServiceSupported(gatt: BluetoothGatt):Boolean {
            isConnecting = false
            wasConnected = true
            discoveredServicesHandler?.apply { this(gatt.services) }
            return true
        }

        override fun onDeviceDisconnected() {}
    }

    //  called before constructor
    override fun getGattCallback() = gattCallback ?: run {
        gattCallback = SampleBleManagerGattCallback()
        gattCallback!!
    }

    override fun log(priority: Int, message: String) {
        Log.d("BleManager", "$priority, $message")
    }

    override fun shouldClearCacheWhenDisconnected() = true

    fun enqueueConnect(bluetoothDevice: BluetoothDevice) {
        isConnecting = true
        connect(bluetoothDevice)
            .timeout(connectionTimeout.toMillis())
            .retry(connectionRetryTime, connectionRetryDelay.toMillis().toInt())
            .enqueue()
    }

    fun enqueueDisconnect(doneHandler: (() -> Unit)? = null) {
        isConnecting = false
        wasConnected = false
        disconnect()
            .done { doneHandler?.apply { this() } }
            .fail { _, _ -> doneHandler?.apply { this() } }
            .enqueue()
    }

    fun readRssi(callback: (Int) -> Unit) =
        readRssi()
            .with { _, rssi -> callback(rssi) }
            .enqueue()

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic, callback: (Data) -> Unit) =
        readCharacteristic(characteristic)
            .with { _, data -> callback(data) }
            .enqueue()

    fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, writeData: Data, callback: (Data) -> Unit) =
        writeCharacteristic(characteristic, writeData)
            .with { _, data -> callback(data) }
            .enqueue()

    fun enableNotificationCallBack(characteristic: BluetoothGattCharacteristic, callback: (Data) -> Unit) {
        setNotificationCallback(characteristic).with { _, data -> callback(data) }
        enableNotifications(characteristic).enqueue()
    }
}
