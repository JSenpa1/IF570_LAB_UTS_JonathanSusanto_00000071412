package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // Firebase instances
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // UI elements
    private lateinit var textEmail: TextView
    private lateinit var textNama: TextView
    private lateinit var textNim: TextView
    private lateinit var buttonEditProfile: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize Firebase instances
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Find the views in the layout
        textEmail = view.findViewById(R.id.textEmail)
        textNama = view.findViewById(R.id.textNama)
        textNim = view.findViewById(R.id.textNim)
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile)

        loadUserProfile()

        buttonEditProfile.setOnClickListener {
            // Navigate to the fragment where the user can edit their profile (you need to implement this)
            // For example, use Navigation component to go to the EditProfileFragment
            Intent(activity, EditProfile::class.java).also {
                startActivity(it)
            }
        }

        return view
    }

    // Fetch the user data from Firestore
    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let {
            val userEmail = currentUser.email?.trim() ?: ""

            // Query Firestore to get the user document based on their email
            firestore.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userDocument = documents.documents[0]

                        // Extract the user's data from the document
                        val email = userDocument.getString("email") ?: ""
                        val nama = userDocument.getString("nama") ?: ""
                        val nim = userDocument.getString("nim") ?: ""

                        // Update the UI with the user's data
                        textEmail.text = email
                        textNama.text = nama
                        textNim.text = nim
                    } else {
                        Toast.makeText(requireContext(), "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error fetching data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}