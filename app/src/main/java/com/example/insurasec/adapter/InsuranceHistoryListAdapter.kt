package com.example.insurasec.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insurasec.databinding.InsuranceHistoryListItemBinding
import com.example.insurasec.databinding.RvInsuranceListItemBinding
import com.example.insurasec.model.Insurance
import com.example.insurasec.model.InsuranceHistoryItem

class InsuranceHistoryListAdapter(
    private val insuranceHistoryListItemClickListener: (InsuranceHistoryItem) -> Unit
): ListAdapter<InsuranceHistoryItem, InsuranceHistoryListAdapter.InsuranceHistoryAdapterViewHolder>(DiffCallBack) {

    class InsuranceHistoryAdapterViewHolder(
        private val binding: InsuranceHistoryListItemBinding,
        private val insuranceHistoryListItemClickListener: (InsuranceHistoryItem) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(insuranceHistoryItem: InsuranceHistoryItem) {
            binding.tvInsuranceName.text = insuranceHistoryItem.insuranceName
            binding.tvCompanyName.text = insuranceHistoryItem.companyName
            binding.tvInsuranceStatus.text = insuranceHistoryItem.insuranceStatus
            binding.rvInsuranceHistoryListItemParent.setOnClickListener {
                insuranceHistoryListItemClickListener(insuranceHistoryItem)
            }
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<InsuranceHistoryItem>() {
        override fun areItemsTheSame(oldItem: InsuranceHistoryItem, newItem: InsuranceHistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InsuranceHistoryItem, newItem: InsuranceHistoryItem): Boolean {
            return oldItem.insuranceName == newItem.insuranceName && oldItem.companyName == newItem.companyName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceHistoryAdapterViewHolder {
        return InsuranceHistoryAdapterViewHolder(
            InsuranceHistoryListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            insuranceHistoryListItemClickListener
        )
    }

    override fun onBindViewHolder(holder: InsuranceHistoryAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}