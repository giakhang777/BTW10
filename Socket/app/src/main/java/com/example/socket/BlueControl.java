package com.example.socket;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BlueControl extends AppCompatActivity {
    ImageButton btnTb1, btnTb2, btnDis;
    TextView txt1, txtMAC;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Set<BluetoothDevice> pairedDevices1;
    String address = null;
    private ProgressDialog progress;
    int flaglamp1;
    int flaglamp2;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newint = getIntent();
        address = newint.getStringExtra(MainActivity.EXTRA_ADDRESS); //receive the address of the bluetooth device
        setContentView(R.layout.activity_blue_control);
        //ánh xạ
        btnTb1 = (ImageButton) findViewById(R.id.btnTb1);
        btnTb2 = (ImageButton) findViewById(R.id.btnTb2);
        txt1 = (TextView) findViewById(R.id.textV1);
        txtMAC = (TextView) findViewById(R.id.textViewMAC);
        btnDis = (ImageButton) findViewById(R.id.btnDis);
        new ConnectBT().execute();
        btnTb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietbi1();
            }
        });
        btnTb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thietbi7();
            }
        });
        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }
    private void thietbi1() {
        if (btSocket != null) {
            try {
                if (this.flaglamp1 == 0) {
                    this.flaglamp1 = 1;
                    this.btnTb1.setBackgroundResource(R.drawable.tb1on);
                    btSocket.getOutputStream().write("1".toString().getBytes());
                    txt1.setText("Thiết bị số 1 đang bật");
                    return;
                } else {
                    if (this.flaglamp1 == 1) return;
                    this.flaglamp1 = 0;
                    this.btnTb1.setBackgroundResource(R.drawable.tb1off);
                    btSocket.getOutputStream().write("A".toString().getBytes());
                    txt1.setText("Thiết bị số 1 đang tắt");
                    return;
                }
            } catch (IOException e) {

            }
        }
    }

    private void Disconnect() {
        if (btSocket != null)
        {
            try {
                btSocket.close();
            } catch (IOException e) {

            }
        }
        finish();
    }
    private void thietbi7() {
        if (btSocket != null) {
            try {
                if (this.flaglamp2 == 0) {
                    this.flaglamp2 = 1;
                    this.btnTb2.setBackgroundResource(R.drawable.tb7on);
                    btSocket.getOutputStream().write("7".toString().getBytes());
                    txt1.setText("Thiết bị số 7 đang bật");
                    return;
                } else {
                    if (this.flaglamp2 == 1) return;
                    this.flaglamp2 = 0;
                    this.btnTb2.setBackgroundResource(R.drawable.tb1off);
                    btSocket.getOutputStream().write("8".toString().getBytes());
                    txt1.setText("Thiết bị số 7 đang tắt");
                    return;
                }
            } catch (IOException e) {

            }
        }
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true;
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(BlueControl.this, "Đang kết nối...", "Xin vui lòng đợi!!!");
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    if (ActivityCompat.checkSelfPermission(BlueControl.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);
                        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();//discovery is a heavy process; needs to be cancelled
                        btSocket.connect();
                    }
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if(!ConnectSuccess)
            {
                msg("Kết nối thất bại! Kiểm tra thiết bị.");
                finish();
            }
            else
            {
                msg("Kết nối thành công.");
                isBtConnected = true;
                pairedDevicesList1();
            }
            progress.dismiss();
        }
    }
    private void msg(String s){
        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
    }
    private void pairedDevicesList1()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            pairedDevices1 = myBluetooth.getBondedDevices();
            if (pairedDevices1.size() > 0) {
                for(BluetoothDevice bt : pairedDevices1) //Get the device's name and the address
                {
                    txtMAC.setText(bt.getName() + " - " + bt.getAddress());
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),  "Không tìm thấy thiết bị đã kết nối.", Toast.LENGTH_LONG).show();
            }
        }
    }
}