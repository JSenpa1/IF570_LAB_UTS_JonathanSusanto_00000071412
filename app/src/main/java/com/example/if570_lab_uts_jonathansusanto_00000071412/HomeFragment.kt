package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.Handler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var textViewGreetings: TextView
    private var currentAttendanceType: String? = null

    private lateinit var textViewClock: TextView
    private val handler = android.os.Handler()
    private val runnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000) // Update every second
        }
    }

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
//        return inflater.inflate(R.layout.fragment_home, container, false)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val textViewDateTime: TextView = view.findViewById(R.id.textViewDateTime)
        val buttonAttendance: Button = view.findViewById(R.id.buttonAttendance)
        val buttonAttendanceKeluar: Button = view.findViewById(R.id.buttonAttendance2)
        textViewGreetings = view.findViewById(R.id.textGreetings)

        textViewClock = view.findViewById(R.id.textViewDateTime)

        handler.post(runnable)

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        fetchUserName()

        // Get the current date and time
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Set the text of textViewDateTime to the current date and time
        textViewDateTime.text = currentDateTime

        // Handle attendance masuk
        buttonAttendance.setOnClickListener {
            dispatchTakePictureIntent("masuk")
        }

        // Handle attendance keluar
        buttonAttendanceKeluar.setOnClickListener {
            dispatchTakePictureIntent("keluar")
        }

        return view
    }

    private fun fetchUserName() {
        val currentUser = mAuth.currentUser
        val email = currentUser?.email ?: return // Get the current user's email

        firestore.collection("users") // Change to your Firestore collection name
            .whereEqualTo("email", email) // Query to find the user by email
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No user found with this email
                    textViewGreetings.text = "User not found"
                } else {
                    for (document in documents) {
                        // Get the user's name from the document
                        val name = document.getString("nama") // Adjust the field name as necessary
                        textViewGreetings.text = "Welcome, $name!" // Update the TextView with the name
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to fetch user name: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateClock() {
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        textViewClock.text = currentDateTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable) // Stop the updates when the view is destroyed
    }

    private fun dispatchTakePictureIntent(attendanceType: String) {
        currentAttendanceType = attendanceType
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.let {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
//            // Handle the image capture
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            // You can save the bitmap or display it as needed
//        }
//    }

    private fun checkAttendanceHistoryAndUploadImage(bitmap: Bitmap, attendanceType: String) {
        val currentUser = mAuth.currentUser
        val email = currentUser?.email ?: return
        val currentDateTime = System.currentTimeMillis()

        firestore.collection("attendance")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                var canAttendMasuk = true
                var canAttendKeluar = true

                for (document in documents) {
                    val attendanceRecord = document.data
                    val attendanceTypeStored = attendanceRecord["attendanceType"] as? String
                    val dateTimeStored = attendanceRecord["dateTime"] as? String

                    val recordTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTimeStored)?.time ?: continue

                    // Check if the stored record is within the last 24 hours
                    val withinLast24Hours = (currentDateTime - recordTime) < 24 * 60 * 60 * 1000

                    // If the record is 'masuk' and within 24 hours, restrict further 'masuk'
                    if (attendanceTypeStored == "masuk" && withinLast24Hours) {
                        canAttendMasuk = false
                    }

                    // If the record is 'keluar' and within 24 hours, restrict further 'keluar'
                    if (attendanceTypeStored == "keluar" && withinLast24Hours) {
                        canAttendKeluar = false
                    }
                }

                // Determine if the requested attendance type is allowed
                val canAttend = when (attendanceType) {
                    "masuk" -> canAttendMasuk
                    "keluar" -> canAttendKeluar
                    else -> false
                }

                if (!canAttend) {
                    Toast.makeText(
                        requireContext(),
                        "You cannot perform '$attendanceType' within 24 hours of the last one.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // Proceed to upload the image
                    uploadImage(bitmap, attendanceType)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Failed to fetch attendance records: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


//    private fun checkAttendanceHistoryAndUploadImage(bitmap: Bitmap, attendanceType: String) {
//        val currentUser = mAuth.currentUser
//        val email = currentUser?.email ?: return
//        val currentDateTime = System.currentTimeMillis()
//
//        // Get attendance records from Firestore
//        firestore.collection("attendance")
//            .whereEqualTo("email", email)
//            .get()
//            .addOnSuccessListener { documents ->
//                var canAttend = true
//                var lastKeluarTime: Long? = null
//                var lastMasukTime: Long? = null
//
//                for (document in documents) {
//                    val attendanceRecord = document.data
//                    val attendanceTypeStored = attendanceRecord["attendanceType"] as? String
//                    val dateTimeStored = attendanceRecord["dateTime"] as? String
//
//                    // Check if it's "keluar" and within the last 24 hours
//                    if (attendanceTypeStored == "keluar") {
//                        val recordTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTimeStored).time
//                        if (currentDateTime - recordTime < 24 * 60 * 60 * 1000) { // 24 hours in milliseconds
//                            canAttend = false
//                            lastKeluarTime = recordTime
//                            break
//                        }
//                    }
//
//                    // Check if it's masuk and within the last 24 hours
//                    if (attendanceTypeStored == "masuk") {
//                        val recordTimeMasuk = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(dateTimeStored).time
//                        if (currentDateTime - recordTimeMasuk < 24 * 60 * 60 * 1000) { // 24 hours in milliseconds
//                            canAttend = false
//                            lastMasukTime = recordTimeMasuk
//                            break
//                        }
//                    }
//                }
//
//                // Logic to handle attendance
//                if (!canAttend) {
//                    Toast.makeText(requireContext(), "You cannot attend 'masuk' or 'keluar' within 24 hours of the last 'keluar'.", Toast.LENGTH_SHORT).show()
//                } else {
//                    // Proceed to upload the image
//                    uploadImage(bitmap, attendanceType)
//                }
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(requireContext(), "Failed to fetch attendance records: ${exception.message}", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun saveToFirestore(imageUrl: String, email: String?, dateTime: String, attendanceType: String) {
        val attendanceData = hashMapOf(
            "email" to email,
            "imageUrl" to imageUrl,
            "dateTime" to dateTime,
            "attendanceType" to attendanceType
        )

        // Save the data in Firestore
        firestore.collection("attendance")
            .add(attendanceData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Attendance saved successfully", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Attendance saved successfully")
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save attendance", Toast.LENGTH_SHORT).show()
                Log.d("Firestore", "Attendance not saved")
            }
    }

    private fun uploadImage(bitmap: Bitmap, attendanceType: String) {
        val currentUser = mAuth.currentUser
        val email = currentUser?.email
        val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Convert Bitmap to ByteArray
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val imageData = byteArrayOutputStream.toByteArray()

        // Create a unique file name for the image
        val fileName = "${System.currentTimeMillis()}_attendance.jpg"
        val storageRef = storage.reference.child("attendance_images/$fileName")

        // Upload the image to Firebase Storage
        val uploadTask = storageRef.putBytes(imageData)
        uploadTask.addOnSuccessListener {
            // Get the download URL of the uploaded image
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // Store the image URL along with date, time, and user email in Firestore
                Log.d("Storage", "Image Upload successfully")
                saveToFirestore(uri.toString(), email, currentDateTime, attendanceType)
            }
        }.addOnFailureListener {
            // Handle any errors during the upload
            Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
//            uploadImage(imageBitmap, currentAttendanceType ?: "unknown")
            if (currentAttendanceType == "masuk") {
                checkAttendanceHistoryAndUploadImage(imageBitmap, "masuk")
            } else if (currentAttendanceType == "keluar") {
                checkAttendanceHistoryAndUploadImage(imageBitmap, "keluar")
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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}