package com.example.shane.salfeldtest1.featureUniqueID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView iD2;
    private TextView iD3;
    private TextView iD4;

    private static String uniqueID = null;
    private static String iMEI = null;
    private static String mAC = null;
    private static String secureID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                mTextMessage.setText("UUID:" + uniqueID);
                iD2.setText(iMEI);
                iD3.setText(mAC);
                iD4.setText(secureID);
                return true;
            } else if (id == R.id.navigation_dashboard) {
                mTextMessage.setText("Press Home");
                return true;
            } else if (id == R.id.navigation_notifications) {
                mTextMessage.setText("Press Home");
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uniqueID = getUuid(getApplicationContext());
        iMEI = getImei(getApplicationContext());
        mAC = getMobileMAC(getApplicationContext());
        uniqueID = getSecureID(getApplicationContext());
        mTextMessage = (TextView) findViewById(R.id.idHolder1);
        iD2 = (TextView) findViewById(R.id.idHolder2);
        iD3 = (TextView) findViewById(R.id.idHolder3);
        iD4 = (TextView) findViewById(R.id.idHolder4);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //UUID
    public synchronized static String getUuid(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
            iMEI = TelephonyMgr.getDeviceId();
        }
    }

    //IMEI
    public synchronized String getImei(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)

                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            String imei = telephonyManager.getDeviceId();
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "Catching IMEI Failed";
        }
    }

    //MAC
    public synchronized static String getMobileMAC(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String str = "";
            String macSerial = "02:00:00:00:00:00";
            try {
                WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = manager.getConnectionInfo();
                macSerial = info.getMacAddress();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return macSerial;
        } else {
            return "02:00:00:00:00:00";
        }
    }

    //Secure ID
    public synchronized static String getSecureID(Context context){
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidId;
    }
}
