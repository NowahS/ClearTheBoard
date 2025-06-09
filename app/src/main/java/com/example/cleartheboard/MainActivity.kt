package com.example.cleartheboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var textViewDice: TextView
    private lateinit var rollButton: Button
    private lateinit var shareButton: Button
    private lateinit var undoButton: Button
    private lateinit var gridLayout: GridLayout
    private lateinit var colorSelector: RadioGroup

    private val numberButtons = mutableListOf<Button>()
    private val undoStack = mutableListOf<Button>()
    private val numbers = listOf(3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

    private var selectedColor: Int = Color.RED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find views
        textViewDice = findViewById(R.id.textViewDice)
        rollButton = findViewById(R.id.rollButton)
        shareButton = findViewById(R.id.shareButton)
        undoButton = findViewById(R.id.undoButton)
        gridLayout = findViewById(R.id.gridLayout)
        colorSelector = findViewById(R.id.colorSelector)

        // Set color selection logic
        colorSelector.setOnCheckedChangeListener { _, checkedId ->
            selectedColor = when (checkedId) {
                R.id.colorRed -> Color.RED
                R.id.colorBlue -> Color.BLUE
                R.id.colorGreen -> Color.GREEN
                else -> Color.RED
            }
        }

        // Create game board buttons
        createBoard()

        // Roll dice
        rollButton.setOnClickListener { rollDice() }

        // Share game
        shareButton.setOnClickListener { shareGameState() }

        // Undo button
        undoButton.setOnClickListener { undoLastAction() }
    }

    private fun createBoard() {
        for (number in numbers) {
            val button = Button(this).apply {
                text = number.toString()
                tag = number
                setBackgroundColor(Color.CYAN)
                setOnClickListener { toggleCounter(this) }
            }
            numberButtons.add(button)
            gridLayout.addView(button)
        }
    }

    private fun toggleCounter(button: Button) {
        if (button.alpha == 1.0f) {
            button.alpha = 0.5f
            button.setBackgroundColor(selectedColor)
            undoStack.add(button)
        } else {
            button.alpha = 1.0f
            button.setBackgroundColor(Color.CYAN)
        }
    }

    private fun undoLastAction() {
        if (undoStack.isNotEmpty()) {
            val last = undoStack.removeAt(undoStack.lastIndex)
            last.alpha = 1.0f
            last.setBackgroundColor(Color.CYAN)
        } else {
            Toast.makeText(this, "Nothing to undo!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun rollDice() {
        val die1 = Random.nextInt(1, 7)
        val die2 = Random.nextInt(1, 7)
        val die3 = Random.nextInt(1, 7)
        val total = die1 + die2 + die3

        //textViewDice.text = "Rolled: $die1 + $die2 + $die3 = $total"
        textViewDice.text = getString(R.string.dice_roll_result, die1, die2, die3, total)


        for (button in numberButtons) {
            if (button.alpha == 0.5f && button.tag == total) {
                button.visibility = View.INVISIBLE
            }
        }
    }

    private fun shareGameState() {
        val counters = numberButtons.filter { it.alpha == 0.5f }
            .joinToString(" ") { it.text.toString() }
        val message = "Current counters placed: $counters"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share game via:")
        startActivity(shareIntent)
    }
}
