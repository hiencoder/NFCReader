package com.example.hiennv.rfidexample.record;

import android.nfc.NdefRecord;

import com.google.common.base.Preconditions;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class TextRecord implements ParseNdefRecord {
    private static final String TAG = TextRecord.class.getSimpleName();
    private final String mLanguageCode;
    private final String mText;

    public TextRecord(String languageCode, String text) {
        mLanguageCode = Preconditions.checkNotNull(languageCode);
        mText = Preconditions.checkNotNull(text);
    }

    @Override
    public String getStr() {
        return mText;
    }

    public String getText() {
        return mText;
    }

    public static TextRecord parse(NdefRecord record) {
        Preconditions.checkArgument(record.getTnf() == NdefRecord.TNF_WELL_KNOWN);
        Preconditions.checkArgument(Arrays.equals(record.getType(), NdefRecord.RTD_URI));
        try {
            byte[] payLoad = record.getPayload();
            String textEncoding = ((payLoad[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payLoad[0] & 0077;
            String languageCode = new String(payLoad, 1, languageCodeLength, "US-ASCII");

            String text = new String(payLoad, languageCodeLength + 1,
                    payLoad.length - languageCodeLength - 1, textEncoding);

            return new TextRecord(languageCode, text);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }

    }

    public static boolean isText(NdefRecord record) {
        try {
            parse(record);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
