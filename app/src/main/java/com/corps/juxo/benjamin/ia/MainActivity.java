package com.corps.juxo.benjamin.ia;

import android.Manifest;
import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.Collection;

public class MainActivity extends AppCompatActivity {

    //Request
    private int MY_PERMISSIONS_REQUEST_MULTIPLE = 10;

    //Variable environment
    public static boolean EXECUTE = false;
    public static String host_server = "";
    public static int port = 6697;
    public static String pseudoTo = "";
    public static MainActivity me;
    public static ContentResolver cr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                                                }, MY_PERMISSIONS_REQUEST_MULTIPLE);
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
        button.setText("Stop");
        //System.out.println("Host : " + host_server + " port " + port + " Pseudo : " + pseudoTo);

    }

    /**
     * Stop listenning SMS and destroy all connexion to IRC.
     */
    public void shutDown() {
        MainActivity.EXECUTE = false;
        Collection<ThreadServer> col = ThreadServer.listThreadServer.values();
        for (ThreadServer t : col) {
            t.deconnexion();
            col.remove(t);
        }
        Button button = (Button) findViewById(R.id.Launch);
        button.setText("Start");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
