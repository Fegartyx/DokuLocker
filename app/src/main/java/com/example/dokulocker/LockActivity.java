package com.example.dokulocker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LockActivity extends AppCompatActivity {
    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        authenticateApp();
    }

    //method to authenticate app
    private void authenticateApp() {
        //Get the instance of KeyGuardManager
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //Check if the device version is greater than or equal to Lollipop(21)
        //Create an intent to open device screen lock screen to authenticate
        //Pass the Screen Lock screen Title and Description
        Intent i = keyguardManager.createConfirmDeviceCredentialIntent(getResources().getString(R.string.unlock), getResources().getString(R.string.confirm_pattern));
        try {
            //Start activity for result
            startActivityForResult(i, LOCK_REQUEST_CODE);
        } catch (Exception e) {

            //If some exception occurs means Screen lock is not set up please set screen lock
            //Open Security screen directly to enable patter lock
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            try {

                //Start activity for result
                startActivityForResult(intent, SECURITY_SETTING_REQUEST_CODE);
            } catch (Exception ex) {

                //If app is unable to find any Security settings then user has to set screen lock manually
                Log.i(TAG, "Lock Manually");
                //textView.setText(getResources().getString(R.string.setting_label));
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOCK_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    //If screen lock authentication is success update text
                    Intent intent = new Intent(LockActivity.this,MainActivity.class);
                    intent.putExtra("Value","Success");
                    startActivity(intent);
                    finish();
                    Log.i(TAG, "OK");
                } else {
                    //If screen lock authentication is failed update text
                    Log.i(TAG, "Failed");
                }
                break;
            case SECURITY_SETTING_REQUEST_CODE:
                //When user is enabled Security settings then we don't get any kind of RESULT_OK
                //So we need to check whether device has enabled screen lock or not
                if (isDeviceSecure()) {
                    //If screen lock enabled show toast and start intent to authenticate user
                    Toast.makeText(this, getResources().getString(R.string.device_is_secure), Toast.LENGTH_SHORT).show();
                    authenticateApp();
                } else {
                    //If screen lock is not enabled just update text
                    Log.i(TAG, "Cancelled");
                }
                break;
        }
    }

    /**
     * method to return whether device has screen lock enabled or not
     **/
    private boolean isDeviceSecure() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        //this method only work whose api level is greater than or equal to Jelly_Bean (16)
        return keyguardManager.isKeyguardSecure();

        //You can also use keyguardManager.isDeviceSecure(); but it requires API Level 23

    }
}
