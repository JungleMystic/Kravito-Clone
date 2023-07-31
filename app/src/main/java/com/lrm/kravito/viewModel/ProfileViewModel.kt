package com.lrm.kravito.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lrm.kravito.constants.LOG_DATA
import com.lrm.kravito.constants.USERS_COLLECTION
import com.lrm.kravito.model.User

class ProfileViewModel:ViewModel() {

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _userLocation = MutableLiveData("Loading Location...")
    val userLocation: LiveData<String> get() = _userLocation

    private val _internet = MutableLiveData<Boolean>()
    val internet: LiveData<Boolean> get() = _internet

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
            .addOnFailureListener { Log.i(LOG_DATA, "getUserProfile: User not found") }
    }

    fun setUserProfile(user: User?) {
        Log.i(LOG_DATA, "setUserProfile: $user")
        _userProfile.value = user
    }

    fun setUserLocation(location: String) {
        _userLocation.value = location
    }

    fun setInternetConnectionStatus(status: Boolean) {
        _internet.postValue(status)
        Log.i(LOG_DATA, "ProfileViewModel: setInternetConnectionStatus is called -> Status = $status")
    }
}