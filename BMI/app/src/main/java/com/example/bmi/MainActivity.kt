package com.example.bmi

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

// ana sınıf (aktivite) — Uygulamanın ekranı
class MainActivity : AppCompatActivity() {
    //onCreate fonksiyonu uygulamamızın ilk çalışan fonksiyodur. Kodlarımızı bunu içine yazıyoruz.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // xml arayüzünde tanımlamış olduğumuz nesneleri kotlin kodunda kullanmak için bağlantılar kuruyoruz.
        val ageND = findViewById<EditText>(R.id.ageND)
        val heightND = findViewById<EditText>(R.id.heightND)
        val weightND = findViewById<EditText>(R.id.weightND)
        val showBTN = findViewById<Button>(R.id.showBTN)
        val statusText = findViewById<TextView>(R.id.statusText)
        val bmiScore = findViewById<TextView>(R.id.bmiScore)
        val bmiGaugeView = findViewById<BmiGaugeView>(R.id.bmiGaugeView)

        // butona tıklandığında ne olacağını tanımlar.
        showBTN.setOnClickListener {
            // kullanıcının girdiği Editable veri tipindeki değerleri String'e çevirip local değişkenlere atıyoruz.
            val ageText = ageND.text.toString()
            val heightText = heightND.text.toString()
            val weightText = weightND.text.toString()

            // eğer yaş boy kilo değişkenleri boş değilse
            if (ageText.isNotEmpty() && heightText.isNotEmpty() && weightText.isNotEmpty()) {
                //değikenleri Int ve Double a çeviriyoruz eğer dönüşüm başarısız olursa da null değer döndürüyoruz böylece programın çökmesini engelliyoruz.
                val age = ageText.toIntOrNull()
                val heightCm = heightText.toDoubleOrNull()
                val weightKg = weightText.toDoubleOrNull()

                // yeni değişkenlerimiz null değilse ve 0 dan büyükse
                if (age != null && heightCm != null && weightKg != null && age > 0 && heightCm > 0 && weightKg > 0) {
                    // calculateBmi fonksiyonumuz ile bmi değerini hesaplayıp bmiValue değişkenine atıyoruz.
                    val bmiValue = calculateBmi(heightCm, weightKg)
                    // hesapladığımız bmi değerini ondalık kısmı 2 basamak olacak şekilde ve sadece nokta ile ayrılabilecek şekilde ayarlıyoruz. Virgül kullanımını engelleriyoruz.
                    val bmiFormatted = String.format(Locale.US, "%.2f", bmiValue)

                    // eğer yaş değişkenimiz 18 den küçükse strings.xml den çektiğimiz metini ageMesage değişkenine atıyoruz. 65 den büyük ise başka bir metni çekip atıyoruz. Eğre ikisinede uymuyorsa boş değer atıyoruz.
                    val ageMessage = when {
                        age < 18 -> getString(R.string.bmi_message_child)
                        age >= 65 -> getString(R.string.bmi_message_elderly)
                        else -> ""
                    }

                    // bmiText değişkenine, strings.xml dosyasındaki bmi_text adlı şablon string kullanılarak, yer tutucular (%1$s, %2$s) yardımıyla bmiFormatted ve ageMessage değerleri yerleştirilir. Bu yöntem, metinleri daha sonra farklı dillere çevirmeyi kolaylaştırır çünkü sadece strings.xml içeriği değiştirerek tüm uygulama çok dilli hale getirilebilir.
                    val bmiText = getString(R.string.bmi_text, bmiFormatted, ageMessage)
                    // bmiText değişkeni, bmiScore'un text özelliğine atanıyor, böylece sonuç ekranda gösterilmiş oluyor.
                    bmiScore.text = bmiText
                    // MainActivity.kt dosyasındaki bmiValue değerini Float'a çeviriyoruz sonra bu değeri BmiGaugeView.kt deki bmiValue değişkenine atıyoruz.
                    bmiGaugeView.bmiValue = bmiValue.toFloat()

                    //bmiValue değeri getBmiStatus fonksiyonumuza gönderilir ve buradan dönen String status değişkenine atanır.
                    val status = getBmiStatus(this, bmiValue)
                    // status değişkeni, statusText'in text özelliğine atanıyor, böylece sonuç ekranda gösterilmiş oluyor.
                    statusText.text = status

                    // yeni değişkenlerimiz null ya da 0 dan küçük eşitse
                } else {
                    // strings.xml den çektiğimiz metini bmiScore'un text özelliğine atıyoruz. Böylece hata mesajımız ekranda gösterilmiş oluyor.
                    bmiScore.text = getString(R.string.error_invalid_data)
                }
                // eğer yaş boy kilo değişkenleri boşsa
            } else {
                //strings.xml den çektiğimiz metini bmiScore'un text özelliğine atıyoruz. Böylece hata mesajımız ekranda gösterilmiş oluyor.
                bmiScore.text = getString(R.string.error_fill_all)
            }
        }

    }
}

// getBmiStatus private'dır yani başka bir sınıfa kullanılamaz iki tane paremetre alır context ve bmi.
// context: Android uygulama ortamını temsil eder. String kaynaklarına erişmek için gerekli.
// bmi: Hesaplanan BMI değeri, Double türünde.
// : String: Fonksiyon, bir String (metin) döndürecektir.
private fun getBmiStatus(context: Context, bmi: Double): String {
    return when {
        // eğer bmi değeri 18.5 in altındaysa string.xml' den zayıf değerini döndürecektir.
        bmi < 18.5 -> context.getString(R.string.bmi_underweight)
        // eğer bmi değeri 25 in altındaysa string.xml' den normal değerini döndürecektir.
        bmi < 25.0 -> context.getString(R.string.bmi_normal)
        // eğer bmi değeri 30 un altındaysa string.xml' den kilolu değerini döndürecektir.
        bmi < 30.0 -> context.getString(R.string.bmi_overweight)
        // eğer bmi değeri bunlardan farklıysa string.xml' den obez değerini döndürecektir.
        else -> context.getString(R.string.bmi_obese)
    }
}

// calculateBmi private'dır yani başka bir sınıfa kullanılamaz iki tane paremetre alır heightCm ve weightKg.
private fun calculateBmi(heightCm: Double, weightKg: Double): Double {
    // heightCm 100'e bölünerek metreye çevrilir.
    val heightM = heightCm / 100
    // weightKg, heightM nin karesine bölünerek bmi değeri hesaplanır.
    return weightKg / (heightM * heightM)
}
