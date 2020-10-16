package com.example.projectmanag

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(Activity:SignUpActivity,UserInfo:user){
         mFireStore.collection("USERS").document(getCurrentUserId()).set(UserInfo, SetOptions.merge()).addOnSuccessListener {
             Activity.UserRegisterSucess()
         }
    }
    fun getCurrentUserId():String {
        var currentUser= FirebaseAuth.getInstance().currentUser
        var currentUserId=" "
        if(currentUser!=null){
            return currentUser.uid
        }
        else{
            return currentUserId
        }

    }
     fun signInUser(Activity: Sign_in_Activity){
         mFireStore.collection("USERS").document(getCurrentUserId()).get().addOnSuccessListener {
             document->
             val loggedinUser=document.toObject(user::class.java)!!
             Activity.signedInUser(loggedinUser)
         }



     }





}