package com.example.insurasec.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.insurasec.databinding.RvInsuranceListItemBinding
import com.example.insurasec.model.Insurance

class InsuranceListAdapter(
    private val insuranceClickListener: (Insurance) -> Unit
): ListAdapter<Insurance, InsuranceListAdapter.InsuranceAdapterViewHolder>(DiffCallBack) {

    class InsuranceAdapterViewHolder(
        private val binding: RvInsuranceListItemBinding,
        private val insuranceClickListener: (Insurance) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(insurance: Insurance) {
            binding.insuranceName.text = insurance.insuranceName
            binding.insuranceCompany.text = insurance.companyName
            binding.rvInsuranceListItemParent.setOnClickListener {
                insuranceClickListener(insurance)
            }
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<Insurance>() {
        override fun areItemsTheSame(oldItem: Insurance, newItem: Insurance): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Insurance, newItem: Insurance): Boolean {
            return oldItem.insuranceName == newItem.insuranceName && oldItem.companyName == newItem.companyName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceAdapterViewHolder {
        return InsuranceAdapterViewHolder(
            RvInsuranceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            insuranceClickListener
        )
    }

    override fun onBindViewHolder(holder: InsuranceAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}