package com.example.insurasec.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.insurasec.R
import com.example.insurasec.adapter.InsuranceHistoryListAdapter
import com.example.insurasec.adapter.InsuranceListAdapter
import com.example.insurasec.databinding.ActivityHomeBinding
import com.example.insurasec.databinding.ActivityInsuranceHistoryBinding
import com.example.insurasec.viewmodel.HomeViewModel
import com.example.insurasec.viewmodel.InsuranceHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsuranceHistory : AppCompatActivity() {

    private lateinit var binding: ActivityInsuranceHistoryBinding
    private val insuranceHistoryViewModel: InsuranceHistoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsuranceHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insuranceHistoryListAdapter = InsuranceHistoryListAdapter(
            insuranceHistoryListItemClickListener = {}
        )

        binding.rvInsuranceHistory.adapter = insuranceHistoryListAdapter
        val insuranceList = insuranceHistoryViewModel.getInsuranceHistory()
        if (insuranceList.isEmpty()) {
            binding.tvEmptyInsuranceHistory.visibility = View.VISIBLE
            insuranceHistoryListAdapter.submitList(emptyList())
        } else {
            binding.tvEmptyInsuranceHistory.visibility = View.GONE
        }
        insuranceHistoryListAdapter.submitList(insuranceList)
    }
}