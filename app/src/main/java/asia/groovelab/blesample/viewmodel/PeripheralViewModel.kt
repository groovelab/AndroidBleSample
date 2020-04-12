package asia.groovelab.blesample.viewmodel


import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import asia.groovelab.blesample.R
import asia.groovelab.blesample.ble.SampleBleServerManager


class PeripheralViewModel(val app: Application) : AndroidViewModel(app) {
    private enum class ServerType {
        Advertise,
        None,
        Disabled
    }
    private var serverType = ServerType.Disabled
        set(value) {
            field = value

            when(field) {
                ServerType.Advertise -> {
                    advertiseButtonTitle.postValue(app.getString(R.string.advertise_stop_button_title))
                    serviceUuidVisibility.postValue(View.VISIBLE)
                    bleServerManager.open()
                }
                ServerType.None -> {
                    advertiseButtonTitle.postValue(app.getString(R.string.advertise_button_title))
                    serviceUuidVisibility.postValue(View.GONE)
                    bleServerManager.clear()
                }
                ServerType.Disabled -> {
                    isAdvertiseButtonEnabled.postValue(false)
                    serviceUuidVisibility.postValue(View.GONE)
                }
            }
        }

    val canAdvertise: Boolean
        get() = bleServerManager.canAdvertise

    private val bleServerManager = SampleBleServerManager(app)

    val isAdvertiseButtonEnabled = MutableLiveData<Boolean>()
    val advertiseButtonTitle = MutableLiveData<String>()
    val serviceUuid = MutableLiveData<String>()
    val characteristicReadUuid = MutableLiveData<String>()
    val characteristicWriteUuid = MutableLiveData<String>()
    val characteristicNotifyUuid = MutableLiveData<String>()
    val serviceUuidVisibility = MutableLiveData<Int>()

    init {
        //  initialize data
        isAdvertiseButtonEnabled.value = true
        advertiseButtonTitle.value =  app.getString(R.string.advertise_button_title)

        serviceUuid.value =  bleServerManager.gattService.uuid.toString()
        characteristicReadUuid.value =  bleServerManager.readCharacteristic.uuid.toString()
        characteristicWriteUuid.value =  bleServerManager.writeCharacteristic.uuid.toString()
        characteristicNotifyUuid.value =  bleServerManager.notifyCharacteristic.uuid.toString()
        serviceUuidVisibility.value = View.GONE

        if (canAdvertise) {
            serverType = ServerType.None
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickAdvertiseButton(view: View) {
        serverType = when (serverType) {
            ServerType.Advertise -> ServerType.None
            else -> ServerType.Advertise
        }
    }

    fun clear() {
        bleServerManager.clear()
    }
}
