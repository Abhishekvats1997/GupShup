package com.android.abhishekvats.gupshup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class Register : AppCompatActivity() {

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    lateinit var firebaseUser : FirebaseUser    // Current User --> unique key --> Information

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth=FirebaseAuth.getInstance()
        firebaseDatabase= FirebaseDatabase.getInstance()
        databaseReference=firebaseDatabase.getReference("GupShup")


        login.setOnClickListener {

            lateinit var Email:String
            lateinit var Password:String
            lateinit var Number:String

            Email = email.text.toString() // email --> @--> .com
            Password = password.text.toString() // min 6 characters
            Number = number.text.toString()



            when {

                Email.isBlank() -> email.error = "Email Can't be Blank"
                Number.isBlank() -> number.error = "Number Can't be Blank"
                Number.length!=10 -> number.error = "Invalid Number"
                Password.isBlank() -> password.error = "Password Can't be Blank"
                Password.length < 6 -> password.error = "Min 6 characters are required"
                Password!=password2.text.toString()-> password2.error = "Passwords do not match!"
                else -> {

                    // Register user on App
                    // Predefined Method --> FirebaseAuth
                    firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener{ task ->
                                if (task.isSuccessful) {
                                    firebaseUser = firebaseAuth.currentUser!!

                                    databaseReference.child(Number).child("Email").setValue(Email)

                                    Toast.makeText(this@Register, "Authentication successful.",
                                            Toast.LENGTH_SHORT).show()
                                    var intent=Intent(this,PhoneVerify::class.java)
                                    intent.putExtra("email",Email)
                                    intent.putExtra("pass",Password)
                                    startActivity(intent)
                                    finish()


                                } else {
                                    Toast.makeText(this@Register, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show()
                                }
                            }
                }
            }
        }

        login_text.setOnClickListener {
            startActivity(Intent(this,Login::class.java))
            finish()
        }
    }
}
