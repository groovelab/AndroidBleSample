package asia.groovelab.blesample.viewmodel


import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import asia.groovelab.blesample.ble.BaseBondingObserver
import asia.groovelab.blesample.ble.BaseConnectionObserver
import asia.groovelab.blesample.ble.SampleBleManager
import asia.groovelab.blesample.extension.asHexData
import asia.groovelab.blesample.extension.toHexString
import asia.groovelab.blesample.model.Item
import asia.groovelab.blesample.model.Peripheral
import asia.groovelab.blesample.model.Section
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class CentralPeripheralViewModel(val app: Application) : AndroidViewModel(app) {
    private var peripheral: Peripheral? = null
    private val bleManager: SampleBleManager by lazy {
        val manager = SampleBleManager(app)
        manager.setConnectionObserver(connectionObserver)
        manager.setBondingObserver(bondingObserver)
        manager.discoveredServicesHandler = { onDiscoveredServices(it) }
        manager
    }

    val appTitle = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val rssi = MutableLiveData<String>()
    val sections = MutableLiveData<List<Section>>()
    val items = MutableLiveData<List<List<Item>>>()
    val progressBarVisibility = MutableLiveData<Int>()

    val reconnected = MutableLiveData<Boolean>()
    val disconnectedFromDevice = MutableLiveData<Boolean>()
    var wroteCharacteristicHandler: ((UUID, ByteArray?) -> Unit)? = null
    var notifiedCharacteristicHandler: ((UUID, ByteArray?) -> Unit)? = null

    private val connectionObserver = object: BaseConnectionObserver {
        override fun onDeviceReady(device: BluetoothDevice) {
            bleManager.readRssi {
                rssi.postValue("${it}dbm")
            }
        }

        override fun onDeviceDisconnected(device: BluetoothDevice, reason: Int) {
            if (bleManager.isConnecting) {
                peripheral?.bluetoothDevice?.let {
                    reconnected.postValue(true)
                    bleManager.enqueueConnect(it)
                    return
                }
            }

            if (bleManager.wasConnected) {
                disconnect {
                    disconnectedFromDevice.postValue(true)
                }
            }
        }
    }
    private val bondingObserver = object : BaseBondingObserver {}

    init {
        appTitle.value = ""
        address.value = ""
        rssi.value = "-"
        progressBarVisibility.value = View.GONE
        sections.value = listOf()
        items.value = listOf()
    }

    fun connect(peripheral: Peripheral) {
        appTitle.postValue(peripheral.localName)
        address.postValue(peripheral.address)
        progressBarVisibility.postValue(View.VISIBLE)

        this.peripheral = peripheral

        peripheral.bluetoothDevice?.let {
            bleManager.enqueueConnect(it)
        }
    }

    fun disconnect(doneHandler: (() -> Unit)? = null) {
        progressBarVisibility.postValue(View.VISIBLE)

        val handler: (() -> Unit)? = doneHandler?.apply {
            viewModelScope.launch {
                delay(200)
                doneHandler()
            }
        }
        bleManager.enqueueDisconnect(handler)
    }

    fun readCharacteristic(item: Item) {
        if (!item.isReadable) {
            return
        }

        bleManager.readCharacteristic(item.bluetoothGattCharacteristic) { data ->
            data.value?.let { readValue ->
                getItem(item.bluetoothGattCharacteristic)?.let {
                    it.readValue = "0x${readValue.toHexString()}"
                    items.postValue(items.value)
                }
            }
        }
    }

    fun writeCharacteristic(item: Item, writeText: String) {
        if (!item.isWritable) {
            return
        }

        bleManager.writeCharacteristic(item.bluetoothGattCharacteristic, writeText.asHexData) {
            wroteCharacteristicHandler?.apply { this(item.bluetoothGattCharacteristic.uuid, it.value) }
        }
    }

    fun getItem(sectionPosition: Int, itemPosition: Int) =
        items.value?.get(sectionPosition)?.get(itemPosition)

    private fun getItem(characteristic: BluetoothGattCharacteristic) =
        items.value?.flatten()?.firstOrNull { it.uuid == characteristic.uuid.toString() }

    private fun onDiscoveredServices(services: List<BluetoothGattService>) {
        val sectionList = services.map { Section(it) }
        sections.postValue(sectionList)
        val itemList = services.map { service ->
            service.characteristics.map { characteristic ->
                bleManager.enableNotificationCallBack(characteristic) {
                    notifiedCharacteristicHandler?.apply { this(characteristic.uuid, it.value) }
                }
                Item(characteristic)
            }
        }
        items.postValue(itemList)
        progressBarVisibility.postValue(View.GONE)
    }
}