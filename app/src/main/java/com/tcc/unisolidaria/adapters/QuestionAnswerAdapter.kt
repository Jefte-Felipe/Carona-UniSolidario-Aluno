package com.tcc.unisolidaria.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tcc.unisolidaria.R
import com.tcc.unisolidaria.models.QuestionAnswer

class QuestionAnswerAdapter(private val listQuestionAnswer: List<QuestionAnswer>) :
    RecyclerView.Adapter<QuestionAnswerAdapter.QuestionAnswerViewHolder>() {

    class QuestionAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.questionTextView)
        val answer: TextView = itemView.findViewById(R.id.answerTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionAnswerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_data_user_lgpd, parent, false)
        return QuestionAnswerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionAnswerViewHolder, position: Int) {
        val questionAnswer = listQuestionAnswer[position]
        holder.question.text = questionAnswer.question
        holder.answer.text = questionAnswer.answer
    }

    override fun getItemCount(): Int {
        return listQuestionAnswer.size
    }
}