package com.randomhq.afkav

import android.content.Context
import android.nfc.Tag
import android.util.Log
import com.google.gson.Gson

private var TAG_ID_PREFERENCES_KEY: String = "TAG_ID"
private val gson = Gson()

class CardHandler(private val context: Context) {

    fun saveCard(tag: Tag) = context.getSharedPreferences(
        context.packageName, Context.MODE_PRIVATE
    ).edit().putString(TAG_ID_PREFERENCES_KEY, gson.toJson(tag)).apply()

    fun loadCard(): Tag? {
        val json: String? = context.getSharedPreferences(
            context.packageName, Context.MODE_PRIVATE
        ).getString(
            TAG_ID_PREFERENCES_KEY,
            ""
        )
        return gson.fromJson(json, Tag::class.java) ?: return null
    }

    fun removeCard() = context.getSharedPreferences(
        context.packageName, Context.MODE_PRIVATE
    ).edit().clear().apply()
}

//private fun debug_detectTagData(tag: Tag): String {
//    val sb = StringBuilder()
//    val id = tag.id
//    sb.append("ID (hex): ").append(toHex(id)).append('\n')
//    sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n')
//    sb.append("ID (dec): ").append(toDec(id)).append('\n')
//    sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n')
//    val prefix = "android.nfc.tech."
//    sb.append("Technologies: ")
//    for (tech in tag.techList) {
//        sb.append(tech.substring(prefix.length))
//        sb.append(", ")
//    }
//    sb.delete(sb.length - 2, sb.length)
//    for (tech in tag.techList) {
//        if (tech == MifareClassic::class.java.name) {
//            sb.append('\n')
//            var type = "Unknown"
//            try {
//                val mifareTag = MifareClassic.get(tag)
//                when (mifareTag.type) {
//                    MifareClassic.TYPE_CLASSIC -> type = "Classic"
//                    MifareClassic.TYPE_PLUS -> type = "Plus"
//                    MifareClassic.TYPE_PRO -> type = "Pro"
//                }
//                sb.append("Mifare Classic type: ")
//                sb.append(type)
//                sb.append('\n')
//                sb.append("Mifare size: ")
//                sb.append(mifareTag.size.toString() + " bytes")
//                sb.append('\n')
//                sb.append("Mifare sectors: ")
//                sb.append(mifareTag.sectorCount)
//                sb.append('\n')
//                sb.append("Mifare blocks: ")
//                sb.append(mifareTag.blockCount)
//            } catch (e: Exception) {
//                sb.append("Mifare classic error: " + e.message)
//            }
//        }
//        if (tech == MifareUltralight::class.java.name) {
//            sb.append('\n')
//            val mifareUlTag = MifareUltralight.get(tag)
//            var type = "Unknown"
//            when (mifareUlTag.type) {
//                MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
//                MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
//            }
//            sb.append("Mifare Ultralight type: ")
//            sb.append(type)
//        }
//    }
//    Log.v("test", sb.toString())
//    return sb.toString()
//}