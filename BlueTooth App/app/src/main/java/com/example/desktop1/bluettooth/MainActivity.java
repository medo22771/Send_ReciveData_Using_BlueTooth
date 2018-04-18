package com.example.desktop1.bluettooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button btn_On;
    private Button btn_Off;
    private Button btn_List_Devices;
    private Button btn_Discover;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView DevicesList;

    private static final int REQUEST_ENABLED = 0;
    private static final int REQUEST_DISCOVERABLE = 0;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView lvNewDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null)
        {
            Toast.makeText(MainActivity.this, "BlueTooth not Supported", Toast.LENGTH_LONG).show();
            finish();
        }

        InitializeUIObjects();
        Listeners();
    }

    public void InitializeUIObjects()
    {
        btn_On = (Button)findViewById(R.id.btn_BT_On);
        btn_Off = (Button)findViewById(R.id.btn_BT_Off);
        btn_List_Devices = (Button)findViewById(R.id.btn_List_Devices);
        btn_Discover = (Button)findViewById(R.id.btn_Discover);
        //DevicesList = (ListView)findViewById(R.id.devicesList);

        lvNewDevices = (ListView) findViewById(R.id.devicesList);
        mBTDevices = new ArrayList<>();
    }

    public void Listeners()
    {
        btn_On.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLED);
            }
        });

        btn_Off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.disable();
            }
        });

        btn_Discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mBluetoothAdapter.isDiscovering())
                {
                    mBluetoothAdapter.startDiscovery();
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(intent, REQUEST_DISCOVERABLE);
                }
            }
        });



//        btn_List_Devices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i("Test", "Here");
//                Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
//                ArrayList<String> Devices = new ArrayList<String>();
//
//                if(pairedDevices.size() > 0 )
//                    ;
//                else
//                    Log.d("Test", "No Devices");
//
//                Log.i("Test", "Here2: " + btAdapter.isDiscovering());
//                for(BluetoothDevice BTDevice : pairedDevices)
//                {
//
//                    Devices.add(BTDevice.getName());
//                    Log.i("Test", BTDevice.getName());
//                }
//
//                Log.i("Test", "Here3");
//                ArrayAdapter arrAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1,Devices);
//
//                DevicesList.setAdapter(arrAdapter);
//            }
//        });
    }

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("Example", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d("Example", "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, mBTDevices);
                lvNewDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    public void btnDiscover(View view) {
        Log.i("Example", "btnDiscover: Looking for unpaired devices.");

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d("Example", "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            //checkBTPermissions();
            Log.d("Example", "Here1");

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    private void checkBTPermissions() {
        Log.d("Example", "Here2");
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            Log.d("Example", "Here3");
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            //permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            Log.d("Example", "Here4");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d("Example", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

}
