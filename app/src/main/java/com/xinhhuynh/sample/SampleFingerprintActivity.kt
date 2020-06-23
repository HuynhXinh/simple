package com.xinhhuynh.sample

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xinhhuynh.securitysp.SecuritySharePreference
import com.xinhhuynh.simplebiometric.SimpleBiometric
import com.xinhhuynh.simplebiometric.SimpleBiometricUtil
import kotlinx.android.synthetic.main.activity_fingerprint.*

class SampleFingerprintActivity : AppCompatActivity() {
    private lateinit var pre: SecuritySharePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)

        pre = SecuritySharePreference(
            this,
            "test_store_email_password"
        )

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            fakeLogin(email, password)

            pre.edit()
                .putString("email", email)
                .putString("password", password)
                .apply()

        }

        SimpleBiometricUtil.simpleCheck(this) { isAvailable ->

            if (isAvailable && isLogIn()) {
                btnBiometric.visibility = View.VISIBLE

                btnBiometric.setOnClickListener {
                    SimpleBiometric(
                        activity = this,
                        title = { "Biometric login for my app" },
                        subTitle = { "Log in using your biometric credential" },
                        buttonText = { "Use account password" }
                    ).simpleCallback(
                        onSuccess = {
                            val email = pre.getString("email", null)
                            val password = pre.getString("password", null)

                            etEmail.setText(email)
                            etPassword.setText(password)

                            fakeLogin(email, password)
                        },
                        onFail = {
                            "Authentication fail".toast()
                        },
                        onCancel = {
                            "Authentication cancel: $it".toast()
                        }).show()
                }
            } else {
                btnBiometric.visibility = View.GONE
            }
        }
    }

    private fun fakeLogin(email: String?, password: String?) {
        etEmail.visibility = View.GONE
        etPassword.visibility = View.GONE
        btnLogin.visibility = View.GONE
        btnBiometric.visibility = View.GONE

        tvInfo.text = "Email: $email\nPassword: $password"
    }

    private fun isLogIn(): Boolean {
        return pre.contains("email") and pre.contains("password")
    }

    private fun String.toast() {
        Toast.makeText(this@SampleFingerprintActivity, this, Toast.LENGTH_LONG).show()
    }
}