package com.example.marcello.providers;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.HashMap;

public class ContactManager {
    private static final String TAG = "ContactManager";

    private final String TELEPHONE_SCHEMA = "tel:";
    private final String PRESERVED_CHARACTER = "+";
    private final String EG_COUNTRY_CODE = "20";

    private static ContactManager instance = new ContactManager();
    private ContactManager(){
    }
    public static synchronized ContactManager getInstance(){
        return instance;
    }
    public String readContacts(Context context){
        ContentResolver cr = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = cr.query(uri,null,null,null,null);
        if(cursor.getCount() > 0){
            Log.d(TAG, "#OF Contacts: " + cursor.getCount());
            while(cursor.moveToNext()){
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                @SuppressLint("Range") String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                Log.d(TAG, "ContactName: " + name + ", ContactNumber: " + number);
            }
        }
        return "done";
    }
    public String addContact(Context context,  HashMap<Object, Object> data){

        ContentResolver cr = context.getContentResolver();

        Uri rawContactUri = cr.insert(ContactsContract.RawContacts.CONTENT_URI, new ContentValues());
        long rawId = ContentUris.parseId(rawContactUri);

        ContentValues contentName = new ContentValues();
        contentName.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        contentName.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE); // this is necessary to work
        contentName.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, data.get("displayName").toString());
        cr.insert(ContactsContract.Data.CONTENT_URI,contentName);

        ContentValues contentNumber = new ContentValues();
        contentNumber.put(ContactsContract.Data.RAW_CONTACT_ID, rawId);
        contentNumber.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        contentNumber.put(ContactsContract.CommonDataKinds.Phone.NUMBER, data.get("phoneNumber").toString());
        contentNumber.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);

        cr.insert(ContactsContract.Data.CONTENT_URI,contentNumber);
        Log.d(TAG, "addContact: Success!");
        return "done";
    }
    @SuppressLint("Range")
    public String deleteContact(Context context,  HashMap<Object, Object> data){


        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
                ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{data.get("displayName").toString()}, null);

        if(cur.getCount() > 0){
            Log.d(TAG, "deleteContact: contacts found: " + cur.getCount());
            if(cur.getCount() == 1){
                cur.moveToFirst();
                int deletedRows = cr.delete(ContactsContract.RawContacts.CONTENT_URI,
                        ContactsContract.RawContacts._ID + " = ?" , new String[]{cur.getString(0)});
                Log.d(TAG, "deleteContact: deletedCount: " + deletedRows);
            }else{
                Log.d(TAG, "deleteContact: Which contact you wish to delete.");
            }
        }
        return "done";
    }

    public String updateContact(){
        return null;
    }

    public String makeACall(Context context,  HashMap<Object, Object> data){


        ContentResolver cr = context.getContentResolver();

        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
                        ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                },
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?  COLLATE NOCASE",
                new String[]{data.get("displayName").toString()}, null);

        String resultMessage =  data.get("displayName") + "لا يوجد رفم بـ اسم ";

        if(cur.getCount() > 0){
            cur.moveToFirst();
            String phoneNumber = preprocessNumber(cur.getString(2));

            Uri phoneCallUri = Uri.parse(TELEPHONE_SCHEMA + PRESERVED_CHARACTER + EG_COUNTRY_CODE + phoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_CALL, phoneCallUri);
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
            resultMessage =  data.get("displayName").toString() + "جارى الاتصال بـ ";
        }
        cur.close();
        return resultMessage;
    }
    private String preprocessNumber(String phoneNumber){
        if(phoneNumber.startsWith("+20")){
            phoneNumber= phoneNumber.substring(3);
        }else if(phoneNumber.startsWith("0")){
            phoneNumber = phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}
