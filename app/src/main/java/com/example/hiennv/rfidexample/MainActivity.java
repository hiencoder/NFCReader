package com.example.hiennv.rfidexample;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private NfcAdapter nfcAdapter;
    TextView tvInfo;
    TextView tvTagInfo;
    TextView tvBlock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvInfo = findViewById(R.id.tv_info);
        tvTagInfo = findViewById(R.id.tv_tag_info);
        tvBlock = findViewById(R.id.tv_block);
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
        new ReadMifareClassicTask(mifareClassicTag).execute();
    }

    private class ReadMifareClassicTask extends AsyncTask<Void, Void, Void> {

        /*
        MIFARE Classic tags are divided into sectors, and each sector is sub-divided into blocks.
        Block size is always 16 bytes (BLOCK_SIZE). Sector size varies.
        MIFARE Classic 1k are 1024 bytes (SIZE_1K), with 16 sectors each of 4 blocks.
        */

        MifareClassic taskTag;
        int numOfBlock;
        final int FIX_SECTOR_COUNT = 16;
        boolean success;
        final int numOfSector = 16;
        final int numOfBlockInSector = 4;
        byte[][][] buffer = new byte[numOfSector][numOfBlockInSector][MifareClassic.BLOCK_SIZE];

        ReadMifareClassicTask(MifareClassic tag){
            taskTag = tag;
            success = false;
        }

        @Override
        protected void onPreExecute() {
            tvBlock.setText("Reading Tag, don't remove it!");
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                taskTag.connect();

                for(int s=0; s<numOfSector; s++){
                    if(taskTag.authenticateSectorWithKeyA(s, MifareClassic.KEY_DEFAULT)) {
                        for(int b=0; b<numOfBlockInSector; b++){
                            int blockIndex = (s * numOfBlockInSector) + b;
                            buffer[s][b] = taskTag.readBlock(blockIndex);
                        }
                    }
                }

                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                if(taskTag!=null){
                    try {
                        taskTag.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //display block
            if(success){
                String stringBlock = "";
                for(int i=0; i<numOfSector; i++){
                    stringBlock += i + " :\n";
                    for(int j=0; j<numOfBlockInSector; j++){
                        for(int k=0; k<MifareClassic.BLOCK_SIZE; k++){
                            stringBlock += String.format("%02X", buffer[i][j][k] & 0xff) + " ";
                        }
                        stringBlock += "\n";
                    }
                    stringBlock += "\n";
                }
                tvBlock.setText(stringBlock);
            }else{
                tvBlock.setText("Fail to read Blocks!!!");
            }
        }
    }
}
