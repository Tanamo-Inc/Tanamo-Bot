package com.tanamo.mybot.model

import android.view.View


interface Clicker {

    fun onClick(view: View, position: Int)

    fun onLongClick(view: View, position: Int)
}