package com.example.epichostv2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    private lateinit var etRegisterFullName: EditText
    private lateinit var etRegisterPassword: EditText
    private lateinit var etRegisterEmail: EditText
    private lateinit var etRegisterPhone: EditText
    private lateinit var etRegisterAdminCode: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        etRegisterFullName = findViewById(R.id.etRegisterFullName)
        etRegisterPhone = findViewById(R.id.etRegisterPhone)
        etRegisterEmail = findViewById(R.id.etRegisterEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etRegisterAdminCode = findViewById(R.id.etRegisterAdminCode)
        btnRegister = findViewById(R.id.btnRegister)

        val baseUrl = readBaseUrl(this)
        apiService = RetrofitClient.getRetrofitInstance(baseUrl).create(ApiService::class.java)

        btnRegister.setOnClickListener {
            val fullname = etRegisterFullName.text.toString().trim()
            val password = etRegisterPassword.text.toString().trim()
            val email = etRegisterEmail.text.toString().trim()
            val phone = etRegisterPhone.text.toString().trim()
            val adminCode = etRegisterAdminCode.text.toString().trim()


            if (validateInput(fullname, phone, email, password)) {
                // Determine user type based on admin code
                val userType = if (adminCode == "secret") "admin" else "customer"
                val user = User(fullname, phone, email, password, userType)
                registerUser(user)
            }
        }
    }

    private fun validateInput(fullname: String, phone: String, email: String,  password: String): Boolean {
        if (fullname.isEmpty()) {
            etRegisterFullName.error = "Full Name is required"
            return false
        }

        if (phone.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            etRegisterPhone.error = "Enter a valid phone number"
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etRegisterEmail.error = "Enter a valid email address"
            return false
        }

        if (password.isEmpty() || password.length < 6) {
            etRegisterPassword.error = "Password must be at least 6 characters long"
            return false
        }

        return true
    }

    private fun registerUser(user: User) {
        apiService.registerUser(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration Successful", Toast.LENGTH_SHORT).show()
                    // Redirect to the SignInActivity
                    startActivity(Intent(this@RegisterActivity, SignInActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration Failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}