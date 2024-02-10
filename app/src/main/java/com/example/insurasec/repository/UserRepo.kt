package com.example.insurasec.repository

import android.content.Context
import android.widget.Toast
import com.example.insurasec.model.Insurance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserRepo {

    fun getInsuranceList(): List<Insurance> {
        return listOf<Insurance>(
            Insurance(1, "SBI Life Insurance", "SBI Bank"),
            Insurance(2, "HDFC Health Insurance", "HDFC Bank"),
            Insurance(3, "Gold Life Insurance", "Axis Bank")
        )
    }
}