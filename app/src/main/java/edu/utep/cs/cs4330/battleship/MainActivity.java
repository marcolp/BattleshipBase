/*
 * @created Mahdokht Afravi on 04/08 S
 *
 * Starts the Battleship App by asking the user
 *   to choose a method of playing the game:
 *   against a local opponent, a network opponent,
 *   or the computer.
 *
 * @modified Mahdokht Afravi on 04/17 M
 */
package edu.utep.cs.cs4330.battleship;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private Game game;
    Spinner p2p;
    int conectionType;
    boolean connected;
    Socket opponentSocket;
    boolean client;
    //BT Functionality
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    java.util.Set<BluetoothDevice> pairedDevices;
    boolean canPlayBT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conectionType = -1;
        game = game.getInstance();
        p2p = (Spinner) findViewById(R.id.P2Pspinner);
        setP2PSpinner();
        connected = false;
        client = false;
    }

    /* Register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
//        registerReceiver(receiver, intentFilter);
    }
    /* De-register the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
    }

    /** Show a toast message. */
    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /* Sets adapters to the Spinners */
    private void setP2PSpinner() {;
        //P2P Adapter
        ArrayAdapter<CharSequence> p2pAdapter = ArrayAdapter.createFromResource(this,R.array.p2p,android.R.layout.simple_spinner_item);
        p2pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        p2p.setAdapter(p2pAdapter);
        p2p.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch ( position ) {
                    case 0: //Bluetooth
                        //startBTGame();
                        conectionType = 0;
                        break;
                    case 1: //Wifi Direct
                        //startWFDirectGame();
                        conectionType = 1;
                        break;
                    case 2: //Wifi
//                        startWFGame();
                        conectionType = 2;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //TODO idk maybe something pretty?
            }
        });
    }

    /* Bluetooth Functionality */
    private boolean turnOnBT() {
        //turns on BT if off and a functionality on the device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if ( btAdapter==null ) return false; //no BT functionality on the phone
        if ( btAdapter.isEnabled() ) return true; //BT is already on
        startActivityForResult(new Intent(btAdapter.ACTION_REQUEST_ENABLE),2); //intent: settings
        while ( btAdapter.getState()<btAdapter.STATE_ON ) ;
        Log.d("MAINACT-BT","enabled BT");
        return true;
    }
    private void makeDiscoverable() {
        //makes the local device discoverable by bluetooth for 3 minutes (180 sec) (if user ok)
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
        startActivity(discoverableIntent);
        while ( !BluetoothAdapter.getDefaultAdapter().isDiscovering() ) ; //wait until in discovery
        Log.d("MAINACT-BT","enabled BT discovery");
    }
    private void startBTGame() {
        //turnOnBT();
        //makeDiscoverable();
//        if ( !btAdapter.isEnabled() )
//            createBTRequestDialog("Connect to a Bluetooth device!","Okay","Cancel");
//        else
        startActivityForResult(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS),5);
        while ( btAdapter.getState()!=btAdapter.STATE_ON ) ;
        if ( canPlayBT ) {
            NetworkAdapter btNtAdapter = null;
            BluetoothDevice r = findRemote();
            if (r != null)
                btNtAdapter = setUpConnection(r);
            if (btNtAdapter != null) {
                game.playerConnection = btNtAdapter;
                if (r.getAddress().compareTo(btAdapter.getAddress()) > 0)
                    game.userClient = true;
            }
            startGame();
        }
    }
    @Override
    protected void onActivityResult(int req, int res, Intent i) {
        //returning from the intent to turn on bluetooth
        if ( req==5 ) {
            if ( res==RESULT_OK ) {
                canPlayBT = true;
            } else canPlayBT = false;
        }
    }
    private BluetoothDevice findRemote() {
        //find the bluetooth remote device
        BluetoothDevice r = null;
        Log.d("MAINACT-BT","have " +pairedDevices.size()+ " paired devices");
        for ( BluetoothDevice d : pairedDevices ) {
            Log.d("MAINACT-BT", "Paired with " +d.getName()+ " address: " +d.getAddress());
            if ( btAdapter.getRemoteDevice(d.getAddress())==d )
                r = d;
        }
        return r;
    }
    private NetworkAdapter setUpConnection(BluetoothDevice r) {
        //help: http://stackoverflow.com/questions/36661680/cant-do-bluetooth-connection-to-remote-devices-android-6-0
        //create the socket from the connection to this device
        java.util.UUID uuid = java.util.UUID.randomUUID();
        BluetoothSocket bS = null;
        try {
            bS = r.createRfcommSocketToServiceRecord(uuid);
        } catch ( java.io.IOException e ) {
            Log.d("MAINACT-BT","BluetoothSocket not created");
            return null;
        }
        Log.d("MAINACT-BT", "BluetoothSocket created");
        java.io.OutputStream oS;
        try {
            oS = bS.getOutputStream();
        } catch ( java.io.IOException e ) {
            Log.d("MAINACT-BT","OutputStream not created");
            return null;
        }
        java.io.PrintStream pS = new java.io.PrintStream(oS);
        Log.d("MAINACT-BT","NetworkAdapter created");
        return new NetworkAdapter(bS,pS);
    }
    private void createBTRequestDialog(String msg, String pos, String neg) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(pos,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(neg,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //todo just close the window
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /* Wifi Functionality */
    private boolean WFenabled() {
        //checks if WF is on
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return m.isWifiEnabled();
    }

    private void turnOnWF() {
        //creates a window alert: user permission to turn on WF
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    private void startWFGame() {
        //activity (settings): connects to wifi
        if ( !WFenabled() )
            turnOnWF();
        //fixme it jumps straight to error dialog before turnOnWF()
        /*
        ConnectivityManager m = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = m.getNetworkInfo(ConnectivityManager.TYPE_WIFI); //deprecated
        if ( wifi.isConnected() ) startGame();*/
        // /*
        WifiManager m = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifi = m.getConnectionInfo();
        if ( wifi.getNetworkId()!=-1 ) {
            connectPlayers();

            //Wait for connection thread to be done.
            while(true){
                if(connected){
                    break;
                }
            }


            //IF we are the client of the game
            if(client){
                game.getInstance().setUserClient(true);
            }


            //IF we are the server of the game
            else{
                game.getInstance().setUserClient(false);
            }
            startGame();
        }
        else createTryAgainDialog("Wifi not connected!","Try Again","Cancel");
    }

    private void connectPlayers(){

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                try{
                    //Connect to the connector socket
                    Socket connectorSocket = new Socket("192.168.1.75", 8003);

                    //To read from socket
                    BufferedReader in
                            = new BufferedReader(
                            new InputStreamReader(connectorSocket.getInputStream()));

                    //To write to socket
                    PrintWriter out
                            = new PrintWriter(
                            new OutputStreamWriter(connectorSocket.getOutputStream()));

                    //This is the other client's IP
                    String otherIP = in.readLine();


                    //This is the other client's PORT number
                    String otherPORT = in.readLine();

                    //This is stating whether This is the client or the server
                    String clientBoolean = in.readLine();

                    ServerSocket opponentServerSocket;

                    //If we are the client then create a socket to the server
                    if(clientBoolean.equals("true")) {
                        client = true;
                        SystemClock.sleep(1000);
                        opponentSocket = new Socket(otherIP, 2027);
                    }

                    //If we are the server create a server socket
                    else{
                        opponentServerSocket = new ServerSocket(2027);
                        opponentSocket = opponentServerSocket.accept();
                    }

                    game.getInstance().initializeAdapter(opponentSocket);

                    connected = true;
                }
                catch(IOException e){
                    e.printStackTrace();
                    Log.d("CONNECTING", e.toString());
                }
            }
        });
    }


    /* Creates a dialog for user confirmation */
    private void createTryAgainDialog(String msg, String positive, String negative) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(msg);
        alertDialogBuilder.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        setP2PSpinner(); //fixme what do when user wants to try connecting again?
                    }
                });
        alertDialogBuilder.setNegativeButton(negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //recreate(); //fixme what do when user doesn't want to connect?
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /* Starts the Battleship Game */
    private void startGame() {
        Intent intent = new Intent(getApplicationContext(),DeployShipActivity.class);
        startActivity(intent);
    }

    public void startAIGame(View view){
        Intent intent = new Intent(getApplicationContext(),DeployShipActivity.class);
        startActivity(intent);
    }

    public void connectPlayer(View view){
        switch ( conectionType ) {
            case 0: //Bluetooth
                startBTGame();
                break;
            case 1: //Wifi Direct
//                startWFDirectGame();
                break;
            case 2: //Wifi
                startWFGame();
                break;
        }
    }
}