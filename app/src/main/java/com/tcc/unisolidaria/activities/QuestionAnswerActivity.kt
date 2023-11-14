package com.tcc.unisolidaria.activities

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcc.unisolidaria.R
import com.tcc.unisolidaria.adapters.QuestionAnswerAdapter
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
                "Por que Precisamos do seu Nome e Sobrenome?",
                "Utilizamos seu nome e sobrenome para personalizar a experiência dentro do aplicativo. Isso ajuda a criar um ambiente mais amigável e confiável durante as interações entre motoristas e passageiros nas caronas solidárias."
            ),
            QuestionAnswer(
                "Por que Precisamos do seu Telefone?",
                "Seu número de telefone é essencial para facilitar a comunicação entre os usuários durante a coordenação das caronas. Além disso, serve como uma camada adicional de segurança ao verificar a identidade dos participantes."
            ),
            QuestionAnswer(
                "Por que Precisamos do seu E-mail Institucional?",
                " O endereço de e-mail institucional é utilizado como meio de identificação de alunos vinculados à Unifatec. Isso permite garantir que apenas membros da faculdade tenham acesso aos serviços oferecidos pelo aplicativo."
            ),
        )

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewQuestionAnswer)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = QuestionAnswerAdapter(listaPerguntasRespostas)
    }
}