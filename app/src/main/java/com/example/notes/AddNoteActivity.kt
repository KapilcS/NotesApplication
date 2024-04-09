package com.example.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notes.databinding.ActivityAddNoteBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val content = binding.contentEditText.text.toString()

            databaseReference = FirebaseDatabase.getInstance().getReference("Notes Information")

            // Generate a unique key for each record
            val noteId = databaseReference.push().key

            // Check if the key is null, handle error if necessary
            noteId?.let {
                val notesData = NotesData(it, title, content)
                databaseReference.child(it).setValue(notesData).addOnSuccessListener {
                    binding.titleEditText.text.clear()
                    binding.contentEditText.text.clear()

                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, UserlistActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "Failed to generate unique ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
