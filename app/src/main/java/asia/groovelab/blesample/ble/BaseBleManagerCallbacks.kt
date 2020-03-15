package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothDevice
import android.util.Log
import no.nordicsemi.android.ble.BleManagerCallbacks

interface BaseBleManagerCallbacks: BleManagerCallbacks {
    override fun onDeviceConnecting(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceConnecting")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceConnected")
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceDisconnected")
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onLinkLossOccurred")
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
        Log.d("SampleBleManagerCallbacks", "onServicesDiscovered")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceReady")
    }

    override fun onBondingRequired(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onBondingRequired")
    }

    override fun onBonded(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onBonded")
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onBondingFailed")
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
        Log.d("PeripheralViewModel", "onError ($errorCode) $message ")
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
        Log.d("SampleBleManagerCallbacks", "onDeviceNotSupported")
    }
}
