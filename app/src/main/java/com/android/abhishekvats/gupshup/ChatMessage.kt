package com.android.abhishekvats.gupshup

public class ChatMessage(text:String,time:Long,type:Int){
    var text=text
    var time=time
    var type=type
    companion object{
        final var SENT=0
        final var RECEIVED=1
    }

}