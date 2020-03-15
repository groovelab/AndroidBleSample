package asia.groovelab.blesample.model

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcel
import android.os.Parcelable
import asia.groovelab.blesample.R
import asia.groovelab.blesample.extension.isNotifiable
import asia.groovelab.blesample.extension.isReadable
import asia.groovelab.blesample.extension.isWritable

data class Item(
    val uuid: String,
    val isReadable: Boolean,
    val isWritable: Boolean,
    val isNotifiable: Boolean,
    val bluetoothGattCharacteristic: BluetoothGattCharacteristic,
    var readValue: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        requireNotNull(parcel.readString()),
        requireNotNull(parcel.readInt() != 0),
        requireNotNull(parcel.readInt() != 0),
        requireNotNull(parcel.readInt() != 0),
        requireNotNull(parcel.readParcelable(BluetoothGattCharacteristic::class.java.classLoader)),
        parcel.readString()
    )
    constructor(source: BluetoothGattCharacteristic) : this(source.uuid.toString(), source.isReadable, source.isWritable, source.isNotifiable, source)

    var readableColorRes: Int = if (isReadable) R.color.colorTextNormal else R.color.colorTextDisabled
    var writableColorRes: Int = if (isWritable) R.color.colorTextNormal else R.color.colorTextDisabled
    var notifiableColorRes: Int = if (isNotifiable) R.color.colorTextNormal else R.color.colorTextDisabled

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uuid)
        parcel.writeInt(if (isReadable) 1 else 0)
        parcel.writeInt(if (isWritable) 1 else 0)
        parcel.writeInt(if (isNotifiable) 1 else 0)
        parcel.writeParcelable(bluetoothGattCharacteristic, flags)
        parcel.writeString(readValue)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item = Item(parcel)
        override fun newArray(size: Int): Array<Item?> = arrayOfNulls(size)
    }
}