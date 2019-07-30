package com.ideal.vox.utils;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

public class Const {

    public static final String SERVER_REMOTE_URL = "http://app.idealvox.com/public/api/";
    public static final String IMAGE_BASE_URL = "http://app.idealvox.com/storage/app/public/avatars/";
    public static final String IMAGE_SLIDER_BASE_URL = "http://app.idealvox.com/storage/app/public/slider_images/";
    public static final String IMAGE_BANK_BASE_URL = "http://app.idealvox.com/storage/app/public/passbook_pic/";
    public static final String IMAGE_ACC_BASE_URL = "http://app.idealvox.com/storage/app/public/accessorie_pic/";
    public static final String IMAGE_ALBUM_BASE_URL = "http://app.idealvox.com/storage/app/public/album_pic/";

    public static final String API_KEY = "AIzaSyAY1cezRA0mbLqSR2g6tVR7xdDKkJme3Kk";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1234;
    public static final String NOTI_CHANNEL_ID = "my_custom_channel";
    public static final int REQUEST_CODE_CHOOSE = 1313;
    /**
     * ******************************* App buy or Purchase Key *************************************
     */
    public static final String DEVICE_TOKEN = "dev_token";
    public static final String FOREGROUND = "forground_notification";
    public static final String SESSION_KEY = "session_key";
    public static final String USER_DATA = "user_data";
    public static final String GUEST_DIRECT = "guest";
    public static final String SHOW_HELP_DIALOG = "show_help_dialog";
    public static final String IS_FIRST_RUN = "is_first_run";

    public static String getPackageName(Context context){
        //Type other package name if playstore id is different
        return context.getPackageName();
    }

    public static class ErrorCodes {
        public static final int SERVER_ERROR = 500;
        public static final int SOCKET_TIMEOUT = 598;
        public static final int SESSION_ERROR = 600;
        public static final int NO_INTERNET = 601;
    }
}