package com.ishaan.quickcart.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.ishaan.quickcart.databinding.FragmentProductDetailBinding
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.launch

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductDetailViewModel by viewModels()
    private val args: ProductDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadProduct(args.productId)

        binding.btnIncrement.setOnClickListener { viewModel.incrementQuantity() }
        binding.btnDecrement.setOnClickListener { viewModel.decrementQuantity() }
        binding.btnAddToCart.setOnClickListener {
            viewModel.productState.value.let { state ->
                if (state is Resource.Success) {
                    viewModel.addToCart(state.data)
                }
            }
        }

        observeProduct()
        observeQuantity()
        observeAddToCart()
    }

    private fun observeProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.productState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val product = state.data
                        binding.tvProductName.text = product.name
                        binding.tvDescription.text = product.description
                        binding.tvPrice.text = "₹${product.price}"
                        binding.tvUnit.text = "/ ${product.unit}"
                        binding.tvCategory.text = product.category
                        binding.ivProduct.load(product.imageUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                        }
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                }
            }
        }
    }

    private fun observeQuantity() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.quantity.collect { qty ->
                binding.tvQuantity.text = qty.toString()
            }
        }
    }

    private fun observeAddToCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addToCartState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.btnAddToCart.isEnabled = false
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.btnAddToCart.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
                        viewModel.resetAddToCartState()
                    }
                    is Resource.Error -> {
                        binding.btnAddToCart.isEnabled = true
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetAddToCartState()
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