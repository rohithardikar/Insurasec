package com.example.insurasec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insurasec.model.InsuranceHistoryItem
import com.example.insurasec.repository.UserRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsuranceHistoryViewModel @Inject constructor(
    private val userRepo: UserRepo
) : ViewModel() {

    fun getInsuranceHistory(): List<InsuranceHistoryItem> {
        return userRepo.getInsuranceHistory()
    }
}