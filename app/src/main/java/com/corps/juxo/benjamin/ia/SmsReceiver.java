package com.corps.juxo.benjamin.ia;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.Calendar;


public class SmsReceiver  extends BroadcastReceiver {

    private final String ACTION_RECEIVE_SMS = "android.provider.Telephony.SMS_RECEIVED";

    private Context mContext;
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        String action = intent.getAction();

        if (action.equals(ACTION_RECEIVE_SMS) && MainActivity.EXECUTE) {
            Bundle bundle;
            bundle = intent.getExtras();
            if (bundle != null) {
               extractMessage();
            }
        }
    }

    /**
     * Received SMS
     * @param message
     * @param phoneNumber
     * @param readState
     */
    public static void saveSms(String message, String phoneNumber, int readState){
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        values.put("read", readState); //"0" for have not read sms and "1" for have read sms
        values.put("date", Calendar.getInstance().getTime().getTime());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri uri = Telephony.Sms.Inbox.CONTENT_URI;
                MainActivity.me.getContentResolver().insert(uri, values);
            }
            else {
                MainActivity.me.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sent SMS
     * @param message
     * @param phoneNumber
     */
    public static void saveSms(String message, String phoneNumber){
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", message);
        values.put("read", 1); //"0" for have not read sms and "1" for have read sms
        values.put("date", Calendar.getInstance().getTime().getTime());

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri uri = Telephony.Sms.Sent.CONTENT_URI;
                MainActivity.me.getContentResolver().insert(uri, values);
            }
            else {
                MainActivity.me.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String extractMessage(){
        String address = "";
        String contentSMS = "";
        String phoneNumber = "";
        String NameContact = "";
        //int contactId = -1;

        SmsMessage[] msgs = getMessagesFromIntent(this.mIntent);

        if (msgs != null) {
            //Recherche du contenu du message
            for (int i = 0; i < msgs.length; i++) {
                phoneNumber = msgs[i].getDisplayOriginatingAddress();
                address = msgs[i].getOriginatingAddress();
                contentSMS += msgs[i].getMessageBody().toString();
                contentSMS += " ";
            }

            // Recherche du contact associé
            String contact = new ContactPhone(mContext).getContactNameByPhoneNumber(address);
            if(contact.isEmpty()){
                NameContact = phoneNumber;
            }else{
                NameContact = contact.replace(" ", "-");
            }

            saveSms(contentSMS, phoneNumber, 1);
            //Démarrage du thread de connexion à IRC
            startThread(NameContact, contentSMS, phoneNumber);

        }

        return contentSMS;
    }

    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[][] pduObjs = new byte[messages.length][];

        for (int i = 0; i < messages.length; i++) {
            pduObjs[i] = (byte[]) messages[i];
        }
        byte[][] pdus = new byte[pduObjs.length][];
        int pduCount = pdus.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i = 0; i < pduCount; i++) {
            pdus[i] = pduObjs[i];
            msgs[i] = SmsMessage.createFromPdu(pdus[i]);
        }
        return msgs;
    }

    private void startThread(String NameContact, String msg, String phoneNumber){
        ThreadServer t = ThreadServer.listThreadServer.get(NameContact);
        if(t == null){
            t = new ThreadServer(   MainActivity.me.getHost_server(),
                                    MainActivity.me.getPort(),
                                    NameContact,
                                    msg,
                                    phoneNumber);
            t.start();
        }else{
            t.sendIRC("PRIVMSG " + MainActivity.getPseudoTo() + " " + msg
                        .replaceAll("(\r\n)+", " ")
                        .replaceAll("\n+", " "));
        }
    }
}
