package kr.co.opensysnet.paperless.view.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kr.co.opensysnet.paperless.MyApplication;
import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.control.listener.Listener;
import kr.co.opensysnet.paperless.control.service.LocationService;
import kr.co.opensysnet.paperless.databinding.FragmentStoreMapBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqStore;
import kr.co.opensysnet.paperless.model.response.RespStore;
import kr.co.opensysnet.paperless.model.response.RespStoreInfo;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreMapFragment extends BaseFragment implements OnMapReadyCallback, Listener.ILocationListener {
    private static final String TAG = "StoreMapFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentStoreMapBinding mBindingViews = null;

    private View mMarkerView;
    private TextView mMarkerTextView;
    private LinearLayout mMarkerBackground;

    private GoogleMap mGoogleMap;
    private MapView mMapView;
    private double mLatitude = 0, mLongitude = 0;

    private long mFinishTime = 0L;

    private List<RespStoreInfo> mStoreList;
    private int mStoreCode = 0;

    private double mScanLatitude = 0, mScanLongitude = 0;
    private boolean mDuringScan = false;

    public static StoreMapFragment newInstance(Bundle bundle) {
        StoreMapFragment fragment = new StoreMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        if (System.currentTimeMillis() - mFinishTime >= 3000) {
            mFinishTime = System.currentTimeMillis();
            Toast.makeText(mContext, R.string.txt_exit_check, Toast.LENGTH_LONG).show();
            return true;
        } else {
            System.exit(0);
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_store_map, container, false);
        mMarkerView = LayoutInflater.from(mContext).inflate(R.layout.marker_store_map, null);
        mMarkerBackground = (LinearLayout) mMarkerView.findViewById(R.id.marker_store_map_ll_container);
        mMarkerTextView = (TextView) mMarkerView.findViewById(R.id.marker_store_map_tv_name);
        return mBindingViews.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mBindingViews.storeMapMvGoogleMap;
        mMapView.onCreate(savedInstanceState);
        MapsInitializer.initialize(mContext);
        mMapView.getMapAsync(this);

        init();
    }

    private void init() {
        initToolbar();
        initView();
        initMap();
    }

    private void initToolbar() {
        View actionBar = initActionBar(mBindingViews.storeMapToolbar, R.layout.toolbar_image_text);
        if (actionBar != null){
            mToolbarBindingViews = DataBindingUtil.bind(actionBar);

        }
        if (mToolbarBindingViews != null){
            mToolbarBindingViews.toolbarTvTitle.setText(R.string.toolbar_title_store_map);
            mToolbarBindingViews.toolbarIvLeftIcon.setImageResource(R.drawable.ic_menu);

            mToolbarBindingViews.toolbarFlLeft.setOnClickListener(mToolbarOnClickListener);
        }
    }

    private final View.OnClickListener mToolbarOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view == mToolbarBindingViews.toolbarFlLeft) {
            ((MainActivity) mContext).drawerActive(true);
        }
    };

    private void initView() {

    }

    private View.OnClickListener mViewOnClickListener = view -> {
        hideSoftKeyboard(view);
    };

    private void initMap() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MainActivity) mContext).setFragmentListener(StoreMapFragment.this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {
            mMapView.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onDestroy() {

        if (mMapView != null) {
            mMapView.onStart();
        }
        super.onDestroy();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng myLocation;
        if (mLatitude == 0 && mLongitude == 0) {
            MyApplication app = (MyApplication) mContext.getApplicationContext();
            myLocation = new LatLng(app.getLatitude(), app.getLongitude());
        } else {
            myLocation = new LatLng(mLatitude, mLongitude);
        }
        DebugLog.i(TAG, "ready location is (" + myLocation.latitude + ", " + myLocation.longitude + ")");
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
        mGoogleMap.setOnMarkerClickListener(mMarkerOnClickListener);
        mGoogleMap.setOnCameraIdleListener(mCameraIdleListener);
    }

    private final GoogleMap.OnMarkerClickListener mMarkerOnClickListener = marker -> {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        return true;
    };

    private final GoogleMap.OnCameraIdleListener mCameraIdleListener = () -> {
        double cameraLat = mGoogleMap.getCameraPosition().target.latitude;
        double cameraLon = mGoogleMap.getCameraPosition().target.longitude;


        if (isFarOverOneSec(mScanLatitude, cameraLat) || isFarOverOneSec(mScanLongitude, cameraLon)) {
            reqGetStoreList(
                    cameraLat,
                    cameraLon,
                    (int) mGoogleMap.getCameraPosition().zoom);
        } else {
            DebugLog.e(TAG, "camera location is too close");
        }
    };

    // 1초가 약 30미터 정도 차이가 있어서 위도나 경도 중에 하나가 1초 이상 차이나는지 계산하여 반환
    private boolean isFarOverOneSec(double deg1, double deg2) {
        if (Math.floor(deg1) != Math.floor(deg2)) return true;

        double min1 = (deg1 % 1) * 60;
        double min2 = (deg2 % 1) * 60;
        if (Math.floor(min1) != Math.floor(min2)) return true;

        double sec1 = (min1 % 1) * 60;
        double sec2 = (min2 % 1) * 60;

        double dif = Math.abs(sec1 - sec2);
        DebugLog.e(TAG, "Target1 : " + deg1 + ", " + min1 + ", " + sec1);
        DebugLog.e(TAG, "Target2 : " + deg2 + ", " + min2 + ", " + sec2);
        DebugLog.e(TAG, "different : " + dif);
        if (dif > 1) return true;
        else return false;
    }

    @Override
    public void onSendLocation(Location location) {
        DebugLog.i(TAG, "onSendLocation start");
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        DebugLog.i(TAG, "received location is (" + mLatitude + ", " + mLongitude + ")");
        updateLocationUI();
    }

    private void updateLocationUI() {
        DebugLog.i(TAG, "updateLocation start");
        if (mGoogleMap == null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            mGoogleMap.setMyLocationEnabled(false);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    private void reqGetStoreList(double lat, double lon, int zoom) {
        if (mDuringScan) {
            DebugLog.e(TAG, "is during scan");
            return;
        }

        mDuringScan = true;
        mScanLatitude = lat;
        mScanLongitude = lon;

        int code = ++mStoreCode;
        ReqStore param =  new ReqStore();
        param.setLatitude(String.valueOf(lat));
        param.setLongitude(String.valueOf(lon));
        DebugLog.e(TAG, "zoom " + zoom);
        int range = (int) (35200000/(Math.pow(2, zoom)));
        if (range < 300) range = 300;
        if (range > 2148) range = 2148;
        DebugLog.e(TAG, "map scale " + range);
        param.setScale(range);

        ReqBase<ReqStore> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespStore<RespStoreInfo>> call = RetrofitService.getInstance().getService().reqStoreList(body);
        call.enqueue(new Callback<RespStore<RespStoreInfo>>() {
            @Override
            public void onResponse(Call<RespStore<RespStoreInfo>> call, Response<RespStore<RespStoreInfo>> response) {
                RespStore<RespStoreInfo> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    return;
                }
                switch (responseData.getResultCode()) {
                    case Constants.ServerResultCode.SUCCESS:
                        if (code != mStoreCode) {
                            DebugLog.e(TAG, "code not matched doing nothing");
                            break;
                        }
                        if (mStoreList == null) mStoreList = new ArrayList<>();
                        mStoreList.clear();
                        mStoreList.addAll(responseData.getStoreList());
                        setMarker();
                        mDuringScan = false;
                        break;
                    case Constants.ServerResultCode.NOT_FOUND:
                        DebugLog.e(TAG, "store list not found");
                        mDuringScan = false;
                        break;
                    default:
                        DebugLog.e(TAG, "get store list failure");
                        mDuringScan = false;
                        break;
                }
            }

            @Override
            public void onFailure(Call<RespStore<RespStoreInfo>> call, Throwable t) {
                DebugLog.e(TAG, "get store list failure");
                mDuringScan = false;
            }
        });
    }

    private void setMarker() {
        mGoogleMap.clear();

        if (mStoreList != null) {
            for (int i = 0; i < mStoreList.size(); i++) {
                RespStoreInfo info = mStoreList.get(i);
                LatLng location = new LatLng(info.getLatitude(), info.getLongitude());

                if (info.getNumSchedule() > 0) {
                    mMarkerBackground.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_radius_f70639));
                } else {
                    mMarkerBackground.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_radius_434f5a));
                }
                mMarkerTextView.setText(info.getOrgan());

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(location);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(mMarkerView)));
                Marker marker = mGoogleMap.addMarker(markerOptions);
                if (marker != null) marker.setTag(i);
            }
        }
    }

    private Bitmap loadBitmapFromView(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.draw(canvas);
        return bitmap;
    }
}
