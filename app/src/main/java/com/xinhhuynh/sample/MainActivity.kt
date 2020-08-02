package com.xinhhuynh.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
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

        btnPermission.setOnClickListener {
            start(SamplePermissionActivity::class.java)
        }
    }
}

fun <C : AppCompatActivity> AppCompatActivity.start(clazz: Class<C>) {
    startActivity(Intent(this, clazz))
}

fun Context.toast(str: String) {
    Toast.makeText(this, str, Toast.LENGTH_LONG).show()
}