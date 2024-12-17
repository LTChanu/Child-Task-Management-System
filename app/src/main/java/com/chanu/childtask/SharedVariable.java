package com.chanu.childtask;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.ValueEventListener;

public class SharedVariable {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_IS_PARENT = "isParent";//must update with login & Register
    private static final String KEY_IS_LOGIN = "isLogin";//must update with login & Register & Logout
    private static final String KEY_IS_GOOGLE = "isGoogle";//must update with login & Register
    private static final String KEY_MOOD = "mood";
    private static final String KEY_DBEMAIL = "dbEmail";//must update with login & Register
    private static final String KEY_NOTIFICATION_LIST = "notificationList";
    private Context mContext;
    String notificationList;


    public SharedVariable(MyService registration) {
        mContext = registration;
    }

    public SharedVariable(Home home) {
        mContext = home;
    }

    public SharedVariable(Registration registration) {
        mContext = registration;
    }

    public SharedVariable(EmailVerification emailVerification) {
        mContext = emailVerification;
    }

    public SharedVariable(Welcome welcome) {
        mContext = welcome;
    }

    public SharedVariable(SignIn signIn) {
        mContext = signIn;
    }

    public SharedVariable(Option option) {
        mContext = option;
    }


    public SharedVariable(DeleteAccount deleteAccount) {
        mContext =deleteAccount;
    }

    public boolean getIsParent() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_PARENT, false);
    }

    public void setIsParent(boolean isParent) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_PARENT, isParent);
        editor.apply();
    }

    public String getMood() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_MOOD, "unknown");
    }

    public void setMood(String mood) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_MOOD, mood);
        editor.apply();
    }

    public String getNotificationList() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NOTIFICATION_LIST, "unknown");
    }

    public void setNotificationList(String notification) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        notificationList = prefs.getString(KEY_NOTIFICATION_LIST, "unknown");
        notificationList = notificationList + ", " + notification;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NOTIFICATION_LIST, notificationList);
        editor.apply();
    }

    public void clearNotificationList() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_NOTIFICATION_LIST);
        editor.apply();
    }

    public boolean getIsLogIn() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setIsLogIn(boolean isLogIn) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_LOGIN, isLogIn);
        editor.apply();
    }

    public boolean getIsGoogle() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_GOOGLE, false);
    }

    public void setIsGoogle(boolean isGoogle) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_GOOGLE, isGoogle);
        editor.apply();
    }

    public String getDBEmail() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DBEMAIL, "unknown");
    }

    public void setDBEmail(String dbEmail) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DBEMAIL, dbEmail);
        editor.apply();
    }

    public void setWhileLogin(String dbEmail, boolean isParent, boolean isLogIn, boolean isGoogle) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DBEMAIL, dbEmail);
        editor.putBoolean(KEY_IS_PARENT, isParent);
        editor.putBoolean(KEY_IS_GOOGLE, isGoogle);
        editor.putBoolean(KEY_IS_LOGIN, isLogIn);
        editor.apply();
    }

}

//    SharedVariable sharedVariable = new SharedVariable(this);
//    sharedVariable.setIsParent(true); // Set the boolean value to true
//
//        SharedVariable sharedVariable = new SharedVariable(this);
//        boolean isParent = sharedVariable.isParent(); // Get the boolean value
