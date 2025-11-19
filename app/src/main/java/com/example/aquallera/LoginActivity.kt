package com.example.aquallera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    //initialize later
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSignUp: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
        setupClickListeners()

    }

    private fun initializeViews() {
        etEmail = findViewById(R.id.LoginEmailInput)
        etPassword = findViewById(R.id.LoginPasswordInput)
        btnLogin = findViewById(R.id.LoginButton)
        tvSignUp = findViewById(R.id.GoToSignupBtn)
    }

    // logic in Clicking buttons
    private fun setupClickListeners() {

        //Login Button Click
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvSignUp.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        //input validation
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        } else if(!isValidEmail(email)){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        }

        //restrict user from pressing button while checking the database
        btnLogin.isEnabled = false
        btnLogin.text = "Logging in..."

        checkUserInDatabase(email, password)
    }

    private fun checkUserInDatabase(email: String, password: String){
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users")

        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot.exists()) {
                        for (userSnapshot in dataSnapshot.children) {
                            val user = userSnapshot.getValue(User::class.java)
                            if (user != null && user.password == password) {
                                loginSuccess(user)
                                return
                            }
                        }
                        // if password is wrong
                        loginError("invalid password")
                    } else {
                        // if email is wrong
                        loginError("User not found. Sign up first.")
                    }
                }
                override fun onCancelled(databaseError : DatabaseError) {
                    loginError("Database Error: ${databaseError.message}")
                    Log.e("Firebase", "Database error", databaseError.toException())
                }
            })
    }

    private fun loginSuccess(user: User) {
        saveUserSession(user)

        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loginError(errorMessage: String) {
        btnLogin.isEnabled = true
        btnLogin.text = "Login"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserSession(user: User) {

        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_email", user.email)
            putBoolean("is_logged_in", true)
            apply()
        }
    }
}