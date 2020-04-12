package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothDevice
import android.util.Log
import no.nordicsemi.android.ble.observer.BondingObserver

interface BaseBondingObserver : BondingObserver {
    override fun onBondingRequired(device: BluetoothDevice) {
        Log.d("BaseBondingObserver", "onBondingRequired")
    }

    override fun onBonded(device: BluetoothDevice) {
        Log.d("BaseBondingObserver", "onBonded")
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        Log.d("BaseBondingObserver", "onBondingFailed")
    }
}