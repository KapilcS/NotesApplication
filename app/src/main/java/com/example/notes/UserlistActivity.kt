package com.example.notes
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class UserlistActivity : AppCompatActivity(), MyAdapter.OnDeleteClickListener, MyAdapter.OnUpdateClickListener {
    private lateinit var dbref: DatabaseReference
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userArrayList: ArrayList<NotesData>
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userlist)

        userRecyclerView = findViewById(R.id.userList)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.setHasFixedSize(true)

        userArrayList = arrayListOf()
        adapter = MyAdapter(userArrayList, this, this)
        userRecyclerView.adapter = adapter

        val addButton: FloatingActionButton = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        getUserData()
    }

    override fun onDeleteClick(position: Int) {
        val noteIdToDelete = userArrayList[position].id
        deleteData(noteIdToDelete)
    }



    private fun deleteData(noteIdToDelete: String) {
        dbref = FirebaseDatabase.getInstance().getReference("Notes Information").child(noteIdToDelete)
        dbref.removeValue()
            .addOnSuccessListener {
                userArrayList.removeAll { it.id == noteIdToDelete }
                // Call recreate() method to refresh the activity

                adapter.notifyDataSetChanged()
                recreate()

                Toast.makeText(this, "Data deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getUserData() {
        dbref = FirebaseDatabase.getInstance().getReference("Notes Information")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(NotesData::class.java)
                        userArrayList.add(user!!)
                    }

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled event
            }
        })
    }

    override fun onUpdateClick(position: Int) {
        val noteId = userArrayList[position].id
        val noteTitle = userArrayList[position].title
        val noteContent = userArrayList[position].content

        val intent = Intent(this, UpdateNoteActivity::class.java).apply {
            putExtra("noteId", noteId)
            putExtra("noteTitle", noteTitle)
            putExtra("noteContent", noteContent)
        }
        startActivityForResult(intent, UPDATE_NOTE_REQUEST)
    }

    companion object {
        const val UPDATE_NOTE_REQUEST = 1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPDATE_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let {
                val updatedNoteId = it.getStringExtra("updatedNoteId")
                val updatedNoteTitle = it.getStringExtra("updatedNoteTitle")
                val updatedNoteContent = it.getStringExtra("updatedNoteContent")

                for (i in 0 until userArrayList.size) {
                    if (userArrayList[i].id == updatedNoteId) {
                        userArrayList[i].title = updatedNoteTitle ?: ""
                        userArrayList[i].content = updatedNoteContent ?: ""
                        adapter.notifyItemChanged(i)
                        break
                    }
                }
                recreate()

                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
