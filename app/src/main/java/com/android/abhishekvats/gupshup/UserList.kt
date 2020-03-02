package com.android.abhishekvats.gupshup

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_user_list.*


class UserList : AppCompatActivity() {

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)



        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser= firebaseAuth.currentUser!!

        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("GupShup")

        go.setOnClickListener {

            number = findnumber.text.toString()

            databaseReference.child(number).addValueEventListener(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        var intent = Intent(this@UserList, Chat::class.java)
                        intent.putExtra("number",number)
                        startActivity(intent)
                        finish()
                    } else {
                        findnumber.error = " User does not Exist"
                    }
                }

                override fun onCancelled(p0: DatabaseError?) {
                }

            })
        }

    }

}

