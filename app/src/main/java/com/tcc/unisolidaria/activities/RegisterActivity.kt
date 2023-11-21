package com.tcc.unisolidaria.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tcc.unisolidaria.databinding.ActivityRegisterBinding
import com.tcc.unisolidaria.models.Client
import com.tcc.unisolidaria.providers.AuthProvider
import com.tcc.unisolidaria.providers.ClientProvider
import com.tcc.unisolidaria.utils.CircleAnimationUtil

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authProvider = AuthProvider()
    private val clientProvider = ClientProvider()

    private var circleAnimationUtil: CircleAnimationUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.btnRegister.setOnClickListener { register() }
        startCirclesAnimation()

        binding.questData.setOnClickListener {
            lgpdData()
        }
    }

    private fun lgpdData() {
        val i = Intent(this, QuestionAnswerActivity::class.java)
        startActivity(i)
    }

    private fun startCirclesAnimation() {
        val circles = listOf(binding.imgCircleEnd)
        circleAnimationUtil = CircleAnimationUtil(circles)
        circleAnimationUtil?.start()
    }

    private fun register() {
        val name = binding.textFieldName.text.toString()
        val lastname = binding.textFieldLastname.text.toString()
        val email = binding.textFieldEmail.text.toString()
        val phone = binding.textFieldPhone.text.toString()
        val password = binding.textFieldPassword.text.toString()
        val confirmPassword = binding.textFieldConfirmPassword.text.toString()

        val (isValid, _) = isValidForm(name, lastname, email, password, confirmPassword)
        if (isValid) {
            authProvider.register(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val client = Client(
                        id = authProvider.getId(),
                        name = name,
                        lastname = lastname,
                        phone = phone,
                        email = email
                    )
                    clientProvider.create(client).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Cadastro realizado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            goToMap()
                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                "Ops, ocorreu uma falha ao salvar dados ${it.exception.toString()}",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                        }
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "E-mail institucional inválido\n Digite novamente",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("FIREBASE", "Error: ${it.exception.toString()}")
                }
            }
        }
    }

    private fun goToMap() {
        val i = Intent(this, MapActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun isValidForm(
        name: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Pair<Boolean, String> {

        val phone = binding.textFieldPhone.text.toString()
        val isPhoneValid = binding.textFieldPhone.isDone

        if (name.isEmpty()) {
            Toast.makeText(this, "Você deve inserir seu nome", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        if (lastname.isEmpty()) {
            Toast.makeText(this, "Você deve inserir seu sobrenome", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Você deve inserir seu e-mail", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        if (!isPhoneValid) {
            Toast.makeText(this, "Telefone inválido", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        val unmaskedPhone = binding.textFieldPhone.unMasked

        if (password.isEmpty()) {
            Toast.makeText(this, "Você deve inserir a senha", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Você deve inserir a confirmação da senha", Toast.LENGTH_SHORT)
                .show()
            return Pair(false, "")
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "As senhas devem corresponder", Toast.LENGTH_SHORT).show()
            return Pair(false, "")
        }

        if (password.length < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_LONG)
                .show()
            return Pair(false, "")
        }

        return Pair(true, unmaskedPhone)
    }


    override fun onDestroy() {
        circleAnimationUtil?.cancel()
        super.onDestroy()
    }
}