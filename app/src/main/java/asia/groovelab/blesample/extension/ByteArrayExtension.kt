package asia.groovelab.blesample.extension

import java.lang.Float

fun ByteArray.toHexString(isReverse: Boolean = false): String = map { String.format("%02x", it) }
    .let { if (isReverse) it.asReversed() else it }
    .joinToString("")

fun ByteArray.toInt() = toHexString(isReverse = true).toInt(16)

fun ByteArray.toLong() = toHexString(isReverse = true).toLong(16)

fun ByteArray.toSignedInteger() = map { it.unaryPlus() }.sum()

fun ByteArray.toFloat() = Float.intBitsToFloat(toLong().toInt())

fun ByteArray.copyWithLength(fromIndex: Int, length: Int) = copyOfRange(fromIndex, fromIndex + length)
