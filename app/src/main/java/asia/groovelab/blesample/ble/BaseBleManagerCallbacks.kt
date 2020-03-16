package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothDevice
import android.util.Log
import no.nordicsemi.android.ble.BleManagerCallbacks

interface BaseBleManagerCallbacks: BleManagerCallbacks {
    companion object {
        private const val TAG = "BaseBleManagerCallbacks"
    }
    override fun onDeviceConnecting(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceConnecting")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceConnected")
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceDisconnected")
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
        Log.d(TAG, "onLinkLossOccurred")
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
        Log.d(TAG, "onServicesDiscovered")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceReady")
    }

    override fun onBondingRequired(device: BluetoothDevice) {
        Log.d(TAG, "onBondingRequired")
    }

    override fun onBonded(device: BluetoothDevice) {
        Log.d(TAG, "onBonded")
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        Log.d(TAG, "onBondingFailed")
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
        Log.d(TAG, "onError ($errorCode) $message ")
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
        Log.d(TAG, "onDeviceNotSupported")
    }
}
