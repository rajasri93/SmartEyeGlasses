package com.example.smarteyeglasses.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.smarteyeglasses.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreenActivity extends BaseActivity {

    /* FIREBASE STUFF */
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        String username = mFirebaseUser.getDisplayName();

        mTextView = (TextView) findViewById(R.id.textView);
        mTextView.setText(username);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_home_screen;
    }

    @Override
    protected String getScreenName() {
        return "Hello";
    }
}
