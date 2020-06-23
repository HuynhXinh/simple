package com.xinhhuynh.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSampleDialog.setOnClickListener {
            start(SampleDialogActivity::class.java)
        }

        btnSampleBiometric.setOnClickListener {
            start(SampleFingerprintActivity::class.java)
        }
    }
}

fun <C : AppCompatActivity> AppCompatActivity.start(clazz: Class<C>) {
    startActivity(Intent(this, clazz))
}