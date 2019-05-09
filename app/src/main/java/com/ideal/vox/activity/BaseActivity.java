package com.ideal.vox.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ideal.vox.R;
import com.ideal.vox.di.BaseApp;
import com.ideal.vox.di.module.ActivityModule;
import com.ideal.vox.retrofitManager.ApiClient;
import com.ideal.vox.retrofitManager.ApiInterface;
import com.ideal.vox.retrofitManager.ApiManager;
import com.ideal.vox.retrofitManager.ResponseListener;
import com.ideal.vox.utils.Const;
import com.ideal.vox.utils.ImageUtils;
import com.ideal.vox.utils.NetworkUtil;
import com.ideal.vox.utils.PermissionsManager;
import com.ideal.vox.utils.PrefStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import javax.inject.Inject;

import retrofit2.Call;

import static com.ideal.vox.utils.UtilsKt.debugLog;

/**
 * Created by Neeraj Narwal on 2/7/15.
 */
public class BaseActivity extends AppCompatActivity implements View.OnClickListener, ResponseListener {
    @Inject
    public PrefStore store;
    //    @Inject
    ApiManager apiManager;
    @Inject
    ApiClient apiClient;

    public ApiInterface apiInterface;
    private Toast toast;
    public Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        AndroidInjection.inject(this);
        ((BaseApp) getApplication()).getInjection().activityModule(new ActivityModule(this)).build().inject(this);
        super.onCreate(savedInstanceState);

//        NetworkUtil.INSTANCE.initialize(this);
//        new NetworkUtil(this).initialize();
        createPicassoDownloader();
        strictModeThread();
        apiInterface = apiClient.getClient().create(ApiInterface.class);
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        checkDate();
    }

    public boolean initFCM() {
        boolean isAvailable = checkPlayServices();
        if (isAvailable) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String refreshedToken = instanceIdResult.getToken();
                    store.saveString(Const.DEVICE_TOKEN, refreshedToken);
                    log("Token >>>>>>>  " + refreshedToken);
                }
            });
        }
        return isAvailable;
    }


    private void createPicassoDownloader() {
        OkHttpClient okHttpClient = new OkHttpClient();
        picasso = new Picasso.Builder(this)
                .downloader(new OkHttpDownloader(okHttpClient))
                .build();
    }

    @Override
    public void onSuccess(@NotNull Call<?> call, @NotNull Object payload) {

    }

    @Override
    public void onError(@NotNull Call<?> call, int statusCode, @NotNull String errorMessage, @NotNull ResponseListener responseListener) {
        switch (statusCode) {
            case Const.ErrorCodes.SOCKET_TIMEOUT:
                showRetry(call, responseListener);
                break;
            case Const.ErrorCodes.SESSION_ERROR:
                showToast(errorMessage, true);
//                logoutUser(this);
                break;
            case Const.ErrorCodes.NO_INTERNET:
                showToast("Bad Internet Connection", true);
                break;
            default:
                showToast("Error : " + errorMessage, true);
                call.cancel();
                break;
        }
    }

    private void showRetry(final Call call, final ResponseListener responseListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert !");
        builder.setMessage("Request timeout");
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Call call1 = call.clone();
                apiManager.makeApiCall(call1, responseListener);
            }
        });
        builder.create().show();
    }

    protected void checkDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2017, Calendar.MARCH, 31);
        Calendar currentcal = Calendar.getInstance();
        if (currentcal.after(cal)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please contact : Admin");
            builder.setTitle("Demo Expired");
            builder.setCancelable(false);
            builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exitFromApp();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                log(getString(R.string.this_device_is_not_supported));
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.INSTANCE.onPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageUtils.Companion.activityResult(requestCode, resultCode, data);
    }

    public void exitFromApp() {
        finish();
        finishAffinity();
    }


    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                    .getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {
        }
    }

    public void log(String string) {
        debugLog("BaseActivity", string);
    }

    public void showExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.are_you_sure_want_to_exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        finishAffinity();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void showToast(String msg, boolean isError) {
        View view = View.inflate(this, R.layout.layout_toast, null);
        TextView toastTV = view.findViewById(R.id.toastTV);
        if (isError) {
            LinearLayout parentLL = view.findViewById(R.id.parentLL);
            parentLL.setBackgroundColor(ContextCompat.getColor(this, R.color.IndianRed));
        }
        toastTV.setText(msg);
        toast.setView(view);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void strictModeThread() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void transitionSlideInHorizontal() {
        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
    }

    @Override
    public void onClick(View v) {

    }
}