package com.example.smarteyeglasses.interfaces;

public interface MainActivityInterface {
    void checkCurrentUser();

    void getUserProfile();

    void getProviderData();

    void updateProfile();

    void updateEmail();

    void updatePassword();

    void sendEmailVerification();

    void sendEmailVerificationWithContinueUrl();

    void sendPasswordReset();

    void deleteUser();

    void reauthenticate();

    void authWithGithub();

}
