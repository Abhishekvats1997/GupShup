package com.android.abhishekvats.gupshup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*



class Login : AppCompatActivity() {

    lateinit var Email:String
    lateinit var Password:String
    lateinit var firebaseUser : FirebaseUser    // Current User --> unique key --> Information
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
//        hideSystemUI()


        firebaseAuth= FirebaseAuth.getInstance()

        signup_text.setOnClickListener {
            startActivity(Intent(this,Register::class.java))
            finish()
        }
        login.setOnClickListener {

                Email = email.text.toString()

                Password = password.text.toString()

                firebaseAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(this@Login) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this@Login, "Authentication successful.",
                                        Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@Login, UserList::class.java))
                            } else {
                                Toast.makeText(this@Login, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                }
        }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}
