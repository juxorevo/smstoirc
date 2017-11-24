package com.corps.juxo.benjamin.ia;

import android.content.ContentValues;
import android.net.Uri;
import android.telephony.SmsManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by Benjamin on 19/11/2017.
 */

public class ThreadServer extends Thread {

    private SSLSocket mySocket;
    private String host_server;
    private int port;
    private String nickname;
    private String name;
    private BufferedWriter bwriter;
    private String firstMsg;
    private String phoneNumber;
    private BufferedReader reader;
    private OutputStreamWriter outputStreamWriter;
    private final int MAX = 9999;
    private final int MIN = 1000;

    private static int MAX_SMS_MESSAGE_LENGTH = 69;

    public static HashMap<String, ThreadServer> listThreadServer = new HashMap<>();

    /**
     *
     */
    public ThreadServer(){
        super();
    }

    /**
     *
     * @param host_server
     * @param port
     * @param nomPersonne
     * @param firstMsg
     * @param pn
     */
    public ThreadServer(String host_server, int port, String nomPersonne, String firstMsg, String pn) {
        super();

        this.host_server = host_server;
        this.port = port;
        this.name = nomPersonne;
        this.nickname = "ID"+((int)(Math.random() * ( MAX - MIN )));
        this.firstMsg = firstMsg;
        this.phoneNumber = pn;
        listThreadServer.put(nomPersonne, this);
    }

    @Override
    public void run() {
        super.run();

        try{
            //Initialisation
            SSLSocketFactory factoryssl = HttpsURLConnection.getDefaultSSLSocketFactory();
            this.mySocket = (SSLSocket) factoryssl.createSocket(host_server, port);
            this.mySocket.startHandshake();
            System.out.println("Handshaking ok");

            //Certificate[] serverCerts = mySocket.getSession().getPeerCertificates();
            /*for(int i = 0; i<serverCerts.length; i++){
                Certificate myCert = serverCerts[i];
                System.out.println("====Certificate:" + (i+1) + "====");
                System.out.println("-Public Key-n" + myCert.getPublicKey());
                System.out.println("-Certificate Type-n " + myCert.getType());
                System.out.println();
            }*/

            //Connexion sans certificat type objet à changer en Socket
            //this.mySocket = new Socket(host_server, port);

            outputStreamWriter = new OutputStreamWriter(mySocket.getOutputStream());
            bwriter = new BufferedWriter(outputStreamWriter);
            reader = new BufferedReader(new InputStreamReader(mySocket.getInputStream( )));

            sendString("NICK " + nickname );
            sendString("USER " + nickname + " 8 * : IA Application Android \r\n");
            String line = null;
            while ((line = reader.readLine( )) != null) {
                System.out.println(line);
                if (line.indexOf("004") >= 0) {
                    sendString("PRIVMSG "
                            + MainActivity.me.getPseudoTo()
                            + " hello Master, you have a text message from :"
                            + name);
                    break;
                }
                else if (line.indexOf("433") >= 0) {
                    mySocket.close();
                    //System.out.println("Nickname is already in use.");
                    return;
                }
            }
            sendString("PRIVMSG " + MainActivity.me.getPseudoTo() + " " + firstMsg);
            waitAuthentication();
            sendString("PRIVMSG " + MainActivity.me.getPseudoTo() + " Socket Close");
            //System.out.println("fin du socket");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void waitAuthentication(){
        try{
            String line = null;
            while ((line = reader.readLine( )) != null) {
                System.out.println(line);
                if(line.contains("PING")){
                    sendString("PONG");
                    //System.out.println("PONG");
                }else if(line.contains("name?")){
                    sendString("PRIVMSG " + MainActivity.me.getPseudoTo() + " The name of the contact is :" + name);
                } else if (line.contains("PRIVMSG")) {
                    sendMsg(line);
                }else if (line.indexOf("433") >= 0) {
                    //System.out.println("Nickname is already in use.");
                    return;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * envoie d'un message IRC
     * @param str
     */
    public void sendString(final String str) {

        Runnable r = new Runnable() {
            public void run() {
                try {
                    bwriter.write(str + "\r\n");
                    bwriter.flush();
                }
                catch (Exception e) {
                    System.out.println("Exception: "+e);
                }
            }
        };

        new Thread(r).start();
    }

    /**
     * Envoie d'un sms
     * @param msg
     */
    public void sendMsg(final String msg){
        Runnable r = new Runnable() {
            public void run() {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    int start = msg.indexOf(nickname);
                    int startsplit = start + nickname.length()+2;
                    String finalmsg = msg.substring(startsplit);
                    int length = finalmsg.length();
                    if(length > MAX_SMS_MESSAGE_LENGTH) {
                        ArrayList<String> messageList = smsManager.divideMessage(finalmsg);
                        smsManager.sendMultipartTextMessage(phoneNumber, null, messageList, null, null);
                    }else {
                        smsManager.sendTextMessage(phoneNumber, null, finalmsg, null, null);
                    }

                    ContentValues values = new ContentValues();
                    values.put("address", phoneNumber);//sender name
                    values.put("body", finalmsg);
                    MainActivity.cr.insert(Uri.parse("content://sms/inbox"), values);
                    sendString("PRIVMSG "
                            + MainActivity.me.getPseudoTo()
                            + "Sms bien envoyé");
                }
                catch (Exception e) {
                    //System.out.println("Exception: "+e);

                }
            }
        };
        new Thread(r).start();
    }

    public void deconnexion(){
        try {
            sendString("PRIVMSG "
                    + MainActivity.me.getPseudoTo()
                    + "Fermeture de la connexion");
            wait(2000);
            mySocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
