package com.example.insurasec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insurasec.repository.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: AuthRepo
): ViewModel() {

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepo.login(email, password)
        }
    }

    fun signup(email: String, password: String) {
        viewModelScope.launch {
            authRepo.signup(email, password)
        }
    }

    fun isLoggedIn(): Boolean {
        return authRepo.isLoggedIn()
    }
}