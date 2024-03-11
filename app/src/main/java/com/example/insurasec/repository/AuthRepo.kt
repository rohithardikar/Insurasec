package com.example.insurasec.repository

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.insurasec.activity.Home
import com.example.insurasec.activity.Welcome
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

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
                    
                    val passwordHash = stringToHash(password)
                    val user = hashMapOf(
                        "email" to email,
                        "password" to passwordHash
                    )

                    db.collection("users")
                        .document(email)
                        .collection("user_info")
                        .document("signup_data")
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

    private fun stringToHash(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        // Convert the byte array to a hexadecimal string
        return digest.joinToString("") { "%02x".format(it) }
    }
}