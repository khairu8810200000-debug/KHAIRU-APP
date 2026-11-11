package com.khairu.app

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.view.ViewGroup
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : Activity() {
    private lateinit var display: TextView
    private var value = "0"
    private var first = 0.0
    private var operation: String? = null
    private var fresh = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this) {}
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 28, 16, 12)
            setBackgroundColor(Color.WHITE)
        }
        root.addView(TextView(this).apply {
            text = "KHAIRU APP"
            textSize = 27f
            gravity = Gravity.CENTER
            setTextColor(Color.rgb(15, 107, 66))
        }, LinearLayout.LayoutParams(-1, 70))
        display = TextView(this).apply {
            text = "0"
            textSize = 40f
            gravity = Gravity.CENTER_VERTICAL or Gravity.END
            setPadding(16, 10, 16, 10)
            setBackgroundColor(Color.rgb(235, 247, 240))
        }
        root.addView(display, LinearLayout.LayoutParams(-1, 120))
        val grid = GridLayout(this).apply { columnCount = 4 }
        val labels = listOf("AC","⌫","%","÷","7","8","9","×","4","5","6","−","1","2","3","+","±","0",".","=")
        for (label in labels) {
            val button = Button(this).apply {
                text = label
                textSize = 19f
                setOnClickListener { press(label) }
            }
            grid.addView(button, GridLayout.LayoutParams().apply {
                width = 0
                height = 0
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(3, 3, 3, 3)
            })
        }
        root.addView(grid, LinearLayout.LayoutParams(-1, 0, 1f))
        val ad = AdView(this).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = "ca-app-pub-3940256099942544/6300978111"
            loadAd(AdRequest.Builder().build())
        }
        root.addView(ad, LinearLayout.LayoutParams(-1, ViewGroup.LayoutParams.WRAP_CONTENT))
        setContentView(root)
    }

    private fun press(key: String) {
        when (key) {
            "AC" -> { value = "0"; first = 0.0; operation = null; fresh = true }
            "⌫" -> { value = if (value.length <= 1 || value.length == 2 && value.startsWith("-")) "0" else value.dropLast(1) }
            "." -> { if (fresh) { value = "0"; fresh = false }; if (!value.contains(".")) value += "." }
            "±" -> if (value != "0" && value != "Ralat") value = if (value.startsWith("-")) value.drop(1) else "-$value"
            "%" -> value = format(number() / 100)
            "+","−","×","÷" -> { first = number(); operation = key; fresh = true }
            "=" -> {
                val second = number()
                val result = when (operation) {
                    "+" -> first + second
                    "−" -> first - second
                    "×" -> first * second
                    "÷" -> if (second == 0.0) Double.NaN else first / second
                    else -> second
                }
                value = if (result.isFinite()) format(result) else "Ralat"
                operation = null
                fresh = true
            }
            else -> {
                value = if (fresh || value == "0" || value == "Ralat") key else if (value.length < 14) value + key else value
                fresh = false
            }
        }
        display.text = value
    }

    private fun number() = value.toDoubleOrNull() ?: 0.0
    private fun format(number: Double): String =
        BigDecimal.valueOf(number).setScale(8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
}
