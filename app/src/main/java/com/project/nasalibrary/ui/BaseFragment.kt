package com.project.nasalibrary.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.project.nasalibrary.ui.noInternetDialogFragment.NoInternetDialogFragment
import com.project.nasalibrary.utils.INetworkConnectionObserver
import com.project.nasalibrary.utils.NetworkConnectionObserver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


abstract class BaseFragment : Fragment() {

    lateinit var networkObserver: INetworkConnectionObserver

    private val _isConnected = MutableStateFlow(true)
    private val isConnected = _isConnected.asStateFlow()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkObserver = NetworkConnectionObserver(requireContext())

        // Listen for the retry button click from the dialog
        childFragmentManager.setFragmentResultListener(
            NoInternetDialogFragment.REQUEST_KEY,
            this
        ) { _, bundle ->
            val retry = bundle.getBoolean(NoInternetDialogFragment.BUNDLE_KEY)
            if (retry) {
                if (isConnected.value) {
                    // If connected, perform the retry action.
                    onRetry()
                } else {
                    // If still not connected, show a new dialog instance directly.
                    NoInternetDialogFragment().show(childFragmentManager, NoInternetDialogFragment.TAG)
                }
            }
        }

        // Observe network changes
        viewLifecycleOwner.lifecycleScope.launch {
            networkObserver.observe().collect { connectionState ->
                _isConnected.value = connectionState
                handleNetworkState(connectionState)
            }
        }
    }

    private fun handleNetworkState(isConnected: Boolean) {
        val dialog = childFragmentManager.findFragmentByTag(NoInternetDialogFragment.TAG)
        if (!isConnected) {
            // initial network loss detection
            if (dialog == null) {
                NoInternetDialogFragment().show(childFragmentManager, NoInternetDialogFragment.TAG)
            }
        } else {
            // Dismiss dialog if it's showing and connection is restored
            (dialog as? NoInternetDialogFragment)?.dismiss()
        }
    }

    abstract fun onRetry()
}