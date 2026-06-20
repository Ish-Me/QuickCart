package com.ishaan.quickcart.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ishaan.quickcart.data.model.Product
import com.ishaan.quickcart.databinding.FragmentAddProductBinding
import com.ishaan.quickcart.utils.Resource
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // AI suggest category button
        binding.btnSuggestCategory.setOnClickListener {
            val name = binding.etName.text.toString()
            val description = binding.etDescription.text.toString()
            if (name.isBlank() || description.isBlank()) {
                Toast.makeText(
                    requireContext(),
                    "Enter name and description first",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.suggestCategory(name, description)
        }

        binding.btnAddProduct.setOnClickListener {
            val name = binding.etName.text.toString()
            val description = binding.etDescription.text.toString()
            val category = binding.etCategory.text.toString()
            val priceStr = binding.etPrice.text.toString()
            val unit = binding.etUnit.text.toString()
            val stockStr = binding.etStock.text.toString()
            val imageUrl = binding.etImageUrl.text.toString()

            if (name.isBlank() || description.isBlank() || category.isBlank() ||
                priceStr.isBlank() || unit.isBlank() || stockStr.isBlank()
            ) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val product = Product(
                name = name,
                description = description,
                category = category,
                price = priceStr.toDoubleOrNull() ?: 0.0,
                unit = unit,
                stock = stockStr.toIntOrNull() ?: 0,
                imageUrl = imageUrl,
                isAvailable = true
            )
            viewModel.addProduct(product)
        }

        observeAISuggestion()
        observeAddProduct()
    }

    private fun observeAISuggestion() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.suggestedCategory.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressAI.visibility = View.VISIBLE
                        binding.btnSuggestCategory.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.progressAI.visibility = View.GONE
                        binding.btnSuggestCategory.isEnabled = true
                        binding.etCategory.setText(state.data)
                        Toast.makeText(
                            requireContext(),
                            "AI suggested: ${state.data}",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetSuggestedCategory()
                    }
                    is Resource.Error -> {
                        binding.progressAI.visibility = View.GONE
                        binding.btnSuggestCategory.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetSuggestedCategory()
                    }
                    null -> {}
                }
            }
        }
    }

    private fun observeAddProduct() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.addProductState.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnAddProduct.isEnabled = false
                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnAddProduct.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            "Product added successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.resetAddProductState()
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnAddProduct.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        viewModel.resetAddProductState()
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