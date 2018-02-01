package com.corps.juxo.benjamin.ia;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactPhone {

    private Context context;

    public ContactPhone(Context c){
        context = c;
    }

    public String getContactNameByPhoneNumber(String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
          Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        if (cursor == null) {
            return null;
        }
        String contactName = "";
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    public String getPhoneNumberByContactName(String contactName){
        ContentResolver cr = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
        String[] selectionArguments = { "%"+contactName+"%" };
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, selection, selectionArguments, null);
        String result = "Res : ";
        if (c != null && c.moveToFirst()) {
            do {
                try {
                        for(int i = 0; i < c.getColumnCount(); i++) {
                            result += "id: " + i +", "+c.getString(i) + " ";
                        }
                        result += " ; ";

                }catch(IllegalArgumentException e){

                }
            }while (c.moveToNext());
        }

        if(c != null && !c.isClosed()) {
            c.close();
        }

        return result;
    }

}
