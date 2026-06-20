package com.ishaan.quickcart.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ishaan.quickcart.databinding.FragmentManageOrdersBinding
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.launch

class ManageOrdersFragment : Fragment() {

    private var _binding: FragmentManageOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adminOrderAdapter: AdminOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        viewModel.fetchAllOrders()
        observeOrders()
        observeUpdateStatus()
    }

    private fun setupRecyclerView() {
        adminOrderAdapter = AdminOrderAdapter { order, newStatus ->
            viewModel.updateOrderStatus(order.orderId, newStatus)
        }
        binding.rvOrders.apply {
            adapter = adminOrderAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ordersState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rvOrders.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvOrders.visibility = View.VISIBLE
                        adminOrderAdapter.submitList(state.data)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeUpdateStatus() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateOrderState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Order status updated!",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetUpdateOrderState()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetUpdateOrderState()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}