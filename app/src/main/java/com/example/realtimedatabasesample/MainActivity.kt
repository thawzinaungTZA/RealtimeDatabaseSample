package com.example.realtimedatabasesample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var databaseRef: DatabaseReference
    private lateinit var messageRef: DatabaseReference
    private lateinit var userRef: DatabaseReference
    private var userIdKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        messageRef = FirebaseDatabase.getInstance().getReference("message")
        databaseRef = FirebaseDatabase.getInstance().reference
        userRef = FirebaseDatabase.getInstance().getReference("users")//.child(userId)

        btnSaveMessage.setOnClickListener {
            writeMessage()
        }
        addValueListenerForMessage()

        btnSaveUser.setOnClickListener {
            if (edtUserName.text.isBlank()) {
                edtUserName.error = "Enter username"
                return@setOnClickListener
            }
            if (edtEmail.text.isBlank()) {
                edtEmail.error = "Enter email"
                return@setOnClickListener
            }
            userIdKey = databaseRef.push().key ?: edtEmail.text.toString()
            writeNewUser(userIdKey, edtUserName.text.toString(), edtEmail.text.toString())
        }
        addValueListenerForUser()
    }

    private fun writeMessage() {
        if (edtMessage.text.isBlank()) {
            edtMessage.error = "Enter message"
            return
        }
        val message = edtMessage.text.toString()
        messageRef.setValue(message)
        edtMessage.text.clear()
    }

    private fun addValueListenerForMessage() {
        // Read from the database
        messageRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                txtMessage.text = value
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                txtMessage.text = getString(R.string.failed_to_read)
                Log.d(TAG, "Failed to read message value.", error.toException())
            }
        })
    }

    private fun writeNewUser(userId: String, name: String, email: String?) {
        val user = User(name, email)
        databaseRef.child("users").child(userId).setValue(user)
    }

    private fun addValueListenerForUser() {
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                txtUser.text = getString(R.string.failed_to_read)
                Log.d(TAG, "Failed to read user value.", error.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                /*val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    txtUser.text = "UserName: ${user.username}, Email: ${user.email}"
                }*/

                val usersStr = StringBuffer("")
                for (users in dataSnapshot.children) {
                    val user = users.getValue(User::class.java)
                    user?.let {
                        usersStr.append("UserName: ${user.username}, Email: ${user.email}\n")
                    }
                }
                txtUser.text = usersStr
            }

        })
    }
}
