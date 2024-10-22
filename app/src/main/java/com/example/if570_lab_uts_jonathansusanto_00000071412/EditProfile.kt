package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditProfile : AppCompatActivity() {

    private lateinit var editNama: EditText
    private lateinit var editNim: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: Button

    private val firestore = FirebaseFirestore.getInstance()
    private val mAuth = FirebaseAuth.getInstance()

    private fun loadUserData() {
        val currentUser = mAuth.currentUser
        currentUser?.let {
            val email = currentUser.email?.trim() ?: ""
            if (email != null) {
                firestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val userDocument = documents.documents[0]
                            val nama = userDocument.getString("nama") ?: ""
                            val nim = userDocument.getString("nim") ?: ""

                            // Pre-fill EditText fields
                            editNama.setText(nama)
                            editNim.setText(nim)
                        } else {
                            Toast.makeText(this@EditProfile, "No user data found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("EditProfileFragment", "Error fetching user data: ", exception)
                        Toast.makeText(this@EditProfile, "Error fetching user data", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun saveUserData() {
        val currentUser = mAuth.currentUser
        currentUser?.let {
            val email = it.email
            val newNama = editNama.text.toString().trim()
            val newNim = editNim.text.toString().trim()

            if (email != null && newNama.isNotEmpty() && newNim.isNotEmpty()) {
                firestore.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (!documents.isEmpty) {
                            val userDocumentId = documents.documents[0].id

                            // Update the user data
                            firestore.collection("users").document(userDocumentId)
                                .update(mapOf("nama" to newNama, "nim" to newNim))
                                .addOnSuccessListener {
                                    Toast.makeText(this@EditProfile, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                    // Navigate back to the profile fragment
                                    intent = Intent(this, ProfileFragment::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Log.e("EditProfileFragment", "Error updating profile: ", e)
                                    Toast.makeText(this@EditProfile, "Failed to update profile", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this@EditProfile, "No user data found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("EditProfileFragment", "Error fetching document: ", e)
                    }
            } else {
                Toast.makeText(this@EditProfile, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        // Initialize UI components
        editNama = findViewById(R.id.editNama)
        editNim = findViewById(R.id.editNim)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        loadUserData()

        buttonSave.setOnClickListener {
            saveUserData()
        }

        buttonCancel.setOnClickListener {
            // Navigate back to the profile fragment
            intent = Intent(this, ProfileFragment::class.java)
            startActivity(intent)
        }

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }
}