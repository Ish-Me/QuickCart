package com.ishaan.quickcart.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ishaan.quickcart.R
import com.ishaan.quickcart.databinding.FragmentHomeBinding
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        setupCategoryChips()
        observeProducts()
        observeCartState()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                val action = HomeFragmentDirections
                    .actionHomeFragmentToProductDetailFragment(product.id)
                findNavController().navigate(action)
            },
            onAddToCart = { product ->
                viewModel.addToCart(product)
            }
        )
        binding.rvProducts.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            if (text.toString().isBlank()) {
                viewModel.fetchProducts()
            } else {
                viewModel.searchProducts(text.toString())
            }
        }
    }

    private fun setupCategoryChips() {
        binding.chipAll.setOnClickListener { viewModel.filterByCategory("") }
        binding.chipFruits.setOnClickListener { viewModel.filterByCategory("Fruits") }
        binding.chipVegetables.setOnClickListener { viewModel.filterByCategory("Vegetables") }
        binding.chipDairy.setOnClickListener { viewModel.filterByCategory("Dairy") }
        binding.chipBakery.setOnClickListener { viewModel.filterByCategory("Bakery") }
        binding.chipBeverages.setOnClickListener { viewModel.filterByCategory("Beverages") }
        binding.chipSnacks.setOnClickListener { viewModel.filterByCategory("Snacks") }
        binding.chipHousehold.setOnClickListener { viewModel.filterByCategory("Household") }
    }

    private fun observeProducts() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productsState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.rvProducts.visibility = View.GONE
                        binding.tvEmpty.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (state.data.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvProducts.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvProducts.visibility = View.VISIBLE
                            productAdapter.submitList(state.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.rvProducts.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun observeCartState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
                        viewModel.resetAddToCartState()
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetAddToCartState()
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