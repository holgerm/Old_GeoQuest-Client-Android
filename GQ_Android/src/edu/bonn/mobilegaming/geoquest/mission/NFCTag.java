package edu.bonn.mobilegaming.geoquest.mission;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.bonn.mobilegaming.geoquest.Globals;
import edu.bonn.mobilegaming.geoquest.R;
import edu.bonn.mobilegaming.geoquest.ui.abstrakt.MissionOrToolUI;

public class NFCTag extends InteractiveMission implements OnClickListener {

    private static final String TAG = "NFCMission";

    // NFC Feature support
    private NfcAdapter mNfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter writeTagFilters[];
    private boolean writeMode;
    private Tag scannedTag;

    private TextView nfcTextView;
    private Button endNfcMissionbutton, resetNfcInfobutton;
    private EditText newNfcInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.nfctagmission);

	nfcTextView = (TextView) findViewById(R.id.nfcTextView);
	endNfcMissionbutton = (Button) findViewById(R.id.endNfcMissionbutton);
	resetNfcInfobutton = (Button) findViewById(R.id.resetNfcInfobutton);
	newNfcInfo = (EditText) findViewById(R.id.newNfcInfo);

	endNfcMissionbutton.setOnClickListener(this);
	resetNfcInfobutton.setOnClickListener(this);

	// Check NFC support for current device. In case its not supported set
	// the NFCMission to end with Error Message on screen.
	mNfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

	if (null == mNfcAdapter) {

	    nfcTextView.setText("Sorry, Nfc is not available on this device.");
	}

	pendingIntent = PendingIntent
		.getActivity(this,
			     0,
			     new Intent(this, getClass())
				     .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
			     0);

	IntentFilter tagDetected = new IntentFilter(
		NfcAdapter.ACTION_TAG_DISCOVERED);
	tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
	writeTagFilters = new IntentFilter[] { tagDetected };

	Log.d(TAG,
	      "Inside NFCMission");
	/**
	 * Steps for the NFCMission 1. Initialize the layout 2. Associate NFC
	 * related IntentFilter with GeoQuest. So NFC will work onDemand 3. Give
	 * functionality to view the current text and facility to write new
	 * information in tag.
	 */
    }

    @Override
    public void onPause() {
	super.onPause();
	WriteModeOff();
    }

    @Override
    public void onResume() {
	super.onResume();
	WriteModeOn();
    }

    private void WriteModeOn() {
	writeMode = true;
	mNfcAdapter.enableForegroundDispatch(this,
					     pendingIntent,
					     writeTagFilters,
					     null);
    }

    private void WriteModeOff() {
	writeMode = false;
	mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
	Log.d(TAG,
	      "Inside onNewIntent fn");
	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
	    scannedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

	    Parcelable msgs[] = intent
		    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	    final int len = msgs != null ? msgs.length : 0;
	    Log.d(TAG,
		  "Ndef mesage length = "
			  + len);

	    NdefMessage[] ndefMsg = new NdefMessage[len];
	    for (int i = 0; i < len; i++) {
		ndefMsg[i] = (NdefMessage) msgs[i];
		NdefRecord records[] = ndefMsg[i].getRecords();

		for (NdefRecord record : records) {

		    Log.d(TAG,
			  "Ndef TNF = "
				  + record.getTnf());

		    String id = new String(record.getId());
		    Log.d(TAG,
			  "Ndef ID = "
				  + id);

		    String type = new String(record.getType());
		    Log.d(TAG,
			  "Ndef TYPE = "
				  + type);

		    String mm = new String(record.getPayload());
		    Log.d(TAG,
			  "Ndef mesage = "
				  + mm);
		    nfcTextView.setText(mm);

		}
	    }

	    byte idArr[] = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
	    Log.d(TAG,
		  "NFC Tag Id = "
			  + new String(idArr));
	}
    }

    /**
     * Creating NdeRecord to write into NdefMessage.
     * 
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    private NdefRecord createRecord(String text)
	    throws UnsupportedEncodingException {

	// create the message in according with the standard
	String lang = "en";
	byte[] textBytes = text.getBytes();
	byte[] langBytes = lang.getBytes("US-ASCII");
	int langLength = langBytes.length;
	int textLength = textBytes.length;

	byte[] payload = new byte[1
		+ langLength
		+ textLength];
	payload[0] = (byte) langLength;

	// copy langbytes and textbytes into payload
	System.arraycopy(langBytes,
			 0,
			 payload,
			 1,
			 langLength);
	System.arraycopy(textBytes,
			 0,
			 payload,
			 1 + langLength,
			 textLength);

	NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
		NdefRecord.RTD_TEXT, new byte[0], payload);
	return recordNFC;
    }

    /*
     * Writes an NdefMessage to a NFC tag
     */
    public static boolean writeTag(NdefMessage message,
				   Tag tag) {
	int size = message.toByteArray().length;
	try {
	    Ndef ndef = Ndef.get(tag);
	    if (ndef != null) {
		ndef.connect();
		if (!ndef.isWritable()) {
		    return false;
		}
		if (ndef.getMaxSize() < size) {
		    return false;
		}
		ndef.writeNdefMessage(message);
		return true;
	    } else {
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
		    try {
			format.connect();
			format.format(message);
			return true;
		    } catch (IOException e) {
			return false;
		    }
		} else {
		    return false;
		}
	    }
	} catch (Exception e) {
	    return false;
	}
    }

    /**
     * Mission onClick listener for events
     */
    public void onClick(View v) {

	switch (v.getId()) {

	case R.id.endNfcMissionbutton:
	    finish(Globals.STATUS_SUCCEEDED);
	    break;
	case R.id.resetNfcInfobutton:
	    String mm = newNfcInfo.getText().toString();

	    try {
		NdefRecord[] records = { createRecord(mm) };
		NdefMessage message = new NdefMessage(records);

		if (writeTag(message,
			     scannedTag)) {
		    Toast.makeText(getApplicationContext(),
				   "Information Replaced.",
				   Toast.LENGTH_SHORT).show();
		} else {
		    Toast.makeText(getApplicationContext(),
				   "ERROR! Touch the NFC Tag.",
				   Toast.LENGTH_SHORT).show();
		}

	    } catch (Exception e) {
		Toast.makeText(getApplicationContext(),
			       "ERROR! Touch the NFC Tag.",
			       Toast.LENGTH_SHORT).show();
	    }

	    break;
	}
    }

    public MissionOrToolUI getUI() {
	// TODO Auto-generated method stub
	return null;
    }
}
