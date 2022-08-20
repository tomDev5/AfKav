package com.randomhq.afkav

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private var savedTag: Tag? = null

    private lateinit var cardHandler: CardHandler

    private lateinit var nfcAdapter: NfcAdapter  // The NFC Adapter the phone has.
    private lateinit var pendingIntent: PendingIntent  // Pending Intent to receive an NFC card.

    private var isScanning = false  // True = Scanning, False = Transmitting.

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


        addCardButton = findViewById(R.id.bt_add_card)
        addCardButton.setOnClickListener {
            isScanning = !isScanning
            updateStateTextView()
        }

        // Create Card Handler
        cardHandler = CardHandler(applicationContext)

        removeCardButton = findViewById(R.id.bt_remove_card)
        removeCardButton.setOnClickListener {
            savedTag = null
            cardHandler.removeCard()
            updateStateTextView()
        }

        transmitButton = findViewById(R.id.bt_transmit_button)
        transmitButton.setOnClickListener {
            Snackbar.make(findViewById(R.id.constraints), "Transmitting", Snackbar.LENGTH_SHORT)
                .show()
        }

        savedTag = cardHandler.loadCard()
        updateStateTextView()
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
            isScanning = false
            savedTag = tag
            updateStateTextView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}