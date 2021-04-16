package com.example.pocket_library_list

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SplashScreen: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {



        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LogInScreen::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }


}