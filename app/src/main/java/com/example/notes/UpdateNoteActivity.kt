package com.example.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class UpdateNoteActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var noteId: String
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var saveButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        noteId = intent.getStringExtra("noteId") ?: ""
        val noteTitle = intent.getStringExtra("noteTitle") ?: ""
        val noteContent = intent.getStringExtra("noteContent") ?: ""

        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        saveButton = findViewById(R.id.saveButton)

        titleEditText.setText(noteTitle)
        contentEditText.setText(noteContent)

        saveButton.setOnClickListener {
            updateNote()
        }
    }

    private fun updateNote() {
        val updatedTitle = titleEditText.text.toString().trim()
        val updatedContent = contentEditText.text.toString().trim()

        if (updatedTitle.isNotEmpty() || updatedContent.isNotEmpty()) {
            dbRef = FirebaseDatabase.getInstance().getReference("Notes Information").child(noteId)

            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val noteToUpdate = snapshot.getValue(NotesData::class.java)

                    noteToUpdate?.let {
                        if (updatedTitle.isNotEmpty()) {
                            it.title = updatedTitle
                        }
                        if (updatedContent.isNotEmpty()) {
                            it.content = updatedContent
                        }

                        dbRef.setValue(it)
                            .addOnSuccessListener {
                                Toast.makeText(this@UpdateNoteActivity, "Note updated successfully", Toast.LENGTH_SHORT).show()

                                // Pass the updated data back to UserlistActivity
                                val intent = Intent().apply {
                                    putExtra("updatedNoteId", noteId)
                                    putExtra("updatedNoteTitle", updatedTitle)
                                    putExtra("updatedNoteContent", updatedContent)
                                }
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@UpdateNoteActivity, "Failed to update note", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle onCancelled event
                }
            })
        } else {
            Toast.makeText(this, "Please update at least one field", Toast.LENGTH_SHORT).show()
        }
    }
}
