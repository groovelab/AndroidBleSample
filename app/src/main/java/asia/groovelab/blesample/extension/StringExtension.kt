package asia.groovelab.blesample.extension

import no.nordicsemi.android.ble.data.Data

val String.asHexByteArray: ByteArray
    inline get() {
        val hexString = if (length % 2 == 0) this else "0$this"
        return hexString.chunked(2).map { it.toUpperCase().toInt(16).toByte() }.toByteArray()
    }

val String.asHexData: Data
    inline get() = Data(asHexByteArray)
