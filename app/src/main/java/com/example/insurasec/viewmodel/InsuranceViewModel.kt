package com.example.insurasec.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insurasec.repository.SecurityRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class InsuranceViewModel @Inject constructor(
    private val securityRepo: SecurityRepo
): ViewModel() {

    fun encryptAndUpload(name: String, phone: String, medCerLink: String) {
        viewModelScope.launch {
            securityRepo.encryptAndUpload(name, phone, medCerLink)
        }
    }

    fun getData() {
        viewModelScope.launch {
            securityRepo.getDataFromFirebase()
        }
    }
}