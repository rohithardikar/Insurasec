package com.example.insurasec.repository

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.insurasec.activity.Home
import com.example.insurasec.activity.Welcome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepo(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun login(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(context, Home::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun signup(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val user = hashMapOf(
                        "email" to email,
                        "password" to password
                    )

                    db.collection("users")
                        .document(email)
                        .set(user)
                        .addOnSuccessListener {
                            val intent = Intent(context, Home::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Signup Failed!", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Signup Failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun signout() {
        try {auth.signOut()}
        catch (e: Exception) {
            return
        }
        val intent = Intent(context, Welcome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun getUsername(): String? {
        return auth.currentUser?.displayName
    }
}