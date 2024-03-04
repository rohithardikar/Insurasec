package com.example.insurasec.repository

import android.content.Context
import android.widget.Toast
import com.example.insurasec.model.Insurance
import com.example.insurasec.model.InsuranceHistoryItem
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

    fun getInsuranceHistory(): List<InsuranceHistoryItem> {
        return listOf(
            InsuranceHistoryItem(1, "Insurance: SBI Life Insurance", "Bank: SBI Bank", "Status: Request Pending"),
            InsuranceHistoryItem(2, "Insurance: HDFC Health Insurance", "Bank: HDFC Bank", "Status: Applied Successfully"),
            InsuranceHistoryItem(3, "Insurance: Gold Life Insurance", "Bank: Axis Bank", "Status: Insurance Claimed")
        )
    }
}