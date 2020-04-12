package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothDevice
import android.util.Log
import no.nordicsemi.android.ble.observer.ConnectionObserver

interface BaseConnectionObserver : ConnectionObserver {
    override fun onDeviceConnecting(device: BluetoothDevice) {
        Log.d("BaseConnectionObserver", "onDeviceConnecting")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        Log.d("BaseConnectionObserver", "onDeviceConnected")
    }

    override fun onDeviceFailedToConnect(device: BluetoothDevice, reason: Int) {
        Log.d("BaseConnectionObserver", "onDeviceFailedToConnect")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        Log.d("BaseConnectionObserver", "onDeviceReady")
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        Log.d("BaseConnectionObserver", "onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
        Log.d("BaseConnectionObserver", "onDeviceDisconnected")
    }
}