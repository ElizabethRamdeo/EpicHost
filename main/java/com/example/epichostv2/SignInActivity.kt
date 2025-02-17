package com.example.epichostv2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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

class SignInActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService
    private lateinit var btnSigninLogin: Button
    private lateinit var btnSignInToRegisterPage: Button
    private lateinit var etSignInEmail: EditText
    private lateinit var etSignInPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        btnSigninLogin = findViewById(R.id.btnSignInLogin)
        etSignInEmail = findViewById(R.id.etSignInEmail)
        etSignInPassword = findViewById(R.id.etSignInPassword)
        btnSignInToRegisterPage = findViewById(R.id.btnSignInGoToRegisterPage)

        val baseUrl = readBaseUrl(this)
        apiService = RetrofitClient.getRetrofitInstance(baseUrl).create(ApiService::class.java)

        btnSigninLogin.setOnClickListener {
            val email = etSignInEmail.text.toString().trim()
            val password = etSignInPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                userLogin(User( email = email, password = password))
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }


        btnSignInToRegisterPage.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SignInActivity, RegisterActivity::class.java))
        })

    }

    private fun userLogin(user: User) {
        apiService.loginUser(user).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val userType = loginResponse?.type
                    val userId = loginResponse?.user_id

                    Log.d("Login", "Response: $loginResponse")

                    if (userId != null) {
                        // Save user_id to SharedPreferences
                        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("user_id", userId)
                        editor.apply()

                        Toast.makeText(this@SignInActivity, "Login successful", Toast.LENGTH_SHORT).show()

                        // Redirect based on user type
                        when (userType) {
                            "admin" -> {
                                val intent = Intent(this@SignInActivity, AdminHomeActivity::class.java)
                                intent.putExtra("user_id", userId)
                                startActivity(intent)
                            }
                            "customer" -> {
                                val intent = Intent(this@SignInActivity, CustomerHomeActivity::class.java)
                                intent.putExtra("user_id", userId)
                                startActivity(intent)
                            }
                            else -> {
                                Toast.makeText(this@SignInActivity, "Unknown user type", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@SignInActivity, "Failed to retrieve user ID", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignInActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("Login", "Error: ${t.message}")
                Toast.makeText(this@SignInActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}