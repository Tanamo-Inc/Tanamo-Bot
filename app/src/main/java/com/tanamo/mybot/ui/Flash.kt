package com.tanamo.mybot.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.tanamo.mybot.R
import com.tanamo.mybot.model.Kons.COUNTS
import com.tanamo.mybot.model.Kons.TAGG


/**
 * This is my  Launcher Class.
 *
 * @author Tandoh Anthony Nwi-Ackah.
 *
 */

class Flash : AppCompatActivity() {

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAGG, "onCreate: ")
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)


        setContentView(R.layout.flash)




        if (!PrefM(this@Flash).isFirstTimeLaunch()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this@Flash.finish()
        } else {
            metho()
        }
    }

    private fun metho() {
        PrefM(this@Flash).setFirstTimeLaunch(false)
        Handler().postDelayed({
            this@Flash.finish()
            val mainIntent = Intent(this@Flash, MainActivity::class.java)
            this@Flash.startActivity(mainIntent)
        }, COUNTS.toLong()) //


    }

    inner class PrefM(_context: Context) {

        val pref: SharedPreferences
        private val editor: SharedPreferences.Editor
        private val Mode = 0


        init {
            pref = _context.getSharedPreferences("Tanamo", Mode)
            editor = pref.edit()
        }

        fun setFirstTimeLaunch(isFirstTime: Boolean) {
            editor.putBoolean("Fi", isFirstTime)
            editor.commit()
        }

        fun isFirstTimeLaunch(): Boolean {
            return pref.getBoolean("Fi", true)
        }

    }

}
