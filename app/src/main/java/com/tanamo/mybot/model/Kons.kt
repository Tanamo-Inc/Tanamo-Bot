package com.tanamo.mybot.model


import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast

/**
 * This is my  Kons  Object.
 *
 * @author Tandoh Anthony Nwi-Ackah.
 *
 */


object Kons {

    const val COUNTS = 3000
    const val TAGG = "tanamo"
    const val REQUEST_RECORD_AUDIO_PERMISSION = 300
    const val RECORD_REQUEST_CODE = 101

    var workSpaceId: String? = null
    var cUsername: String = ""
    var cPassword: String = ""
    var sttUsername: String = ""
    var sttPassword: String = ""
    var ttsUsername: String = ""
    var ttsPassword: String? = ""

    fun initToast(c: Context, message: String) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show()
    }

    fun connect(context: Context): Boolean {
        val conec: Boolean
        val conectivtyManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        conec = conectivtyManager.activeNetworkInfo != null
                && conectivtyManager.activeNetworkInfo.isAvailable
                && conectivtyManager.activeNetworkInfo.isConnected
        return conec
    }


}
