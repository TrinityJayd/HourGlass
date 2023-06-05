package com.trinityjayd.hourglass

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.trinityjayd.hourglass.dbmanagement.EntryManagement
import com.trinityjayd.hourglass.models.Entry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID


class NewEntry : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storageRef: StorageReference
    private var filePath: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_entry)

        auth = Firebase.auth

        populateCategories()
        createDatePickerDialog()

        val date = findViewById<Button>(R.id.datePickerButton)
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        date.text = currentDate

        val hours = findViewById<EditText>(R.id.hoursEditText)
        val minutes = findViewById<EditText>(R.id.minutesEditText)

        val intent = intent
        //check if intent has extra
        if (intent.hasExtra("hours") && intent.hasExtra("minutes")) {
            //set hours and minutes
            hours.setText(intent.getStringExtra("hours"))
            minutes.setText(intent.getStringExtra("minutes"))
        }


        //get home image view
        val home = findViewById<ImageView>(R.id.homeImageView)
        //set on click listener
        home.setOnClickListener {
            //create intent to go to home page
            val intent = Intent(this, Home::class.java)
            //start activity
            startActivity(intent)
        }

        //get new category button
        val newCategory = findViewById<Button>(R.id.newCategoryButton)
        //set on click listener
        newCategory.setOnClickListener {
            //create intent to go to new category page
            val intent = Intent(this, NewCategory::class.java)
            //start activity
            startActivity(intent)
        }

        //get save button
        val save = findViewById<Button>(R.id.saveButton)

        //set on click listener
        save.setOnClickListener {
            //get task name edit test
            val taskName = findViewById<EditText>(R.id.editTextTaskName)
            val category = findViewById<Spinner>(R.id.spinnerCategory)
            val date = findViewById<Button>(R.id.datePickerButton)
            val description = findViewById<EditText>(R.id.editTextNotes)

            if (taskName.text.toString().isNullOrBlank()) {
                taskName.error = "Please enter a task name."
                return@setOnClickListener
            } else if (taskName.text.toString().length > 50) {
                taskName.error = "Please enter a task name less than 50 characters."
                return@setOnClickListener
            } else if (category.selectedItem.toString() == "Select Category") {
                //display error message
                Toast.makeText(this, "Please select a category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if (hours.text.toString().isNullOrBlank()) {
                hours.error = "Please enter hours."
                return@setOnClickListener
            } else if (hours.text.toString().toInt() > 24) {
                hours.error = "Please enter hours less than 24."
                return@setOnClickListener
            } else if (hours.text.toString().toInt() < 0) {
                hours.error = "Please enter hours greater than 0."
                return@setOnClickListener
            } else if (minutes.text.toString().isNullOrBlank()) {
                minutes.error = "Please enter minutes."
                return@setOnClickListener
            } else if (minutes.text.toString().toInt() > 60) {
                minutes.error = "Please enter minutes less than 60."
                return@setOnClickListener
            } else if (minutes.text.toString().toInt() < 0) {
                minutes.error = "Please enter minutes greater than 0."
                return@setOnClickListener
            } else if (description.text.toString().isNullOrBlank()) {
                //set description to blank
                description.setText("")
            } else if (description.text.toString().length > 100) {
                description.error = "Please enter a description less than 1000 characters."
                return@setOnClickListener
            } else {
                val taskNameText = taskName.text.toString()
                val selectedCategory = category.selectedItem.toString()
                val selectedDate = date.text.toString()
                val hoursText = hours.text.toString()
                val hoursInt = hoursText.toInt()
                val minutesText = minutes.text.toString()
                val minutesInt = minutesText.toInt()
                val descriptionText = description.text.toString()


                val user = auth.currentUser!!
                val uid = user.uid

                //create entry object
                val entry = Entry(
                    taskNameText,
                    selectedCategory,
                    selectedDate,
                    hoursInt,
                    minutesInt,
                    descriptionText,
                    filePath,
                    uid
                )

                //create entry management object
                val entryManagement = EntryManagement()

                entryManagement.addEntryToDatabase(entry)

                //create intent to go to home page
                val intent = Intent(this, Home::class.java)
                //start activity
                startActivity(intent)
            }


        }

        val picture = findViewById<Button>(R.id.pictureButton)
        picture.setOnClickListener {
            //Code Attribution
            //Android – Upload an Image on Firebase Storage with Kotlin
            //Author: GeeksforGeeks
            //Link: https://www.geeksforgeeks.org/android-upload-an-image-on-firebase-storage-with-kotlin/
            storageRef = Firebase.storage.reference
            val galleryIntent = Intent(Intent.ACTION_PICK)
            // here item is type of image
            galleryIntent.type = "image/*"
            // ActivityResultLauncher callback
            imagePickerActivityResult.launch(galleryIntent)
        }

    }

    private fun createDatePickerDialog() {
        //Code Attribution
        //Android Date Picker Dialog Example
        //Author: Coding Demos
        //Date: 22/02/2018
        //https://www.codingdemos.com/android-datepicker-button/
        val date = findViewById<Button>(R.id.datePickerButton)
        date.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            val year: Int = calendar.get(Calendar.YEAR)
            val month: Int = calendar.get(Calendar.MONTH)
            val dayOfMonth: Int = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this@NewEntry,
                { datePicker, year, month, day ->
                    val formattedDate = String.format("%02d/%02d/%04d", day, month + 1, year)
                    date.text = formattedDate
                }, year, month, dayOfMonth
            )
            datePickerDialog.show()
        }

    }

    //create function that populates the spinner with the categories from the database
    private fun populateCategories() {
        //get the category names from the children of the logged in user's uid from the realtime database where the user id is the same as the current user
        val user = auth.currentUser!!
        val uid = user.uid
        val database = Firebase.database
        val myRef = database.getReference("categories/$uid")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //create array list of categories
                val categories = ArrayList<String>()
                //add select category to array list
                categories.add("Select Category")
                //loop through each category in the database
                for (category in dataSnapshot.children) {
                    //get the category name
                    val categoryName = category.child("name").getValue(String::class.java)
                    //add the category name to the array list
                    categories.add(categoryName!!)
                }
                //get spinner
                val spinner = findViewById<Spinner>(R.id.spinnerCategory)
                //create array adapter
                val adapter = ArrayAdapter(
                    this@NewEntry,
                    R.layout.spinner_list,
                    categories
                )
                //set drop down view resource
                adapter.setDropDownViewResource(R.layout.spinner_list)
                //set adapter
                spinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("NewEntry", "Failed to read value.", error.toException())
            }
        })


    }

    //Code Attribution
    //Android – Upload an Image on Firebase Storage with Kotlin
    //Author: GeeksforGeeks
    //Link: https://www.geeksforgeeks.org/android-upload-an-image-on-firebase-storage-with-kotlin/

    private var imagePickerActivityResult: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result != null && result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val sd = getFileName(applicationContext, imageUri)
                    val user = auth.currentUser
                    val uid = user?.uid
                    if (uid != null) {
                        filePath = "file/$sd"
                        val uploadTask = storageRef.child(uid).child(filePath!!).putFile(imageUri)
                        uploadTask.addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "Image Uploaded!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, "Image Upload Failed!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            } else {
                // User cancelled the image selection
                Toast.makeText(applicationContext, "Image selection cancelled", Toast.LENGTH_SHORT)
                    .show()
            }
        }


    private fun getFileName(context: Context, uri: Uri): String? {
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor.use {
                if (cursor != null && cursor.moveToFirst()) {
                    val displayNameColumnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameColumnIndex >= 0) {
                        return cursor.getString(displayNameColumnIndex)
                    }
                }
            }
        }
        return uri.path?.lastIndexOf('/')?.let { uri.path?.substring(it + 1) }
    }

    //Code Attribution End





}