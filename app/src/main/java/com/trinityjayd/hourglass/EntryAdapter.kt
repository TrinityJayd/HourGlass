package com.trinityjayd.hourglass

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.trinityjayd.hourglass.dbmanagement.CategoryManagement
import com.trinityjayd.hourglass.models.Category
import com.trinityjayd.hourglass.models.Entry
import java.util.Locale

class EntryAdapter(private var entryList: List<Entry>) :
    RecyclerView.Adapter<EntryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorBlock: View = itemView.findViewById(R.id.categoryColorView)
        val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val photoImageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entryList[position]

        // Bind data to UI elements
        holder.taskNameTextView.text = entry.taskName
        holder.categoryTextView.text = entry.category
        holder.descriptionTextView.text = entry.taskDescription
        holder.durationTextView.text = formatDuration(entry.hours, entry.minutes)
        holder.dateTextView.text = entry.date

        //get category management instance
        val categoryManagement = CategoryManagement()

        categoryManagement.getCategoryColor(entry.category) { color ->
            holder.colorBlock.setBackgroundColor(color)
        }

        // Check if entry has an image
        if (!entry.imageKey.isNullOrEmpty()) {
            val storageRef = Firebase.storage.reference.child(entry.uid).child(entry.imageKey)
            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                Glide.with(holder.itemView.context)
                    .load(imageUrl)
                    .into(holder.photoImageView)
                holder.photoImageView.visibility = View.VISIBLE
            }.addOnFailureListener { exception ->
                // Handle image retrieval failure
                Log.e("EntryAdapter", "Failed to retrieve image: ${exception.message}")
            }
        } else {
            //make image view invisible
            holder.photoImageView.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return entryList.size
    }



    fun updateEntries(entries: List<Entry>) {
        entryList = entries
        notifyDataSetChanged()
    }

    private fun formatDuration(hours: Int, minutes: Int): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hours, minutes)
    }

}