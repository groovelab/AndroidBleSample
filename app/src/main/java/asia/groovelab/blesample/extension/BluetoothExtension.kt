package asia.groovelab.blesample.extension

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGattCharacteristic

val BluetoothAdapter.isDisabled: Boolean
    get() = !isEnabled

val BluetoothGattCharacteristic.isWritable: Boolean
    get() = properties.hasFlag(BluetoothGattCharacteristic.PROPERTY_WRITE) || properties.hasFlag(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)

val BluetoothGattCharacteristic.isReadable: Boolean
    get() = properties.hasFlag(BluetoothGattCharacteristic.PROPERTY_READ)

val BluetoothGattCharacteristic.isNotifiable: Boolean
    get() = properties.hasFlag(BluetoothGattCharacteristic.PROPERTY_NOTIFY)