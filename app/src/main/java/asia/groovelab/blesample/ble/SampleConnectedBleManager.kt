package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.ValueChangedCallback

class SampleConnectedBleManager(context: Context) : BleManager(context) {
    companion object {
        private const val TAG = "SampleConnectedBleManager"
    }
    private var gattCallback: BleManagerGattCallback? = null
    private inner class GattCallback : BleManagerGattCallback() {
        override fun isRequiredServiceSupported(gatt: BluetoothGatt) = true
        override fun onDeviceDisconnected() {}
    }

    override fun getGattCallback() = gattCallback ?: run {
        gattCallback = GattCallback()
        gattCallback!!
    }

    override fun log(priority: Int, message: String) {
        Log.d(TAG, "$priority, $message")
    }

    fun withWriteCallback(serverCharacteristic: BluetoothGattCharacteristic): ValueChangedCallback {
        return super.setWriteCallback(serverCharacteristic)
    }

    fun sendNotification(serverCharacteristic: BluetoothGattCharacteristic, dataForNotify: ByteArray,
                         doneHandler: (() -> Unit)? = null) {
        super.sendNotification(serverCharacteristic, dataForNotify)
            .done { doneHandler?.let { it() } }
            .enqueue()
    }
}