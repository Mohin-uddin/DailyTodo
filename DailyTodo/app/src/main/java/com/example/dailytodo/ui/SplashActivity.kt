package com.example.dailytodo.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import com.example.dailytodo.R
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val preference = getSharedPreferences("Todo", Context.MODE_PRIVATE)
        val state= preference?.getBoolean("isLoggedIn",false)?:false
        val hyperspaceJump: Animation = AnimationUtils.loadAnimation(this, R.anim.fadein)
        cvLogo.startAnimation(hyperspaceJump)
        lifecycleScope.launch {
            delay(2000)
            if (state) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
            else
            {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }

        }
    }
}