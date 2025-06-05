package com.project.nasalibrary.ui.noInternetDialogFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.project.nasalibrary.databinding.FragmentNoInternetDialogueBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoInternetDialogFragment : DialogFragment() {

    private var _binding: FragmentNoInternetDialogueBinding? = null
    private val binding get() = _binding!!


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoInternetDialogueBinding.inflate(inflater, container, false)
        isCancelable = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tryAgainButton.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf(BUNDLE_KEY to true))
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "NoInternetDialog"
        const val REQUEST_KEY = "NoInternetDialogRequest"
        const val BUNDLE_KEY = "RetryClick"
    }


}