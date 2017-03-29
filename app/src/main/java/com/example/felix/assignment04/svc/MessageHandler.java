package com.example.felix.assignment04.svc;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.example.felix.assignment04.data.ContactsContract;

import org.json.JSONException;
import org.json.JSONObject;




public class MessageHandler {
    private static final String TAG = "JSON";

    public static final String TAG_FNAME = "firstname";
    public static final String TAG_LNAME = "lastname";
    public static final String TAG_PHONE = "homephone";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_LOCATION = "location";

    private static final Map<String, String> MARSHAL_TAB;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(RESTService.FNAME, TAG_FNAME);
        m.put(RESTService.LNAME, TAG_LNAME);
        m.put(RESTService.PHONE, TAG_PHONE);
        m.put(RESTService.EMAIL, TAG_EMAIL);
        MARSHAL_TAB = m;
    }

    private static final Map<String, String> UNMARSHAL_TAB;
    static {
        Map<String, String> m = new HashMap<String, String>();
        m.put(TAG_FNAME, ContactsContract.Columns.FNAME);
        m.put(TAG_LNAME, ContactsContract.Columns.LNAME);
        m.put(TAG_PHONE, ContactsContract.Columns.PHONE);
        m.put(TAG_EMAIL, ContactsContract.Columns.EMAIL);
        m.put(TAG_LOCATION, ContactsContract.Columns.REMOTE_ID);
        UNMARSHAL_TAB = m;
    }

    public String marshal(Bundle args) throws JSONException {
        JSONObject payload = new JSONObject();

        if (args.containsKey(RESTService.FNAME)) {
            payload.put(
                    MARSHAL_TAB.get(RESTService.FNAME),
                    args.getString(RESTService.FNAME));
        }

        if (args.containsKey(RESTService.LNAME)) {
            payload.put(
                    MARSHAL_TAB.get(RESTService.LNAME),
                    args.getString(RESTService.LNAME));
        }

        if (args.containsKey(RESTService.PHONE)) {
            payload.put(
                    MARSHAL_TAB.get(RESTService.PHONE),
                    args.getString(RESTService.PHONE));
        }

        if (args.containsKey(RESTService.EMAIL)) {
            payload.put(
                    MARSHAL_TAB.get(RESTService.EMAIL),
                    args.getString(RESTService.EMAIL));
        }

        return payload.toString();
    }

    public ContentValues unmarshal(Reader in, ContentValues vals)
            throws IOException
    {
        JsonReader reader = null;
        try {
            reader = new JsonReader(in);
            unmarshalContact(reader, vals);
        }
        finally {
            if (null != reader) {
                try { reader.close(); } catch (Exception e) {}
            }
        }
        return vals;
    }

    public void unmarshalContact(JsonReader in, ContentValues vals)
            throws IOException
    {
        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            String val = in.nextString();
            String col = UNMARSHAL_TAB.get(key);
            if (null == col) {
                Log.w(TAG, "Ignoring unexpected JSON tag: " + key + "=" + val);
                continue;
            }

            if (TAG_LOCATION.equals(key)) { val = parseLocation(val); }

            vals.put(col, val);
        }
        in.endObject();
    }

    private String parseLocation(String val) {
        return Uri.parse(val).getLastPathSegment();
    }
}

