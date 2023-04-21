package com.airwallex.android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.airwallex.android.R
import com.airwallex.android.core.log.AnalyticsLogger
import com.airwallex.android.core.log.TrackablePage
import com.airwallex.android.databinding.FragmentBaseBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BottomSheetDialog<VB : ViewBinding> : BottomSheetDialogFragment() {

    // base binding using in base class
    private var _baseBinding: FragmentBaseBinding? = null
    private val baseBinding
        get() = _baseBinding!!

    // binding using in the sub class
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransBottomSheetDialogStyle)

        if (this is TrackablePage) {
            AnalyticsLogger.logPageView(pageName, additionalInfo)
        }
    }

    abstract fun bindFragment(inflater: LayoutInflater, container: ViewGroup): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _baseBinding = FragmentBaseBinding.inflate(inflater, container, false)
        _binding = bindFragment(inflater, baseBinding.fragmentContent)
        return baseBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // required on fragment
        _binding = null
        _baseBinding = null
    }
}
