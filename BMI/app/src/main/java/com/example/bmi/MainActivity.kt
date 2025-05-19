package com.example.bmi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val showBTN = findViewById<Button>(R.id.showBTN)
        val ageND = findViewById<EditText>(R.id.ageND)
        val heightND = findViewById<EditText>(R.id.heightND)
        val weightND = findViewById<EditText>(R.id.weightND)
        val bmiScore = findViewById<TextView>(R.id.bmiScore)
        val bmiGaugeView = findViewById<BmiGaugeView>(R.id.bmiGaugeView)
        val statusText = findViewById<TextView>(R.id.statusText)

        showBTN.setOnClickListener {
            val ageText = ageND.text.toString()
            val heightText = heightND.text.toString()
            val weightText = weightND.text.toString()

            if (ageText.isNotEmpty() && heightText.isNotEmpty() && weightText.isNotEmpty()) {
                val age = ageText.toIntOrNull()
                val heightCm = heightText.toDoubleOrNull()
                val weightKg = weightText.toDoubleOrNull()

                if (age != null && heightCm != null && weightKg != null && heightCm > 0) {
                    val heightM = heightCm / 100
                    val bmiS = weightKg / (heightM * heightM)
                    val bmiFormatted = String.format(Locale.US, "%.2f", bmiS)

                    val ageMessage = when {
                        age < 18 -> " (BMI for children is assessed differently)"
                        age >= 65 -> " (BMI for the elderly is assessed differently)"
                        else -> ""
                    }

                    val bmiText = getString(R.string.bmi_text, bmiFormatted, ageMessage)
                    bmiScore.text = bmiText
                    bmiGaugeView.bmiValue = bmiS.toFloat()

                    val status = getBmiStatus(bmiS)
                    statusText.text = status

                } else {
                    bmiScore.text = getString(R.string.error_invalid_data)
                }
            } else {
                bmiScore.text = getString(R.string.error_fill_all)
            }
        }

    }
}
private fun getBmiStatus(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}
