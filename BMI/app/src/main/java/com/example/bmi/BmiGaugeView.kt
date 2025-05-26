package com.example.bmi

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.toColorInt
import java.text.DecimalFormat
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class BmiGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // value, dışarıdan (örneğin MainActivity.kt'den) bmiValue değişkenine atanan yeni değeri temsil eder.
    // field, bu sınıf içindeki bmiValue property'sinin arka planda tutulan gerçek verisidir.
    // Bu setter sayesinde dışarıdan gelen value değeri, field'a yani bu sınıftaki bmiValue'ya atanır.
    // Sonrasında invalidate() ile görünüm yeniden çizdirilir (onDraw() tetiklenir).
    // value değeri MainActivity.kt den gelen bmiValue değerini temsil eder ve bu değer field' da yani BmiGaugeView'daki bmiValue değerine atanmış olur.
    //Setter, bir nesnedeki (örneğin bir sınıftaki) özelliğe (property) yeni bir değer atandığında çalışan özel bir fonksiyondur. Kotlin’de set bloğu ile yazılır.

    var bmiValue: Float = 22.0f
        set(value) {
            field = value
            invalidate()
        }

    // arcRect değişkenine koordinatları (0,0,0,0) olan boş bir dikdörtgen atanıyor.
    private val arcRect = RectF()

    // bmiFormat değişkeni, ondalıklı sayıları "#0.0" formatında yani bir ondalık basamakla formatlamak için DecimalFormat sınıfından oluşturuluyor.
    private val bmiFormat = DecimalFormat("#0.0")

    // arcPaint değişkeni, çizim işlemlerinde kullanılacak olan Paint nesnesidir.
    // Paint.ANTI_ALIAS_FLAG parametresi ile çizim sırasında kenar yumuşatma (antialiasing) etkinleştirilir, böylece çizgiler daha pürüzsüz olur.
    // apply bloğu içinde arcPaint'in özellikleri ayarlanır:
    // - style: Paint.Style.STROKE olarak ayarlanmış, yani sadece şeklin kenarları çizilecek (dolgulu değil).
    // - strokeWidth: Kenar çizgi kalınlığı 60 piksel olarak belirlenmiş.
    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 60f
    }

    // arrowPaint değişkeni, ok (gösterge ibresi) çizimi için kullanılan Paint nesnesidir.
    // Paint.ANTI_ALIAS_FLAG ile kenar yumuşatma (antialiasing) etkinleştirilir, çizim daha düzgün olur.
    // apply bloğunda özellikler atanır:
    // - color: Siyah renk (Color.BLACK) olarak ayarlanır.
    // - strokeWidth: Çizgi kalınlığı 8 piksel olarak belirlenir.
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 8f
    }

    // textPaint değişkeni, metin çizimi için kullanılan Paint nesnesidir.
    // Paint.ANTI_ALIAS_FLAG ile kenar yumuşatma (antialiasing) etkinleştirilir, metin daha net görünür.
    // apply bloğunda özellikler atanır:
    // - textSize: Yazı boyutu 50 piksel olarak ayarlanır.
    // - color: Yazı rengi mavi (Color.BLUE) olarak belirlenir.
    // - textAlign: Metin hizalaması ortalanır (Paint.Align.CENTER).
    // - style: Çizim stili dolgu (Fill) olarak ayarlanır, yani metin dolu renkte olur.
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = Color.BLUE
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
    }

    // onDraw fonksiyonu, View sınıfının çizim işlemini gerçekleştirdiği fonksiyondur.
    // Android sistem tarafından bu View'in görünümü çizilmek istendiğinde çağrılır.
    // override ile üst sınıftaki (View) onDraw fonksiyonu ezilir, yani kendi çizim kodlarımız burada yazılır.
    // canvas parametresi, çizim işlemleri için kullanılan bir nesnedir, üzerine şekiller, metinler çizilir.

    // super.onDraw(canvas) ile üst sınıfın (View) orijinal çizim işlemi yapılır.
    // Genellikle varsayılan çizim yapılması ya da bazı temel işlemler için çağrılır.
    // Sonrasında kendi özel çizim kodlarımız yazılır.
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // View'in genişliği (piksel cinsinden) alınır ve Float tipine çevrilir.
        val width = width.toFloat()
        // View'in yüksekliği (piksel cinsinden) alınır ve Float tipine çevrilir.
        val height = height.toFloat()
        // Çemberin yarıçapı hesaplanıyor.
        // width ve height'dan daha küçük olanı seçilip 2.5'e bölünüyor.
        // Böylece çember hem yatay hem dikey sığacak şekilde ayarlanıyor.
        val radius = min(width, height) / 2.5f
        // Çemberin yataydaki merkezi, yani View'in ortası.
        val centerX = width / 2
        // Çemberin dikeydeki merkezi, View yüksekliğinin %80'i.
        // Yani çember biraz aşağıda yer alıyor, ekranın tam ortasında değil.
        val centerY = height * 0.8f

        //arcRect yay çizimi için "alan" yani konum ve boyut bilgisi sağlar.
        // arcPaint ise yay çizilirken nasıl görüneceğini (renk, kalınlık vs.) belirtir.

        // arcRect dikdörtgeninin sol, üst, sağ ve alt kenarlarını belirleyerek,
        // yay çiziminde kullanılacak alanı merkez (centerX, centerY) ve yarıçap (radius) kullanarak ayarlıyoruz.
        arcRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        // arcPaint nesnesinin rengini "#64B5F6" (açık mavi) olarak ayarlıyoruz
        arcPaint.color = "#64B5F6".toColorInt()
        // arcRect dikdörtgeni içinde 180 derece başlangıç açısıyla 45 derece yay çiziyoruz (sol alt kısım)
        canvas.drawArc(arcRect, 180f, 45f, false, arcPaint)

        // arcPaint rengini "#81C784" (açık yeşil) yapıyoruz
        arcPaint.color = "#81C784".toColorInt()
        // arcRect içinde 225 derece başlangıç açısıyla 90 derece yay çiziyoruz (orta kısım)
        canvas.drawArc(arcRect, 225f, 90f, false, arcPaint)

        // arcPaint rengini "#E57373" (açık kırmızı) olarak ayarlıyoruz
        arcPaint.color = "#E57373".toColorInt()
        // arcRect içinde 315 derece başlangıç açısıyla 45 derece yay çiziyoruz (sağ alt kısım)
        canvas.drawArc(arcRect, 315f, 45f, false, arcPaint)

        // bmiValue değerine göre okun hangi açıyla çizileceğini hesapla
        val angle = mapBmiToAngle(bmiValue)

        // Okun uzunluğunu belirle (yarıçaptan 40 piksel küçük)
        val arrowLength = radius - 40

        // Açıyı derece cinsinden radyan cinsine çevir (trigonometrik işlemler için)
        val rad = Math.toRadians(angle.toDouble())

        // Okun bitiş noktasının X koordinatını hesapla
        val endX = (centerX + arrowLength * cos(rad)).toFloat()

        // Okun bitiş noktasının Y koordinatını hesapla
        val endY = (centerY + arrowLength * sin(rad)).toFloat()

        // Başlangıç noktası (centerX, centerY) ile bitiş noktası (endX, endY) arasında çizgi çiz (ok)
        canvas.drawLine(centerX, centerY, endX, endY, arrowPaint)

        // bmiValue değerini #0.0 formatında biçimlendirerek "BMI: ..." şeklinde metin oluştur
        val bmiText = "BMI: ${bmiFormat.format(bmiValue)}"
        // Oluşan metni canvas üzerine çiz, konum: yatayda merkez (centerX), dikeyde okun üstünden biraz yukarı (centerY - radius - 60)
        canvas.drawText(bmiText, centerX, centerY - radius - 60, textPaint)
    }

    // bmi değerine göre okun (gösterge oku) hangi açıda duracağını hesaplayan fonksiyon
    private fun mapBmiToAngle(bmi: Float): Float {
        return when {
            // Eğer bmi 18.5'ten küçükse
            bmi < 18.5f -> {
                // 180 derece ile 225 derece aralığında lineer bir dönüşüm yapılıyor.
                // 10 ile 18.5 arasındaki değer bmi için haritalanıyor.
                180f + ((bmi - 10f) / 8.5f) * 45f
            }

            // Eğer bmi 18.5 ile 25 arasındaysa
            bmi < 25f -> {
                // 225 ile 315 derece aralığında lineer dönüşüm yapılıyor.
                // 18.5 ile 25 arasındaki bmi değerleri bu aralığa eşleniyor.
                225f + ((bmi - 18.5f) / 6.5f) * 90f
            }

            // Eğer bmi 25 veya daha büyükse
            else -> {
                // 315 ile 360 derece aralığında lineer dönüşüm yapılır.
                // 25 ile 40 arasındaki değerler bu aralığa eşlenir.
                val capped = bmi.coerceAtMost(40f)
                315f + ((capped - 25f) / 15f) * 45f
            }
        }
    }
}
