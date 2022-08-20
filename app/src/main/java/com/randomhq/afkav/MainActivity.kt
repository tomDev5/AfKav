package com.randomhq.afkav

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {
    private var savedTag: Tag? = null  // The tag saved from the scanner

    private lateinit var cardHandler: CardHandler  // Handles saving and loading card

    private lateinit var nfcAdapter: NfcAdapter  // The NFC Adapter the phone has.
    private lateinit var pendingIntent: PendingIntent  // Pending Intent to receive an NFC card.

    private var isScanning = false  // True = Scanning, False = Transmitting.

    // Buttons in the activity
    private lateinit var addCardButton: FloatingActionButton
    private lateinit var removeCardButton: FloatingActionButton
    private lateinit var transmitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, this.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_MUTABLE
        )

        // Create Card Handler
        cardHandler = CardHandler(applicationContext)

        handleButtons()

        savedTag = cardHandler.loadCard()
        updateStateTextView()
    }

    private fun loadDialog(
        message: String,
        onPositive: android.content.DialogInterface.OnClickListener
    ) {
        AlertDialog.Builder(this@MainActivity).setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.dialog_positive), onPositive)
            .setNegativeButton(getString(R.string.dialog_negative)) { dialog, _ -> dialog.dismiss() }
            .create().show()
    }

    private fun handleButtons() {
        addCardButton = findViewById(R.id.bt_add_card)
        addCardButton.setOnClickListener {
            if (savedTag == null || isScanning) {
                isScanning = !isScanning
            } else {
                loadDialog(
                    getString(R.string.overwrite_card_confirmation)
                ) { _, _ ->
                    isScanning = !isScanning
                }
            }
            updateStateTextView()
        }

        removeCardButton = findViewById(R.id.bt_remove_card)
        removeCardButton.setOnClickListener {
            loadDialog(
                getString(R.string.remove_card_confirmation)
            ) { _, _ ->
                savedTag = null
                cardHandler.removeCard()
            }
            updateStateTextView()
        }

        transmitButton = findViewById(R.id.bt_transmit_button)
        transmitButton.setOnClickListener {
            transmitCard()
        }
    }

    private fun transmitCard() {
        Snackbar.make(findViewById(R.id.constraints), "Transmitting Card", Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun updateStateTextView() {
        val stateTextView = findViewById<TextView>(R.id.tv_card_state)
        stateTextView.text = if (isScanning) {
            getString(R.string.scanning_for_card)
        } else if (savedTag == null) {
            getString(R.string.no_card_saved)
        } else {
            getString(R.string.ready_to_transmit)
        }

        transmitButton.isEnabled = !(isScanning || savedTag == null)
        removeCardButton.isEnabled = savedTag != null
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }


    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        if (intent != null) {
            resolveIntent(intent)
        }
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
            val tag: Tag = (intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?)!!

            // Save the NFC card to Shared Prefs
            cardHandler.saveCard(tag)
            savedTag = tag

            // Update state of scanner
            isScanning = false
            updateStateTextView()
        }
    }
}