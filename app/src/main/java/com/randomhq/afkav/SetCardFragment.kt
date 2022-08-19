package com.randomhq.afkav

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.os.Bundle
import android.util.Log
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
        if (activity != null) {
            readFromIntent(activity?.intent);
        }

//        var pendingIntent =
//            PendingIntent.getActivity(context, 0, Intent (this, getClass()).addFlags(
//                Intent.FLAG_ACTIVITY_SINGLE_TOP
//            ), 0);
//        val tagDetected: IntentFilter = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
//        var writeTagFilters = IntentFilter[] { tagDetected };
    }

    private fun readFromIntent(intent: Intent) {
        val action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            val messages: Array<NdefMessage>? = null
            for (i in parcelables!!.indices) {
                messages!![i] = parcelables[i] as NdefMessage
                Log.d("parse", messages[i].toString());
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}