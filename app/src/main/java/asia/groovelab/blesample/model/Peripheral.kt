package asia.groovelab.blesample.model

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable
import no.nordicsemi.android.support.v18.scanner.ScanResult

data class Peripheral(
    val localName: String?,
    val address: String,
    val rssi: Int,
    val serviceUuid: String?,
    val bluetoothDevice: BluetoothDevice?  = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        requireNotNull(parcel.readString()),
        requireNotNull(parcel.readInt()),
        parcel.readString(),
        parcel.readParcelable(BluetoothDevice::class.java.classLoader)
    )
    constructor(scanResult: ScanResult) : this(
        scanResult.device.name ?: "No Name",
        scanResult.device.address,
        scanResult.rssi,
        scanResult.scanRecord?.serviceUuids?.firstOrNull()?.uuid?.toString(),
        scanResult.device
    )

    var rssiString: String = "${rssi}dbm"

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(localName)
        parcel.writeString(address)
        parcel.writeInt(rssi)
        parcel.writeString(serviceUuid)
        parcel.writeParcelable(bluetoothDevice, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Peripheral> {
        override fun createFromParcel(parcel: Parcel): Peripheral = Peripheral(parcel)
        override fun newArray(size: Int): Array<Peripheral?> = arrayOfNulls(size)
    }
}