package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.util.logging.Handler


class MainActivity : AppCompatActivity() {

    val SPLASH_SCREEN_TIME_OUT: Int = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handler to delay the intent by 3 seconds using the MainLooper
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            // After 3 seconds, start the NextActivity
            val intent = Intent(this@MainActivity, register::class.java)
            startActivity(intent)
            finish() // Closes the current activity
        }, SPLASH_SCREEN_TIME_OUT.toLong())
    }
}