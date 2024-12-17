package com.chanu.childtask;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class common {

    private static ProgressDialog progressDialog;
    public static String rName, rEmail, rPassword, rType, rDBEmail, childEmail, parentEmail, nick, taskID; // r mean reuse
    public static boolean isGoogleLogin, isLogin, willSend=false, willGoogleLogin=false, isParent;
    public static void showMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // dismisses the dialog
            }
        });
        builder.show();
    }

    public static String generateOTP() {
        final String NUMBERS = "0123456789";
        final int OTP_LENGTH = 6;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    public static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void startLoading(Context context, String title){
        progressDialog = ProgressDialog.show(context,title,"Please wait...",false,false);
    }

    public static void stopLoading(){
        progressDialog.dismiss(); // = ProgressDialog.show(context,title,"Please wait...",false,false);
    }

    public static String getCTime(){
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(now);
    }

    public static String getCDate(){
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(now);
    }

    public static String getCDateTime(){
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(now);
    }

    public static String convertToTimeFormat(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm:ss");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToDateFormat(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }

}
