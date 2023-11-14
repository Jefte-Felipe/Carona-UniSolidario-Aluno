package com.tcc.unisolidaria.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcc.unisolidaria.R
import com.tcc.unisolidaria.databinding.ActivityQuestionAnswerBinding
import com.tcc.unisolidaria.models.QuestionAnswer

class QuestionAnswerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionAnswerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuestionAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        lgpdData()

    }

    private fun lgpdData() {
        val listaPerguntasRespostas = listOf(
            QuestionAnswer(
                "O que é carona solidária?",
                "A carona solidária é um sistema de transporte em que motoristas compartilham seus carros com outras pessoas que precisam da mesma rota."
            ),
            QuestionAnswer(
                "Como posso ser um motorista?",
                "Você pode se cadastrar como motorista no nosso aplicativo e oferecer caronas para outras pessoas."
            ),
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewQuestionAnswer)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuestionAnswerAdapter(listaPerguntasRespostas)
    }
}