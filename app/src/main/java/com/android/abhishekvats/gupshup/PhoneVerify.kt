package com.android.abhishekvats.gupshup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_verify.*
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import com.google.android.gms.tasks.OnCompleteListener
import android.widget.Toast
import android.widget.EditText







class PhoneVerify : AppCompatActivity() {



    lateinit var firebaseAuth: FirebaseAuth
    lateinit var phoneCredential: PhoneAuthCredential
    lateinit var email:String
    lateinit var pass:String

    var codeSent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_verify)

        email=intent.getStringExtra("email")
        pass=intent.getStringExtra("pass")

        firebaseAuth = FirebaseAuth.getInstance()
        sendcode.isEnabled=true

       sendcode.setOnClickListener {
           sendVerificationCode()
           verify.isEnabled=true
       }
        verify.setOnClickListener {
            verifySignInCode()
        }
    }

    private fun verifySignInCode() {
        val code=code.text.toString()
        val credential = PhoneAuthProvider.getCredential(codeSent!!, code)
        signInWithPhoneAuthCredential(credential)

    }



    private fun sendVerificationCode() {

        val phone = verifynum.text.toString()

        if (phone.isEmpty()) {
            verifynum.error = "Phone number is required"
            verifynum.requestFocus()
            return
        }

        if (phone.length < 10) {
            verifynum.error = "Please enter a valid phone"
            verifynum.requestFocus()
            return
        }


        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, // Phone number to verify
                60, // Timeout duration
                TimeUnit.SECONDS, // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks)        // OnVerificationStateChangedCallbacks
    }


    var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(phoneAuthCredential)
        }

        override fun onVerificationFailed(e: FirebaseException) {

        }

        override fun onCodeSent(s: String?, forceResendingToken: PhoneAuthProvider.ForceResendingToken?) {
            super.onCodeSent(s, forceResendingToken)
            Toast.makeText(this@PhoneVerify,"Code sent",Toast.LENGTH_LONG)
            codeSent = s
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        //here you can open new activity
                        Toast.makeText(applicationContext,
                                "Login Successfull", Toast.LENGTH_LONG).show()
                        firebaseAuth.currentUser!!.linkWithCredential(credential)
                        startActivity(Intent(this, UserList::class.java))
                        finish()
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(applicationContext,
                                    "Incorrect Verification Code ", Toast.LENGTH_LONG).show()
                        }
                    }
                })
    }















}
