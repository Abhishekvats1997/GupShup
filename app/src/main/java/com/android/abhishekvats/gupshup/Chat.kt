package com.android.abhishekvats.gupshup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.ArrayList
import com.stfalcon.chatkit.messages.MessageInput
import android.view.MenuItem
import android.widget.Toast
import com.tomash.androidcontacts.contactgetter.entity.ContactData
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder
import android.R.attr.data
import android.app.Activity


class Chat : AppCompatActivity() {

    lateinit var number:String
    lateinit var myNumber:String

    var list=ArrayList<ChatMessage>()
    var map= hashMapOf<Long,ChatMessage>()

    lateinit var firebaseAuth: FirebaseAuth
    lateinit var firebaseUser: FirebaseUser
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var sendermessageCounter=0
    lateinit var readPathReference:DatabaseReference
    lateinit var sendPathReference:DatabaseReference

    var firstReading=true
    var GET_CONTACT_NAME=1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        number=intent.getStringExtra("number")
        supportActionBar!!.title=number
        supportActionBar!!.subtitle="Online"
        setupFirebase()


        var layoutManager=LinearLayoutManager(this)
        layoutManager.stackFromEnd=true
        chatView.layoutManager=layoutManager
        chatView.adapter=MyAdapter(list)

        Log.i("detect","I am called")

        getSentHistory()
        getMessages()

        input.setInputListener(MessageInput.InputListener {
            //validate and send message
            list.add(ChatMessage(it.toString(),System.currentTimeMillis(),ChatMessage.SENT))
            chatView.adapter.notifyDataSetChanged()
            chatView.scrollToPosition(list.size-1)
            sendMessage(it.toString())
            true
        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if(requestCode==GET_CONTACT_NAME && resultCode== Activity.RESULT_OK){
             var newContact= data!!.data
             val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

             val cursor = applicationContext.contentResolver.query(newContact, projection, null, null, null)
             cursor!!.moveToFirst()

             val nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
             val name = cursor.getString(nameColumnIndex)
             cursor.close()
             supportActionBar!!.title=name
         }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.action_settings-> {
                val intent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                    // Sets the MIME type to match the Contacts Provider
                    type = ContactsContract.RawContacts.CONTENT_TYPE
                    putExtra(ContactsContract.Intents.Insert.PHONE, number)
                    putExtra("finishActivityOnSaveCompleted", true)
                }
//                startActivity(intent)
                startActivityForResult(intent,GET_CONTACT_NAME)
                true
            }
            R.id.sign_out-> {
                firebaseAuth.signOut()
                startActivity(Intent(this,Login::class.java))
                Toast.makeText(this,"Signed Out",Toast.LENGTH_LONG)
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun getSentHistory() {
        sendPathReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for(datasnap:DataSnapshot in dataSnapshot.children){
                        if(datasnap.key=="Counter")
                            sendermessageCounter=datasnap.value.toString().toInt()
                        else{
                            for(data:DataSnapshot in datasnap.children) {
                                var text: String = data.key
                                var time: Long = data.value.toString().toLong()
                                Log.i("Sent",text)
                                map[time]= ChatMessage(text,time,ChatMessage.SENT)
                            }
                        }
                    }
                }

            }
            override fun onCancelled(p0: DatabaseError?) {
            }
        })
    }
    fun sortandAdd(){
        var sorted=map.toSortedMap()
        for(i in sorted.values){
            Log.i("map",i.text)
        }
        list.addAll(ArrayList(sorted.values))
        chatView.adapter.notifyDataSetChanged()
        firstReading=false
    }
    fun getMessages(){
        Log.i("test","New Listener called")
        readPathReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
            }
            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }
            override fun onChildAdded(datasnap: DataSnapshot?, p1: String?) {
                if(datasnap!!.key!="Counter") {
                    for(data:DataSnapshot in datasnap.children) {
                        var text: String = data.key
                        var time: Long = data.value.toString().toLong()
                        Log.i("new",text)
                        if(firstReading==true){
                            map[time]=(ChatMessage(text,time,ChatMessage.RECEIVED))
                        }
                        else{
                            list.add(ChatMessage(text,time,ChatMessage.RECEIVED))
                            chatView.adapter.notifyDataSetChanged()
                            chatView.scrollToPosition(list.size-1)
                        }

                    }
                }
            }
            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        })
        readPathReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                sortandAdd()
            }

        })
    }
    fun sendMessage(input:CharSequence){
        sendermessageCounter++
        var sentTime=System.currentTimeMillis()
        databaseReference.child(number).child(myNumber).child("Message"+sendermessageCounter.toString()).child(input.toString()).setValue(sentTime)
        databaseReference.child(number).child(myNumber).child("Counter").setValue(sendermessageCounter)
    }
    fun setupFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("GupShup")
        firebaseAuth= FirebaseAuth.getInstance()
        firebaseUser= firebaseAuth.currentUser!!
        myNumber= firebaseUser.phoneNumber!!.substring(3)
        readPathReference=databaseReference.child(myNumber).child(number)
        sendPathReference=databaseReference.child(number).child(myNumber)

    }

}

