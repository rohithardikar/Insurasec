package com.example.insurasec.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.insurasec.adapter.InsuranceListAdapter
import com.example.insurasec.databinding.ActivityHomeBinding
import com.example.insurasec.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = homeViewModel.getUsername()
        if (!username.isNullOrBlank()) {
            binding.tvWelcome.text = "Welcome, $username!"
        }

        binding.tvSignout.setOnClickListener {
            homeViewModel.signout()
        }

        val insuranceListAdapter = InsuranceListAdapter(
            insuranceClickListener = {
                val intent = Intent(this, Insurance::class.java)
                intent.putExtra("scheduleId", it.insuranceName)
                intent.putExtra("scheduleName", it.companyName)
                startActivity(intent)
            }
        )

        binding.rvInsuranceList.adapter = insuranceListAdapter
        val insuranceList = homeViewModel.getInsuranceList()
        if (insuranceList.isEmpty()) {
            binding.tvEmptyInsuranceList.visibility = View.VISIBLE
            insuranceListAdapter.submitList(emptyList())
        } else {
            binding.tvEmptyInsuranceList.visibility = View.GONE
        }
        insuranceListAdapter.submitList(insuranceList)
    }
}