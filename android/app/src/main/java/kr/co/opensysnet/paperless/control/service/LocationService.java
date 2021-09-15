package kr.co.opensysnet.paperless.control.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import kr.co.opensysnet.paperless.MyApplication;
import kr.co.opensysnet.paperless.control.listener.Listener;
import kr.co.opensysnet.paperless.utils.DebugLog;

public class LocationService extends Service {
    private static final String TAG = "LocationService";

    private Context mContext;

    private FusedLocationProviderClient mFusedLocationClient = null;
    private LocationCallback mLocationCallback = null;
    private LocationRequest mLocationRequest = null;

    private Listener.ILocationListener mFragmentListener;

    private final IBinder mBinder = new LocalBinder();

    private int mInterval = 0;

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener((Activity) mContext, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mFusedLocationClient = null;
                }
            });
        }

        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    private void startLocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        createLocationCallback();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(mInterval);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        getCurrentLocation();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                MyApplication app = (MyApplication) mContext.getApplicationContext();
                app.setLocation(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

                if (mFragmentListener != null) {
                    mFragmentListener.onSendLocation(locationResult.getLastLocation());
                }else {
                    DebugLog.e(TAG, "fragment listener is null");
                }
            }
        };
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    public void setContextWithListener(Context context, int interval) {
        if (interval > 0) {
            this.mInterval = interval;
        }

        if (context != null) {
            this.mContext = context;
            startLocation();
        }
    }

    public void setFragmentListener(Fragment fragment) {
        this.mFragmentListener = (Listener.ILocationListener) fragment;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
