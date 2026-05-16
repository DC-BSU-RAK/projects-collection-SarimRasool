package com.example.app1vibecalculator

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // UI elements
    private lateinit var tvEquation: TextView
    private lateinit var tvVibeResult: TextView
    private lateinit var tvVibeEmoji: TextView
    private lateinit var tvVibeDesc: TextView
    private lateinit var layoutResult: LinearLayout

    // State
    private val equation = StringBuilder()
    private var vibeScore = 0
    private var pendingOperator = 1
    private var operatorPending = false

    // Mood scores
    private val moodScores = mapOf(
        "Chill" to 5,
        "Hype" to 9,
        "Sad" to -6,
        "Anxious" to -4,
        "Happy" to 8,
        "Angry" to -7,
        "Bored" to -2,
        "Loved" to 10,
        "Stressed" to -5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        tvEquation = findViewById(R.id.tvEquation)
        tvVibeResult = findViewById(R.id.tvVibeResult)
        tvVibeEmoji = findViewById(R.id.tvVibeEmoji)
        tvVibeDesc = findViewById(R.id.tvVibeDesc)
        layoutResult = findViewById(R.id.layoutResult)

        // Mood button click listener
        val moodListener = View.OnClickListener { view ->
            val mood = when (view.id) {
                R.id.btnChill -> "Chill"
                R.id.btnHype -> "Hype"
                R.id.btnSad -> "Sad"
                R.id.btnAnxious -> "Anxious"
                R.id.btnHappy -> "Happy"
                R.id.btnAngry -> "Angry"
                R.id.btnBored -> "Bored"
                R.id.btnLoved -> "Loved"
                R.id.btnStressed -> "Stressed"
                else -> ""
            }
            if (mood.isNotEmpty()) addMood(mood)
        }

        listOf(
            R.id.btnChill, R.id.btnHype, R.id.btnSad, R.id.btnAnxious,
            R.id.btnHappy, R.id.btnAngry, R.id.btnBored, R.id.btnLoved, R.id.btnStressed
        ).forEach { findViewById<Button>(it).setOnClickListener(moodListener) }

        findViewById<Button>(R.id.btnPlus).setOnClickListener { addOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { addOperator("−") }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnCalculate).setOnClickListener { calculateVibe() }
        findViewById<Button>(R.id.btnInfo).setOnClickListener { showInfoModal() }
    }

    private fun addMood(mood: String) {
        val score = moodScores[mood] ?: 0
        if (equation.isEmpty()) {
            vibeScore += score
        } else if (operatorPending) {
            vibeScore += pendingOperator * score
        } else {
            equation.append(" + ")
            vibeScore += score
        }
        equation.append(mood)
        operatorPending = false
        pendingOperator = 1
        tvEquation.setTextColor(0xFFFFFFFF.toInt())
        tvEquation.text = equation.toString()
        layoutResult.visibility = View.GONE
    }

    private fun addOperator(op: String) {
        if (equation.isEmpty()) return
        if (operatorPending) {
            val lastSpace = equation.lastIndexOf(" ")
            if (lastSpace >= 0) equation.delete(lastSpace, equation.length)
        }
        equation.append(" $op ")
        pendingOperator = if (op == "+") 1 else -1
        operatorPending = true
        tvEquation.text = equation.toString()
    }

    private fun clearAll() {
        equation.clear()
        vibeScore = 0
        pendingOperator = 1
        operatorPending = false
        tvEquation.setTextColor(0x80FFFFFF.toInt())
        tvEquation.text = "Add your moods..."
        layoutResult.visibility = View.GONE
    }

    private fun calculateVibe() {
        if (equation.isEmpty()) return

        val (emoji, title, desc) = when {
            vibeScore >= 15 -> Triple("🌟✨💫", "LEGENDARY VIBE", "You are literally radiating light. The universe is lucky to have you.")
            vibeScore >= 8  -> Triple("🔥😊🌈", "HIGH VIBE ENERGY", "Your energy is contagious. People want to be around you right now.")
            vibeScore >= 3  -> Triple("😌🌿💚", "BALANCED VIBE", "You're in a peaceful, grounded state. Flow with it.")
            vibeScore >= 0  -> Triple("😐🌫️", "NEUTRAL VIBE", "Not great, not terrible. Maybe grab a snack and reset.")
            vibeScore >= -5 -> Triple("😔💙", "LOW VIBE", "You're carrying some heavy feelings. Be gentle with yourself today.")
            vibeScore >= -10 -> Triple("😵‍💫🌧️", "CHAOTIC VIBE", "It's a lot. Take a deep breath. This too shall pass.")
            else            -> Triple("🖤⚡😤", "VIBE: SEND HELP", "Maximum chaos detected. Step outside, drink water, you've got this.")
        }

        tvVibeEmoji.text = emoji
        tvVibeResult.text = title
        tvVibeDesc.text = desc

        layoutResult.visibility = View.VISIBLE
        layoutResult.alpha = 0f
        layoutResult.scaleX = 0.8f
        layoutResult.scaleY = 0.8f

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(layoutResult, "alpha", 0f, 1f),
                ObjectAnimator.ofFloat(layoutResult, "scaleX", 0.8f, 1f),
                ObjectAnimator.ofFloat(layoutResult, "scaleY", 0.8f, 1f)
            )
            duration = 400
            interpolator = OvershootInterpolator()
            start()
        }
    }

    private fun showInfoModal() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_info)
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.findViewById<Button>(R.id.btnCloseInfo).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}