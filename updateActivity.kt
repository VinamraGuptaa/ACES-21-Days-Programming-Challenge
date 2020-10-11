package com.example.notes20

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_update.*
import java.text.SimpleDateFormat
import java.util.*

class updateActivity : AppCompatActivity() {
    var notesDetails: Model? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        val date = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MMM h:mm a", Locale.getDefault())
        val mydate = sdf.format(date.time)
        img_Update.setOnClickListener {
            if (update_et_title.text.isNullOrBlank() || update_et_Body.text.isNullOrBlank()) {
                Toast.makeText(
                    applicationContext,
                    "The title and body cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (update_et_Body.length() > 500||update_et_title.length() >15) {
                Toast.makeText(
                    applicationContext,
                    "The length cannot be more than 500 words",
                    Toast.LENGTH_SHORT
                ).show()

            }
            if (notesDetails != null) {
                val noteID = notesDetails!!.id
                val Note = Model()
                if(update_et_title.text.isNullOrBlank()||update_et_Body.text.isNullOrBlank()){
                    Toast.makeText(
                        applicationContext,
                        "The title and body cannot be empty",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else if (update_et_Body.length() > 500||update_et_title.length() >15) {
                    Toast.makeText(
                        applicationContext,
                        "Oops That's out of word limit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Note.Title = update_et_title.text.toString()
                    Note.Body = update_et_Body.text.toString()
                    Note.Date = mydate
                    Note.id = noteID
                    var db = Room.databaseBuilder(applicationContext, Database::class.java, "Notes")
                        .fallbackToDestructiveMigration().allowMainThreadQueries().build()
                    db.callDao().updateNote(Note)
                    Toast.makeText(this@updateActivity, "The note is updated", Toast.LENGTH_SHORT)
                        .show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                




            }
        }

        if (intent.hasExtra(MainActivity.reqCode)) {
            notesDetails = intent.getSerializableExtra(MainActivity.reqCode) as Model
            supportActionBar?.title = "Edit Note"
            update_et_title.setText(notesDetails!!.Title)
            update_et_Body.setText(notesDetails!!.Body)
            update_tv_date.setText(mydate)

        }
    }

}