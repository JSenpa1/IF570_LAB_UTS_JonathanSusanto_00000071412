package com.example.if570_lab_uts_jonathansusanto_00000071412

data class AttendanceRecord(
    val email: String? = null,
    val imageUrl: String? = null,
    val dateTime: String? = null,
//    val dateTime: com.google.firebase.Timestamp? = null,
    val type: String? = null // "masuk" or "keluar"
)