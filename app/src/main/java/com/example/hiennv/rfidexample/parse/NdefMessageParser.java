package com.example.hiennv.rfidexample.parse;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.example.hiennv.rfidexample.record.ParseNdefRecord;
import com.example.hiennv.rfidexample.record.SmartPoster;
import com.example.hiennv.rfidexample.record.TextRecord;
import com.example.hiennv.rfidexample.record.UriRecord;

import java.util.ArrayList;
import java.util.List;

public class NdefMessageParser {
    public NdefMessageParser(){

    }

    public static List<ParseNdefRecord> parse(NdefMessage message){
        return getRecords(message.getRecords());
    }

    public static List<ParseNdefRecord> getRecords(NdefRecord[] records) {
        List<ParseNdefRecord> elements = new ArrayList<ParseNdefRecord>();

        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
                elements.add(new ParseNdefRecord() {
                    public String getStr() {
                        return new String(record.getPayload());
                    }
                });

            }
        }

        return elements;
    }
}
