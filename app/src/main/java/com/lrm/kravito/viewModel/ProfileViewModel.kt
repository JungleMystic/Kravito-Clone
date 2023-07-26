package com.lrm.kravito.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lrm.kravito.constants.USERS_COLLECTION
import com.lrm.kravito.model.User

class ProfileViewModel:ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var currentUserNumber: String
    lateinit var currentUserUid: String

    fun getUserProfile() {
        auth = FirebaseAuth.getInstance()
        db = Firebase.firestore

        currentUserNumber = auth.currentUser?.phoneNumber!!
        currentUserUid = auth.currentUser?.uid!!
        val userRef = db.collection(USERS_COLLECTION).document(currentUserNumber)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            val user = documentSnapshot.toObject(User::class.java)
            setUserProfile(user!!)
        }
            .addOnFailureListener { Log.i("MyLogMessages", "getUserProfile: User not found") }
    }

    fun setUserProfile(user: User?) {
        Log.i("MyLogMessages", "setUserProfile: $user")
        _userProfile.value = user
    }
}