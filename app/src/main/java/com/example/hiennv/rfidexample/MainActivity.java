package com.example.hiennv.rfidexample;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private NfcAdapter nfcAdapter;
    TextView tvInfo;
    TextView tvTagInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tv_info);
        tvTagInfo = findViewById(R.id.tv_tag_info);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Log.i(TAG, "NFC NOT supported on this devices!");
        } else if (!nfcAdapter.isEnabled()) {
            Log.i(TAG, "NFC NOT Enabled!");
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.i(TAG, "onResume: ACTION_TAG_DISCOVERED");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag == null) {
                tvInfo.setText("Tag == null");
            } else {
                String tagInfo = tag.toString() + "\n";
                tagInfo += "TagId: " + "\n";
                byte[] tagId = tag.getId();
                tagInfo += "Length: " + tagId.length + "\n";
                for (int i = 0; i < tagId.length; i++) {
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                tagInfo += "\n";

                String[] techList = tag.getTechList();
                tagInfo += "\nTech List\n";
                tagInfo += "Length: " + techList.length + "\n";
                for (int i = 0; i < techList.length; i++) {
                    tagInfo += techList[i] + "\n";
                }
                tvInfo.setText(tagInfo);

                readMifareClassic(tag);
            }
        } else {
            Log.i(TAG, "onResume: " + action);
        }
    }

    private void readMifareClassic(Tag tag) {
        MifareClassic mifareClassicTag = MifareClassic.get(tag);
        String strTypeInfo = "---MifareClassic Tag---\n";
        int type = mifareClassicTag.getType();
        switch (type) {
            case MifareClassic.TYPE_CLASSIC:
                strTypeInfo += "MifareClassic.TYPE_CLASSIC\n";
                break;
            case MifareClassic.TYPE_PLUS:
                strTypeInfo += "MifareClassic.TYPE_PLUS\n";
                break;
            case MifareClassic.TYPE_PRO:
                strTypeInfo += "MifareClassic.TYPE_PRO\n";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                strTypeInfo += "MifareClassic.TYPE_UNKNOWN\n";
                break;
            default:
                strTypeInfo += "Unknown...\n";
                break;
        }
        int size = mifareClassicTag.getSize();
        switch (size) {
            case MifareClassic.SIZE_MINI:
                strTypeInfo += "MifareClassic.SIZE_MINI\n";
                break;
            case MifareClassic.SIZE_1K:
                strTypeInfo += "MifareClassic.SIZE_1K\n";
                break;
            case MifareClassic.SIZE_2K:
                strTypeInfo += "MifareClassic.SIZE_2K\n";
                break;
            case MifareClassic.SIZE_4K:
                strTypeInfo += "MifareClassic.SIZE_4K\n";
                break;
            default:
                strTypeInfo += "Unknown Size...";
                break;
        }

        int blockCount = mifareClassicTag.getBlockCount();
        strTypeInfo += "BlockCount: " + blockCount + "\n";
        int sectorCount = mifareClassicTag.getSectorCount();
        strTypeInfo += "SectorCount: " + sectorCount + "\n";

        tvTagInfo.setText(strTypeInfo);
    }
}
