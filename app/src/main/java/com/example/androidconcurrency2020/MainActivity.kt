package com.example.androidconcurrency2020

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.androidconcurrency2020.databinding.ActivityMainBinding
import java.lang.Thread.sleep
import kotlin.concurrent.thread
import kotlin.random.Random

private const val DIE_VALUE_KEY = "die_value"
private const val DIE_IMAGE_KEY = "die_image"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageViews: Array<ImageView>
    private val drawables = arrayOf(
        R.drawable.die_1,
        R.drawable.die_2,
        R.drawable.die_3,
        R.drawable.die_4,
        R.drawable.die_5,
        R.drawable.die_6
    )

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val dieValue = msg.data.getInt(DIE_VALUE_KEY, 1)
            val dieIndex = msg.data.getInt(DIE_IMAGE_KEY)
            imageViews[dieIndex].setImageResource(drawables[dieValue-1])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding for view object references
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViews = arrayOf(binding.die1, binding.die2, binding.die3, binding.die4, binding.die5)
        binding.rollButton.setOnClickListener { rollTheDice() }
    }

    /**
     * Run some code
     */
    private fun rollTheDice() {
        for (dieIndex in imageViews.indices) {
            thread(start = true) {
            var repeats = 0
                while (repeats < 10) {
                    val dieValue = getDieValue()
                    sendMessageToHandler(dieValue, dieIndex)
                    repeats++
                    sleep(100)
                }
            }
        }
    }

    private fun sendMessageToHandler(dieValue: Int, dieIndex: Int) {
        val bundle = Bundle()
        bundle.putInt(DIE_VALUE_KEY, dieValue)
        bundle.putInt(DIE_IMAGE_KEY, dieIndex)
        Message().also {
            it.data = bundle
            handler.sendMessage(it)
        }
    }

    /**
     * Get a random number from 1 to 6
     */
    private fun getDieValue(): Int {
        return Random.nextInt(1, 7)
    }

}
