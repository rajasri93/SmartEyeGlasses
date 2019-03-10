package com.example.smarteyeglasses.Activities.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smarteyeglasses.Activities.HomeScreenActivity;
import com.example.smarteyeglasses.R;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link LoginFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link LoginFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class LoginFragment extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private final String LOG_TAG = getClass().getSimpleName();

    private Button mLoginButton;
    private EditText mUsername;
    private EditText mPassword;
    private TextView mTextView;

    /* GOOGLE SIGNIN STUFF */
    private GoogleApiClient mGoogleApiClient;
    private final int RC_SIGN_IN = 1;


    /* FIREBASE STUFF */
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    /* FACEBOOK STUFF */
    private CallbackManager mCallbackManager;
    private FacebookCallback<LoginResult> mCallbackResult = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            firebaseAuthWithFacebook(loginResult.getAccessToken());
//            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayWelcomeMessage(profile);
        }

        @Override
        public void onCancel() {
            Log.d(LOG_TAG, "Cancelled");

        }

        @Override
        public void onError(FacebookException error) {
            Log.d(LOG_TAG, "FB error");
        }
    };

    private void displayWelcomeMessage(Profile profile){
        if(profile != null){
            mTextView.setText("Welcome "+profile.getName());
        }
    }

    private AccessTokenTracker mAccessTokenTracker;
    private ProfileTracker mProfileTracker;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        AppEventsLogger.activateApp(getActivity());
        mCallbackManager = CallbackManager.Factory.create();

        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            }
        };

        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                displayWelcomeMessage(currentProfile);
            }
        };

        Log.d(LOG_TAG, "onCreate()");
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser = mFirebaseAuth.getCurrentUser();

                if(mFirebaseUser != null){
                    // User is signed in
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + mFirebaseUser.getUid());
                    setupSession();
                    openHomeScreen();
                }
                else{
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void firebaseAuthWithFacebook(AccessToken token){
        Log.d(LOG_TAG, "firebaseAuthWithFacebook:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(getActivity(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            loginFailed(task);
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
//                            Toast.makeText(getActivity(), "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            loginFailed(task);
                        }
                    }
                });
    }

    private void showAlertDialogue(int titleString, int messageString){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(messageString)
                .setTitle(titleString)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void validateAndLogin(String email, String password){
        Log.d(LOG_TAG, "validateAndLogin() called");
        if(email == null || !email.contains("@")){
            showAlertDialogue(R.string.invalidEmailTitle, R.string.invalidEmailMessage);
            return;
        }
        if(password == null || password.equals("")){
            showAlertDialogue(R.string.passwordNotEnteredTitle, R.string.passwordNotEnteredMessage);
            return;
        }
//        if(){
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
//                            mFirebaseUser = task.getResult().getUser();
//                            setupSession();
//                            openHomeScreen();
                            }
                            else{
                                loginFailed(task);
                            }
                        }
                    });
//        }
//        else{
//            showAlertDialogue(R.string.login_error_title, R.string.loginFailedMessage);
//            loginFailed("Please enter email and password");
//        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(LOG_TAG, "onStart() called");
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LOG_TAG, "onStop() called");
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume() called");
        Profile profile = Profile.getCurrentProfile();
        displayWelcomeMessage(profile);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        bindUItoVariables(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(LOG_TAG, "onViewCreated()");
        bindUItoVariables(view);
        LoginButton fbLoginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
//        fbLoginButton.setReadPermissions("user_friends");
        fbLoginButton.setReadPermissions("email", "public_profile");
        fbLoginButton.setFragment(this);
        fbLoginButton.registerCallback(mCallbackManager, mCallbackResult);

        mTextView = (TextView) view.findViewById(R.id.welcomeText);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult()");
        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
//            mTextView.setText(getString(R.string.signed_in_fmt, account.getDisplayName()));
//            openHomeScreen(createUser(account));
        } else {
            showAlertDialogue(R.string.googleLoginFailedTitle, R.string.googleLoginFailedMessage);
//            loginFailed("Failed to login with Google Account");
            // Signed out, show unauthenticated UI.
//            updateUI(false);
        }
    }

    private void bindUItoVariables(View view){
        Log.d(LOG_TAG, "bindUItoVariables()");
        if(view != null) {
            mLoginButton = (Button) view.findViewById(R.id.btnLogin);
            mLoginButton.setOnClickListener(this);

            mUsername = (EditText) view.findViewById(R.id.loginUsername);
            mPassword = (EditText) view.findViewById(R.id.loginPassword);

            view.findViewById(R.id.btnGoogleLogin).setOnClickListener(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(LOG_TAG, "onActivityCreated()");
        restoreUIVariables(savedInstanceState);
        if(mFirebaseUser != null){
            openHomeScreen();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState()");
        saveUIVariables(outState);
    }

    private void restoreUIVariables(Bundle savedInstanceState){
        Log.d(LOG_TAG, "restoreUIVariables()");
        if(savedInstanceState != null){
            mUsername.setText(savedInstanceState.getString(getString(R.string.username), ""));
            mPassword.setText(savedInstanceState.getString(getString(R.string.password), ""));
        }
    }

    private Bundle saveUIVariables(Bundle outState){
        Log.d(LOG_TAG, "saveUIVariables()");
        if(outState != null){
            outState.putString(getString(R.string.username), mUsername.getText().toString());
            outState.putString(getString(R.string.password), mPassword.getText().toString());
        }
        return outState;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                performLogin();
                break;
            case R.id.btnGoogleLogin:
                googleSignIn();
                break;
            default:
                break;
        }

    }

    private void googleSignIn(){
        Log.d(LOG_TAG, "googleSignIn() called");
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    private void performLogin(){
        Log.d(LOG_TAG, "performLogin()");
        if(mUsername.getText() != null && mPassword.getText() != null &&
                mUsername.getText().toString() != null && mUsername.getText().toString() != "" &&
                mPassword.getText().toString() != null && mPassword.getText().toString() != ""){
            validateAndLogin(mUsername.getText().toString(), mPassword.getText().toString());
        }
    }

    private void openHomeScreen(){
        Log.d(LOG_TAG, "openHomeScreen() called");
        Intent homeScreen = new Intent(getActivity(), HomeScreenActivity.class);
        homeScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeScreen);
        getActivity().finish();
    }

//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

//    private User createUser(){
//
//        List<Hunt> mCreatedHuntList = new ArrayList<>();
//        List<Hunt> mRegisteredHuntList = new ArrayList<>();
//
//        User user = new User();
//        user.setUserId("abcd");
//        user.setUserEmail("qwerty@xyz.com");
//        user.setDisplayName("ANIRUDH");
//
////        Hunt h = new Hunt();
////        h.
////
////        createdHuntList.add(new Hunt());
//
//        return user;
//    }

//    private User createUser(FirebaseUser firebaseUser){
//        User user = new User();
//        user.setUserId(firebaseUser.getUid());
//        user.setUserName(firebaseUser.getEmail());
//        user.setDisplayName(firebaseUser.getDisplayName());
//        return user;
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setupSession(){
        //UserSessionManager sessionManager = UserSessionManager.INSTANCE;
        //sessionManager.setUpSession(mFirebaseUser, getContext());
    }

    private void loginFailed(Task<AuthResult> task){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(task.getException().getMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginFailed(String errMsg){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(errMsg)
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
