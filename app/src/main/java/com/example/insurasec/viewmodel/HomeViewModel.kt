package com.example.insurasec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insurasec.model.Insurance
import com.example.insurasec.repository.AuthRepo
import com.example.insurasec.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
): ViewModel() {

    fun signout() {
        viewModelScope.launch {
            authRepo.signout()
        }
    }

    fun getUsername(): String? {
        return authRepo.getUsername()
    }

    fun getInsuranceList(): List<Insurance> {
        return userRepo.getInsuranceList()
    }
}