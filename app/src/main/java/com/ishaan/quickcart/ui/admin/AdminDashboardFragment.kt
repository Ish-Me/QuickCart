package com.ishaan.quickcart.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ishaan.quickcart.AppModule
import com.ishaan.quickcart.R
import com.ishaan.quickcart.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddProduct.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_addProductFragment)
        }

        binding.btnManageOrders.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboardFragment_to_manageOrdersFragment)
        }

        binding.btnLogout.setOnClickListener {
            AppModule.authRepository.logout()
            findNavController().navigate(R.id.action_adminDashboardFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}