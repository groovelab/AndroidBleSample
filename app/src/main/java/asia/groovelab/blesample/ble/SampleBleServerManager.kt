package asia.groovelab.blesample.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import asia.groovelab.blesample.extension.asHexByteArray
import no.nordicsemi.android.ble.BleServerManager
import no.nordicsemi.android.ble.observer.ServerObserver
import java.util.*


class SampleBleServerManager(private val context: Context) : BleServerManager(context) {
    companion object {
        private const val TAG = "SampleBleServerManager"
        private val serviceUUID = UUID.randomUUID()
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.d(TAG, "start advertise")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.d(TAG, "failed advertise")
        }
    }

    val readCharacteristic = characteristic(
        UUID.randomUUID(),
        BluetoothGattCharacteristic.PROPERTY_READ,
        BluetoothGattCharacteristic.PERMISSION_READ
    ).apply {
        value = "00".asHexByteArray
    }
    val writeCharacteristic = characteristic(
        UUID.randomUUID(),
        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
        BluetoothGattCharacteristic.PERMISSION_WRITE)
    val notifyCharacteristic = characteristic(
        UUID.randomUUID(),
        BluetoothGattCharacteristic.PROPERTY_NOTIFY,
        BluetoothGattCharacteristic.PERMISSION_READ,
        cccd())
    val gattService = service(serviceUUID, readCharacteristic, writeCharacteristic, notifyCharacteristic)

    private val connectionObserver = object: BaseConnectionObserver {}
    private val bondingObserver = object: BaseBondingObserver {}
    private val serverObserver = object: ServerObserver {
        override fun onDeviceConnectedToServer(device: BluetoothDevice) {
            setConnectedMangerToServer(device)
        }

        override fun onDeviceDisconnectedFromServer(device: BluetoothDevice) {
            Log.d(TAG, "disconnected from server")
        }

        override fun onServerReady() {
            startAdvertising()
        }
    }

    val canAdvertise: Boolean
        get() = advertiser != null

    private val advertiser: BluetoothLeAdvertiser?
        get() = BluetoothAdapter.getDefaultAdapter().bluetoothLeAdvertiser

    init {
        setServerObserver(serverObserver)
    }

    override fun initializeServer() = listOf(gattService)

    override fun log(priority: Int, message: String) {
        Log.d(TAG, "$priority $message")
    }

    fun startAdvertising() {
        val advertiseData = AdvertiseData.Builder()
            .addServiceUuid(ParcelUuid(serviceUUID))
            .build()

        val adviserSettings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setTimeout(0)
            .setConnectable(true)
            .build()

        advertiser?.startAdvertising(adviserSettings, advertiseData, advertiseCallback)
    }

    private fun stopAdvertising() {
        advertiser?.stopAdvertising(advertiseCallback)
    }

    fun clear() {
        close()
        stopAdvertising()
    }

    private fun setConnectedMangerToServer(device: BluetoothDevice) {
        val connectedManager = SampleConnectedBleManager(context)
        connectedManager.setConnectionObserver(connectionObserver)
        connectedManager.setBondingObserver(bondingObserver)
        connectedManager.useServer(this)
        connectedManager.withWriteCallback(writeCharacteristic)
            .with { _, data ->
                sendNotificationForWriteRequest(connectedManager, data.value)
            }

        connectedManager
            .connect(device)
            .enqueue()
    }

    private fun sendNotificationForWriteRequest(connectedManager: SampleConnectedBleManager, value: ByteArray?) {
        value?.let {
            connectedManager.sendNotification(notifyCharacteristic, "ff".asHexByteArray) {
                Log.d(TAG, "send notification")
            }
        }
    }
}