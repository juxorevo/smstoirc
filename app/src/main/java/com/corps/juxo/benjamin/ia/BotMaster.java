package com.corps.juxo.benjamin.ia;

import android.telephony.SmsManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Benjamin Adacis on 01/02/2018.
 */

public class BotMaster extends ThreadServer {

    public BotMaster(){
        super();
    }

    public BotMaster(String host_server, int port, String nomPersonne, String pn) {
        super(host_server, port, nomPersonne, "I'm online Master, what can I do for you ?", pn);
        this.nickname = MainActivity.me.getPseudoTo().toLowerCase()+ "_" + nomPersonne;
        listThreadServer.put(nomPersonne, this);
    }

    public void run() {
        super.initializeConnexion();
        sendIRC("PRIVMSG " + MainActivity.me.getPseudoTo() + " " + super.firstMsg);
        listenerMessageIrc();
        sendIRC("PRIVMSG " + MainActivity.me.getPseudoTo() + " Socket Close");

    }


    public void listenerMessageIrc() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (line.contains("PING :")) {
                    sendIRC("PONG");
                    System.out.println("PONG");
                } else if ( line.contains("PRIVMSG") &&
                            line.contains("name?")&&
                            line.toLowerCase().contains(MainActivity.me.getPseudoTo().toLowerCase())) {
                    sendIRC("PRIVMSG " + MainActivity.me.getPseudoTo() + " The name of the contact is :" + name);
                } else if (line.contains("PRIVMSG") &&
                            line.contains("tel:")&&
                            line.toLowerCase().contains(MainActivity.me.getPseudoTo().toLowerCase())) {
                    ContactPhone c = new ContactPhone(MainActivity.me.getBaseContext());
                    if(line.split(":").length > 3)
                        sendIRC("PRIVMSG " + MainActivity.me.getPseudoTo() + " " + c.getPhoneNumberByContactName(line.split(":")[3]));
                    else
                        sendIRC("PRIVMSG " + MainActivity.me.getPseudoTo() + " I can't there's an error in your request.");
                } else if (line.contains("PRIVMSG") &&
                        line.contains("send:")&&
                        line.toLowerCase().contains(MainActivity.me.getPseudoTo().toLowerCase())) {
                        sendSMS(line);
                } else{
                    System.out.println("Nothing to do");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSMS(final String msg) {
        Runnable r = new Runnable() {
            public void run() {
                try {

                    SmsManager smsManager = SmsManager.getDefault();
                    int start = msg.indexOf(nickname);
                    int startsplit = start + nickname.length() + 2;
                    String MessageComplet = msg.substring(startsplit);
                    String finalmsg = MessageComplet.split(":")[2];
                    String pn = MessageComplet.split(":")[1];
                    int length = finalmsg.length();

                    if (length > MAX_SMS_MESSAGE_LENGTH) {
                        ArrayList<String> messageList = smsManager.divideMessage(finalmsg);
                        smsManager.sendMultipartTextMessage(pn, null, messageList, null, null);
                    } else {
                        SmsReceiver.saveSms(finalmsg, pn);
                        smsManager.sendTextMessage(pn, null, finalmsg, null, null);
                    }

                    sendIRC("PRIVMSG "
                            + MainActivity.me.getPseudoTo()
                            + " Sms bien envoyé");

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        };
        new Thread(r).start();
    }
}
