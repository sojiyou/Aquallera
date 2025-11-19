package com.example.aquallera

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

class SignupActivity : AppCompatActivity() {
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText

    private lateinit var etNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etFullName = findViewById(R.id.FullNameInput)
        etEmail = findViewById(R.id.SignUpEmailInput)
        etNumber = findViewById(R.id.SignupNumberInput)
        etPassword = findViewById(R.id.PasswordInput)
        etConfirmPassword = findViewById(R.id.ConfirmPasswordInput)
        btnSignup = findViewById(R.id.SignUpBtn)
        tvLogin = findViewById(R.id.GoToLoginBtn)
    }

    private fun registerUser() {
        val fullName = etFullName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val number = etNumber.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val confirmPassword = etConfirmPassword.text.toString().trim()

        //input validation
        if (fullName.isEmpty() || email.isEmpty() || number.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }else if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            return
        } else if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }else if (number.length != 11) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
            return
        }

        // restrict the user from pressing the button while checking the database
        btnSignup.isEnabled = false
        btnSignup.text = "Processing..."

        checkIfUserExists(email, fullName, number, password, confirmPassword)
    }

    private fun checkIfUserExists(email: String, fullName: String, number: String, password: String, confirmPassword: String) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("users")

        //check if the user already exists
        userRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        signupError("Number already registered, Please use different number")
                    } else {
                        createUser(email, fullName, number, password, confirmPassword)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    signupError("Database Error: ${databaseError.message}")
                    Log.e("Firebase", "Database error", databaseError.toException())
                }
            })

    }

    private fun createUser(email: String, fullName: String, number: String, password: String, confirmPassword: String) {
        val database = FirebaseDatabase.getInstance().reference
        val user = User(fullName, email, number, password, confirmPassword)

        val newUserRef = database.child("users").push()
        newUserRef.setValue(user)
            .addOnSuccessListener {
                Log.d("Firebase", "User saved successfully with ID: ${newUserRef.key}")
                signupSuccess(user)
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error saving user", error)
                signupError("Failed to create account. Please try again.")
            }
    }

    private fun signupSuccess(user: User){
        saveUserSession(user)

        btnSignup.isEnabled = true
        btnSignup.text = "Sign Up"

        Toast.makeText(this, "Account created Successfully", Toast.LENGTH_SHORT).show()


        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signupError (errorMessage: String){
        btnSignup.isEnabled = true
        btnSignup.text = "Sign Up"
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun saveUserSession(user: User) {
        val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_name", user.fullName)
            putString("user_email", user.email)
            putString("user_number", user.number)
            putBoolean("is_logged_in", true)
            apply()
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setupClickListeners() {

        // sign up button click
        btnSignup.setOnClickListener {
            registerUser()
        }

        //login text button click
        tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}