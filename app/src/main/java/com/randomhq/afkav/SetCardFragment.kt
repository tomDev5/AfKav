package com.randomhq.afkav

import android.content.Context
import android.nfc.NfcManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.randomhq.afkav.databinding.FragmentSecondBinding


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SetCardFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val manager = requireContext().getSystemService(Context.NFC_SERVICE) as NfcManager
        val adapter = manager.defaultAdapter

        val message = if (adapter == null) {
            "Your device doesn't seem to support NFC"
        } else if (!adapter.isEnabled) {
            // adapter exists and is enabled.
            "NFC is off! WTF do you want me to do"
        } else {
            "NFC is working!"
        }
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            .setAction("Action", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}