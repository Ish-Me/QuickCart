package com.ishaan.quickcart.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.data.model.OrderStatus
import com.ishaan.quickcart.databinding.ItemAdminOrderBinding

class AdminOrderAdapter(
    private val onStatusUpdate: (Order, String) -> Unit
) : ListAdapter<Order, AdminOrderAdapter.AdminOrderViewHolder>(DiffCallback()) {

    inner class AdminOrderViewHolder(private val binding: ItemAdminOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.tvCustomerName.text = order.userName
            binding.tvTotal.text = "₹${order.totalAmount}"
            binding.tvAddress.text = order.userAddress
            binding.tvItemCount.text = "${order.itemCount} item(s)"

            val statuses = OrderStatus.values().map { it.name }
            val spinnerAdapter = ArrayAdapter(
                binding.root.context,
                android.R.layout.simple_spinner_item,
                statuses
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

            binding.spinnerStatus.adapter = spinnerAdapter
            val currentIndex = statuses.indexOf(order.status)
            if (currentIndex >= 0) binding.spinnerStatus.setSelection(currentIndex)

            binding.btnUpdateStatus.setOnClickListener {
                val selectedStatus = binding.spinnerStatus.selectedItem.toString()
                onStatusUpdate(order, selectedStatus)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val binding = ItemAdminOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AdminOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) =
            oldItem.orderId == newItem.orderId
        override fun areContentsTheSame(oldItem: Order, newItem: Order) =
            oldItem == newItem
    }
}