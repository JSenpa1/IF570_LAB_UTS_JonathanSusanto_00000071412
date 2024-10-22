package com.example.if570_lab_uts_jonathansusanto_00000071412

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AttendanceAdapter(private val attendanceList: List<AttendanceRecord>) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    // ViewHolder class
    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.textViewDateTime)
        val typeTextView: TextView = itemView.findViewById(R.id.textViewType)
        val attendanceImageView: ImageView = itemView.findViewById(R.id.imageViewAttendance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val attendanceRecord = attendanceList[position]
//        holder.emailTextView.text = attendanceRecord.email
        holder.dateTimeTextView.text = attendanceRecord.dateTime
//        holder.dateTimeTextView.text = attendanceRecord.dateTime?.toDate()?.toString()
        holder.typeTextView.text = attendanceRecord.type

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(attendanceRecord.imageUrl)
//            .placeholder(R.drawable.placeholder_image) // Optional: Set a placeholder image
//            .error(R.drawable.error_image) // Optional: Set an error image
            .into(holder.attendanceImageView)
    }

    override fun getItemCount(): Int {
        return attendanceList.size
    }
}
