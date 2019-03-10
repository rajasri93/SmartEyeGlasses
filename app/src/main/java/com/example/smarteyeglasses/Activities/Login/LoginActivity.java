package com.example.smarteyeglasses.Activities.Login;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.smarteyeglasses.R;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by aj on 10/14/16.
 */
public class LoginActivity extends AppCompatActivity {

    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        Log.d(LOG_TAG, "onCreate() called");

        if(savedInstaceState != null){

        }
        if(!isOnline()){
            handleOffline();
        }
        else {
            handleOnline();
        }
//        fbHashKey();
    }
    private void handleOnline(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.loginTab));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.signupTab));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final LoginPagerAdapter adapter = new LoginPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void handleOffline(){
        Toast.makeText(this,getString(R.string.offline_toast),Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.offline_display_message))
                .setCancelable(false);
        alertDialogBuilder.setPositiveButton(getString(R.string.offline_goto_settings), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 3);

            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.offline_close_app),
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                        finish();
                        System.exit(0);
                        //openHomeScreen();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 3){
            if(!isOnline()){
                handleOffline();
            }
            else handleOnline();
        }
    }

    private void fbHashKey(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.aj.scavengersworld",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    public boolean isOnline() {
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
             connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;


        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
            Log.v("connectivity", e.toString());
        }
        return connected;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "******* onStart() **********");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "******* onResume() **********");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "******* onPause() **********");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "******* onStop() **********");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "******* onDestroy() **********");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG, "******* onRestart() **********");
    }
}
