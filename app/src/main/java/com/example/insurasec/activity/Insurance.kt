package com.example.insurasec.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.example.insurasec.R
import com.example.insurasec.databinding.ActivityInsuranceBinding
import com.example.insurasec.viewmodel.InsuranceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Insurance : AppCompatActivity() {

    private lateinit var binding: ActivityInsuranceBinding
    private val insuranceViewModel: InsuranceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsuranceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            val name = binding.tietName.text.toString()
            val phone = binding.tietPhone.text.toString()
            val medCerLink = binding.tietMedicalCertificate.text.toString()

            insuranceViewModel.encryptAndUpload(name, phone, medCerLink)
        }

        binding.btnGetData.setOnClickListener {
            insuranceViewModel.getData()
        }
    }
}