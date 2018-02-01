package com.corps.juxo.benjamin.ia;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    //Request
    private int MY_PERMISSIONS_REQUEST_MULTIPLE = 10;
    private static final String ACTION_MMS_RECEIVED = "android.provider.Telephony.WAP_PUSH_RECEIVED";
    private static final String MMS_DATA_TYPE = "application/vnd.wap.mms-message";

    //Variable environment
    public static boolean EXECUTE = false;
    public static String host_server = "";
    public static int port = 6697;
    public static String pseudoTo = "";
    public static MainActivity me;
    public static ContentResolver cr;
    private static final int DEF_SMS_REQ = 0;
    private String defaultSmsApp="";
    private MmsReceiver receiverMms;
    public IntentFilter mmsIntent;

    /*private void test() {
                if (Build.VERSION.SDK_INT > 18) {
                    //DEBUG
                    Cursor c = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
                   // Cursor c = getContentResolver().query(Telephony.Sms.CONTENT_URI,
                   //         new String[]{"_id", "thread_id", "address",
                   //                 "person", "date", "body"}, null, null, null);

                    if (c != null && c.moveToFirst()) {
                        do {
                            if(c.getString(2).contains("+33650064154")){
                                if(c.getString(12).contains("69")) {
                                    System.out.println(c.getString(12));
                                    break;
                                }
                            }
                           /* for (int i = 0 ; i < c.getColumnCount(); i ++
                                 ) {

                                System.out.println("index : " + i + " - " + c.getColumnName(i)+" : " + c.getString(i));
                            }*/

                           /* long ids = c.get
                            long threadId = c.getLong(1);
                            String address = c.getString(2);
                            Long dateLong = c.getLong(4);
                            String body = c.getString(5);*/
                       /* } while (c.moveToNext());
                    }
                    System.out.println("STOP");
                }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startMmsBroadcast();
        //test();

        //Default application
        if(Build.VERSION.SDK_INT > 18) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(getBaseContext());
            Intent intent = new  Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
            startActivity(intent);
        }

        //Instanciation access
        cr = getContentResolver();
        me = this;

        //Ihm Initialisation
        Button button= (Button) findViewById(R.id.Launch);
        button.setOnClickListener(new GeneralListener());
        CheckBox checkSsl = (CheckBox) findViewById(R.id.checkBox);
        checkSsl.setEnabled(false);

        //Autorisation Initialisation
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS,
                                                            Manifest.permission.READ_CONTACTS,
                                                            Manifest.permission.BROADCAST_SMS,
                                                            Manifest.permission.SEND_SMS,
                                                            Manifest.permission.READ_SMS
                                                },MY_PERMISSIONS_REQUEST_MULTIPLE);
      }

    /**
     * When user return if true or false for autorization
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_MULTIPLE) {
         //   Log.i("TAG", "MY_PERMISSIONS_REQUEST_SMS_RECEIVE --> YES");
        }
    }

    /**
     * Set the environment variable
     */
    public void startListenningSms(){
        EditText textHost = (EditText) findViewById(R.id.host);
        EditText textport = (EditText) findViewById(R.id.port);
        EditText textpseudoto = (EditText) findViewById(R.id.pseudoto);
        Button button = (Button) findViewById(R.id.Launch);

        host_server = textHost.getText().toString();
        port = Integer.valueOf(textport.getText().toString());
        pseudoTo = textpseudoto.getText().toString();
        EXECUTE = true;

        BotMaster b = new BotMaster(host_server, port, "BotMaster", "I'm online Master");
        b.start();

        button.setText("Stop");
    }

    /**
     * Stop listenning SMS and destroy all connexion to IRC.
     */
    public void shutDown() {
        MainActivity.EXECUTE = false;
        Collection<ThreadServer> col = ThreadServer.listThreadServer.values();

        for (ThreadServer t : col) {
            t.deconnexion();
        }

        ThreadServer.listThreadServer = new HashMap<>();
        Button button = (Button) findViewById(R.id.Launch);
        button.setText("Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMmsBroadcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    protected void startMmsBroadcast(){
        receiverMms = new MmsReceiver();
        try {
            mmsIntent = new IntentFilter(ACTION_MMS_RECEIVED);
            mmsIntent.addDataType(MMS_DATA_TYPE);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        this.registerReceiver(receiverMms,mmsIntent);
    }

    protected void stopMmsBroadcast(){
        try {
            this.unregisterReceiver(receiverMms);
            receiverMms = null;
        }catch(IllegalArgumentException e){
        }

    }


    public static String getHost_server() {
        return host_server;
    }

    public static int getPort() {
        return port;
    }

    public static String getPseudoTo() {
        return pseudoTo;
    }

}
