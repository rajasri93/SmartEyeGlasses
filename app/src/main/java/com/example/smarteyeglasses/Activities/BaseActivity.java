package com.example.smarteyeglasses.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.smarteyeglasses.Activities.Login.LoginActivity;
import com.example.smarteyeglasses.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by aj on 10/14/16.
 */
public abstract class BaseActivity extends AppCompatActivity {

    Toolbar toolbar;

    private final String LOG_TAG = getClass().getSimpleName()+" Toolbar";
    private int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate()");
        setContentView(getLayoutResource());
        configureToolbar();
    }

    protected abstract int getLayoutResource();

    protected abstract String getScreenName();

    private void configureToolbar() {
        Log.d(LOG_TAG, "configureToolbar()");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
//            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_home_white_24dp);
            getSupportActionBar().setTitle(getScreenName());

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(LOG_TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(LOG_TAG, "onOptionsItemSelected()");
        switch (item.getItemId()) {
            case android.R.id.home:
//                FragmentManager fm = getSupportFragmentManager();
//                if (fm != null && fm.getBackStackEntryCount() > 0) {
//                    fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                } else {
//                    finish();
//                }
//                onBackPressed();
                openHomeScreen();
                return true;
            case R.id.signout:
                signout();
                return true;
            case R.id.profile:
                openProfileActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openProfileActivity(){
        Log.d(LOG_TAG, "openProfileActivitiy()");
        //Intent profileActivity = new Intent(this, ProfileActivity.class);
        //startActivity(profileActivity);
    }

    private void signout(){
        Log.d(LOG_TAG, "signout()");
        //TODO: use UserSessionManager to retireve username and signout
        FirebaseAuth.getInstance().signOut();
        clearSession();
        openLoginScreen();
    }

    private void openLoginScreen(){
        Intent login = new Intent(this, LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
    }

    protected void openHomeScreen(){
        homeButtonClicked();
        Intent homeScreen = new Intent(this, HomeScreenActivity.class);
        homeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeScreen);
    }

    protected void homeButtonClicked(){

    }

    private void clearSession(){
        //UserSessionManager sessionManager = UserSessionManager.INSTANCE;
        //sessionManager.clearSession();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
