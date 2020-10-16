package com.example.projectmanag

import java.io.Serializable

data class user(
    val id:String=" ",
    val name:String=" ",
    val email:String= " ",
    val image:String=" ",
    val mNumber:Long=0,
    val fcmToken:String=" "
):Serializable