package teddyng.warmbuddy;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button startbtn, stopbtn, disconnectbtn;
    TextView tempdisplay;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter mBTAdapter = null;
    BluetoothSocket mBTSocket = null;
    private boolean isBtConnected = false;
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);

        startbtn = (Button) findViewById(R.id.startbtn);
        stopbtn = (Button) findViewById(R.id.stopbtn);
        disconnectbtn = (Button) findViewById(R.id.disconnectbtn);
        tempdisplay = (TextView) findViewById(R.id.tempdisplay);

        new ConnectBT().execute();

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("1");
            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("0");
            }
        });

        disconnectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });
    }

    private void sendSignal ( String number ) {
        if ( mBTSocket != null ) {
            try {
                mBTSocket.getOutputStream().write(number.toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public void receiveData() throws IOException{
        InputStream socketInputStream =  mBTSocket.getInputStream();
        byte[] buffer = new byte[256];
        int bytes;
        
        while (true) {
            try {
                bytes = socketInputStream.read(buffer);
                String readMessage = new String(buffer, 0, bytes);
                Log.i("logging", readMessage + "");
            } catch (IOException e) {
                break;
            }
        }

    }

    private void Disconnect () {
        if ( mBTSocket !=null ) {
            try {
                mBTSocket.close();
            } catch(IOException e) {
                msg("Error");
            }
        }

        finish();
    }
/*

    public void run() {
        byte[] buffer = new byte[256];
        int bytes;

        // Keep looping to listen for received messages
        while (true) {
            try {
                bytes = mmInStream.read(buffer);            //read bytes from input buffer
                String readMessage = new String(buffer, 0, bytes);
                // Send the obtained bytes to the UI Activity via handler
                bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
            } catch (IOException e) {
                break;
            }
        }
    }
*/

    private void msg (String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected  void onPreExecute () {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please Wait!!!");
        }

        @Override
        protected Void doInBackground (Void... devices) {
            try {
                if ( mBTSocket ==null || !isBtConnected ) {
                    mBTAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = mBTAdapter.getRemoteDevice(address);
                    mBTSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(BTUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBTSocket.connect();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Please try again.");
                finish();
            } else {
                msg("Connected");
                isBtConnected = true;
            }

            progress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
    }
}