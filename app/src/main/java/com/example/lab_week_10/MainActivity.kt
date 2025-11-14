package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {
    // Create an instance of the TotalDatabase
    // by lazy is used to create the database only when it's needed
    private val db by lazy { prepareDatabase() }

    // Create an instance of the TotalViewModel
    // by lazy is used to create the ViewModel only when it's needed
    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the value of the total from the database
        initializeValueFromDatabase()
        // Prepare the ViewModel
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        // Show toast with the last update date when app starts
        val total = db.totalDao().getTotal(ID)
        if (total.isNotEmpty() && total.first().total.date.isNotEmpty()) {
            Toast.makeText(
                this,
                "Last Update: ${total.first().total.date}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateText(totalObject: TotalObject) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, totalObject.value)
    }

    private fun prepareViewModel(){
        // Observe the LiveData object
        viewModel.total.observe(this, {
            // Whenever the value of the LiveData object changes
            // the updateText() is called, with the new value as the parameter
            updateText(it)
        })

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    // Create and build the TotalDatabase with the name 'total-database'
    // allowMainThreadQueries() is used to allow queries to be run on the main thread
    // This is not recommended, but for simplicity it's used here
    // fallbackToDestructiveMigration() is used to handle schema changes
    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    // Initialize the value of the total from the database
    // If the database is empty, insert a new Total object with the value of 0
    // If the database is not empty, get the value of the total from the database
    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            db.totalDao().insert(Total(id = 1, total = TotalObject(0, "")))
        } else {
            viewModel.setTotal(total.first().total)
        }
    }

    // Update the value of the total in the database
    // whenever the activity is paused
    // This is done to ensure that the value of the total is always up to date
    // even if the app is closed
    override fun onPause() {
        super.onPause()
        val currentTotal = viewModel.total.value ?: TotalObject(0, "")
        val updatedTotal = TotalObject(currentTotal.value, Date().toString())
        db.totalDao().update(Total(ID, updatedTotal))
    }

    // The ID of the Total object in the database
    // For simplicity, we only have one Total object in the database
    // So the ID is always 1
    companion object {
        const val ID: Long = 1
    }
}