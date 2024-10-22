package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        var registerEmail: EditText = this.findViewById(R.id.registerEmail)
        val registerNama: EditText = findViewById(R.id.registerNama)
        val registerNim: EditText = findViewById(R.id.registerNim)
        var registerPassword: EditText = this.findViewById(R.id.registerPassword)
        var registerButton: Button = this.findViewById(R.id.registerButton)
        val changeLogin: Button = this.findViewById(R.id.changePageLogin)

        var mAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        changeLogin.setOnClickListener {
            startActivity(Intent(this@register, login::class.java))
            finish()
        }

        registerButton.setOnClickListener {
            val email = registerEmail.text.toString().trim()
            val nama = registerNama.text.toString().trim()
            val nim = registerNim.text.toString().trim()
            val password = registerPassword.text.toString().trim()

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@register, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user with Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Get the user ID (UID) from the newly created user
                        val uid = mAuth.currentUser?.uid

                        // Create a map to store user data
                        val userData = hashMapOf(
                            "email" to email,
                            "nama" to nama,
                            "nim" to nim
                        )

                        // Save user data to Firestore using the UID
                        if (uid != null) {
                            firestore.collection("users").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this@register, "Registration Successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@register, login::class.java))
                                    finish() // Close the current activity
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@register, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this@register, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}