package com.ishaan.quickcart.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ishaan.quickcart.data.model.Order
import com.ishaan.quickcart.databinding.ItemOrderBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter : ListAdapter<Order, OrderAdapter.OrderViewHolder>(DiffCallback()) {

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.tvOrderId.text = "Order #${order.orderId.takeLast(6).uppercase()}"
            binding.tvStatus.text = order.status
            binding.tvItemCount.text = "${order.itemCount} item(s)"
            binding.tvTotal.text = "₹${order.totalAmount}"
            binding.tvDate.text = SimpleDateFormat(
                "dd MMM yyyy", Locale.getDefault()
            ).format(Date(order.timestamp))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order) =
            oldItem.orderId == newItem.orderId
        override fun areContentsTheSame(oldItem: Order, newItem: Order) =
            oldItem == newItem
    }
}