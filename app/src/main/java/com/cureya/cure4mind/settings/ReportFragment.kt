package com.cureya.cure4mind.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cureya.cure4mind.databinding.FragmentSettingsReportBinding

class ReportFragment : Fragment() {

    private lateinit var binding: FragmentSettingsReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsReportBinding.inflate(inflater, container, false)
        return binding.root
    }
}