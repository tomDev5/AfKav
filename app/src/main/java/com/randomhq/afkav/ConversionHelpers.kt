package com.randomhq.afkav

import kotlin.experimental.and

fun toHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices.reversed()) {
        val b: Byte = bytes[i] and 0xff.toByte()
        if (b < 0x10) sb.append('0')
        sb.append(Integer.toHexString(b.toInt()))
        if (i > 0) {
            sb.append(" ")
        }
    }
    return sb.toString()
}

fun toReversedHex(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices) {
        if (i > 0) {
            sb.append(" ")
        }
        val b: Byte = bytes[i] and 0xff.toByte()
        if (b < 0x10) sb.append('0')
        sb.append(Integer.toHexString(b.toInt()))
    }
    return sb.toString()
}

fun toDec(bytes: ByteArray): Long {
    var result: Long = 0
    var factor: Long = 1
    for (i in bytes.indices) {
        val value: Byte = bytes[i] and 0xffL.toByte()
        result += value * factor
        factor *= 256L
    }
    return result
}

fun toReversedDec(bytes: ByteArray): Long {
    var result: Long = 0
    var factor: Long = 1
    for (i in bytes.indices.reversed()) {
        val value: Byte = bytes[i] and 0xffL.toByte()
        result += value * factor
        factor *= 256L
    }
    return result
}