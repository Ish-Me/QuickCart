package com.ishaan.quickcart.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.R
import com.ishaan.quickcart.data.model.User
import com.ishaan.quickcart.databinding.FragmentCartBinding
import com.ishaan.quickcart.ui.orders.OrderViewModel
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeCart()
        observeTotal()
        observePlaceOrder()
        fetchCurrentUser()

        binding.btnPlaceOrder.setOnClickListener {
            val user = currentUser
            if (user == null) {
                Toast.makeText(requireContext(), "User not loaded yet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val note = binding.etNote.text.toString()
            val items = cartAdapter.currentList
            orderViewModel.placeOrder(user, items, note)
        }
    }

    private fun fetchCurrentUser() {
        viewLifecycleOwner.lifecycleScope.launch {
            val uid = AppModule.authRepository.currentUser?.uid ?: return@launch
            val result = AppModule.authUseCase.getUserProfile(uid)
            if (result is Resource.Success) {
                currentUser = result.data
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrease = { cartViewModel.increaseQuantity(it) },
            onDecrease = { cartViewModel.decreaseQuantity(it) },
            onDelete = { cartViewModel.removeItem(it) }
        )
        binding.rvCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartItems.collect { items ->
                cartAdapter.submitList(items)
                if (items.isEmpty()) {
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvCart.visibility = View.GONE
                    binding.layoutCheckout.visibility = View.GONE
                } else {
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvCart.visibility = View.VISIBLE
                    binding.layoutCheckout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observeTotal() {
        viewLifecycleOwner.lifecycleScope.launch {
            cartViewModel.cartTotal.collect { total ->
                binding.tvTotal.text = "₹%.2f".format(total)
            }
        }
    }

    private fun observePlaceOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            orderViewModel.placeOrderState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnPlaceOrder.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnPlaceOrder.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Order placed successfully! 🎉",
                            Toast.LENGTH_LONG
                        ).show()
                        orderViewModel.resetPlaceOrderState()
                        findNavController().navigate(R.id.action_cartFragment_to_orderFragment)
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnPlaceOrder.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        orderViewModel.resetPlaceOrderState()
                    }
                    null -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}