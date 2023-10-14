package com.tcc.unisolidaria.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tcc.unisolidaria.databinding.ActivityLoginBinding
import com.tcc.unisolidaria.databinding.SplashscreenMainBinding
import com.tcc.unisolidaria.providers.AuthProvider

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: SplashscreenMainBinding
    val authProvider = AuthProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
        window.statusBarColor = Color.parseColor("#D7D6D7")

        if (authProvider.existSession()) {
            // Se o usuário estiver logado, vá diretamente para MapActivity
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            //O usuário não está logado, mostra o SplashScreen
            binding = SplashscreenMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )

            binding.button.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
