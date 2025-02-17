package com.example.epichostv2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        val imgSplash = findViewById<ImageView>(R.id.imgSplash)

        val translateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim)
        imgSplash.startAnimation(translateAnimation)

        Handler().postDelayed({
            val i = Intent(this@SplashActivity, SignInActivity::class.java)
            startActivity(i)
            finish()
        }, 5000)
    }
}