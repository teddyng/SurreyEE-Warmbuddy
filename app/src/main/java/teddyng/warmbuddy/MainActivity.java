package teddyng.warmbuddy;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button startbtn, stopbtn, disconnectbtn;
    TextView tempdisplay, tempreadout;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter mBTAdapter = null;
    BluetoothSocket mBTSocket = null;
    private boolean isBtConnected = false;
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private CountDownTimer timer;

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
        tempreadout = (TextView) findViewById(R.id.tempreadout);


        new ConnectBT().execute();

        final Random r = new Random();

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("1");

                int i1 = r.nextInt(375 - 360) + 360;
                final double i2 = i1 / 10.0;
                final String readout = Double.toString(i2);
                final String readoutFull = readout + " °C";
                tempreadout.setText(readoutFull);

            }
        });

        stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                sendSignal("0");
                timer.cancel();
            }
        });

        disconnectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                Disconnect();
            }
        });

        TextView textView = (TextView) findViewById(R.id.warmbuddy_title2);
        Spannable word = new SpannableString("WARM ");

        word.setSpan(new ForegroundColorSpan(0xFFFF8800), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(word);
        Spannable wordTwo = new SpannableString("BUDDY");

        wordTwo.setSpan(new ForegroundColorSpan(Color.WHITE), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.append(wordTwo);

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