package com.xinhhuynh.simpledialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnShowOne.setOnClickListener {
            showSimpleDialog(
                    title = { "Hi One !" },
                    msg = { "Thanks for your visit to simple dialog. This is simple with positive button" },
                    textPositive = { "Ok" }
            )
        }

        btnShowTwo.setOnClickListener {
            showSimpleDialog(
                    title = { "Hi Two!" },
                    msg = { "Thanks for your visit to simple dialog. This is simple with two positive and negative buttons" },
                    textPositive = { "Ok" },
                    textNegative = { "Cancel" }
            )
        }

        btnShowCustom.setOnClickListener {
            showCustomSimpleDialog(
                    title = { "Hi Custom!" },
                    msg = { "Thanks for your visit to simple dialog. This is custom simple dialog" },
                    textPositive = { "Ok" },
                    textNegative = { "Cancel" },
                    isCancelable = { false }
            )
        }

        btnShowTextButtonTooLong.setOnClickListener {
            showSimpleDialog(
                    title = { "Hi Long Text Button!" },
                    msg = { "Thanks for your visit to simple dialog. This is very long text button" },
                    textPositive = { "Very long text button OK" },
                    textNegative = { "Cancel" }
            )
        }
    }
}
