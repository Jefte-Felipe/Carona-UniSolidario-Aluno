package com.tcc.unisolidaria.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tcc.unisolidaria.databinding.ActivityQualificationBinding
import com.tcc.unisolidaria.models.History
import com.tcc.unisolidaria.providers.HistoryProvider


class QualificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQualificationBinding
    private var historyProvider = HistoryProvider()
    private var qualification = 0f
    private var history: History? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQualificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        binding.ratingBar.setOnRatingBarChangeListener { ratingBar, value, b ->
            qualification = value
        }

        binding.btnCalification.setOnClickListener {
            if (history?.id != null) {
                updateCalification(history?.id!!)
            }
            else {
                Toast.makeText(this, "O id do histórico é nulo", Toast.LENGTH_LONG).show()
            }
        }

        getHistory()
    }

    private fun updateCalification(idDocument: String) {
        historyProvider.updateCalificationToDriver(idDocument, qualification).addOnCompleteListener {
            if (it.isSuccessful) {
                goToMap()
            }
            else {
                Toast.makeText(this@QualificationActivity, "Erro ao atualizar a qualificação", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun goToMap() {
        val i = Intent(this, MapActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(i)
    }

    private fun getHistory() {
        historyProvider.getLastHistory().get().addOnSuccessListener { query ->
            if (query != null) {

                if (query.documents.size > 0) {
                    history = query.documents[0].toObject(History::class.java)

                    history?.id = query.documents[0].id
                    binding.textViewOrigin.text = history?.origin
                    binding.textViewDestination.text = history?.destination
                    binding.textViewPrice.text = "${String.format("%.1f", history?.price)}$"
                    binding.textViewTimeAndDistance.text = "${history?.time} Min - ${String.format("%.1f", history?.km)} Km"

                    Log.d("FIRESTORE", "HISTORIAL: ${history?.toJson()}")
                }
                else {
                    Toast.makeText(this, "Não vejo o histórico", Toast.LENGTH_LONG).show()
                }

            }
        }
    }
}