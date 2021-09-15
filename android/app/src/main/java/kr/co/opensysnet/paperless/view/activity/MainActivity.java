package kr.co.opensysnet.paperless.view.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.control.listener.Listener;
import kr.co.opensysnet.paperless.control.service.LocationService;
import kr.co.opensysnet.paperless.control.storage.PrefStorage;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.fragment.IntroFragment;
import kr.co.opensysnet.paperless.view.fragment.LoginFragment;
import kr.co.opensysnet.paperless.view.fragment.ServicePaperRegisterFragment;
import kr.co.opensysnet.paperless.view.fragment.ServicePaperSignFragment;
import kr.co.opensysnet.paperless.view.fragment.StoreMapFragment;
import kr.co.opensysnet.paperless.view.fragment.WorkListFragment;
import kr.co.opensysnet.paperless.view.fragment.WorkPaperRegisterFragment;
import kr.co.opensysnet.paperless.view.fragment.WorkPaperSignFragment;
import kr.co.opensysnet.paperless.view.fragment.WorkScheduleFragment;

public class MainActivity extends BaseActivity implements Listener.IFragmentListener {
    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout = null;

    private static final int LOCATION_INTERVAL = 1000 * 10;
    private LocationService mLocationService = null;
    private boolean mIsLocationService = false;

    private FrameLayout mProgressLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initDrawer();
        changeFragment(Constants.FragmentState.INTRO, null);

        findViewById(R.id.main_fragment_container).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mIsLocationService) {
            unbindService(mLocationServiceConnection);
            mIsLocationService = false;
        }
        super.onDestroy();
    }

    private void initDrawer() {
        mDrawerLayout = findViewById(R.id.main_container);
        mProgressLayout = findViewById(R.id.main_progress_container);
        mDrawerLayout.findViewById(R.id.side_btn_logout).setOnClickListener(mDrawerOnClickListener);
        mDrawerLayout.findViewById(R.id.side_ll_store_map).setOnClickListener(mDrawerOnClickListener);
        mDrawerLayout.findViewById(R.id.side_ll_work_list).setOnClickListener(mDrawerOnClickListener);
        mDrawerLayout.findViewById(R.id.side_ll_work_schedule).setOnClickListener(mDrawerOnClickListener);

        setDrawerLock();
    }

    private void changeFragment(String showType, Bundle bundle) {
        int targetFragment = 0;
        boolean isAddBackStack = false;
        boolean isAddFragment = false;
        String addBackStackName = null;

        if (bundle != null) {
            isAddBackStack = bundle.getBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, false);
            isAddFragment = bundle.getBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_FRAGMENT, false);
            addBackStackName = bundle.getString(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK_NAME, null);
            targetFragment = bundle.getInt(Constants.IntentExtra.EXTRA_KEY_TARGET_FRAGMENT, 0);
            bundle.remove(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK);
            bundle.remove(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK_NAME);
            bundle.remove(Constants.IntentExtra.EXTRA_KEY_TARGET_FRAGMENT);
        }

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (isAddBackStack) {
            ft.addToBackStack(addBackStackName);
        }

        Fragment fragment = null;

        switch (showType) {
            case Constants.FragmentState.INTRO:
                fragment = IntroFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.LOGIN:
                fragment = LoginFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.STORE_MAP:
                fragment = StoreMapFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.WORK_SCHEDULE:
                fragment = WorkScheduleFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.WORK_LIST:
                fragment = WorkListFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.WORK_PAPER_REGISTER:
                fragment = WorkPaperRegisterFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.WORK_PAPER_SIGN:
                fragment = WorkPaperSignFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.SERVICE_PAPER_REGISTER:
                fragment = ServicePaperRegisterFragment.newInstance(bundle);
                break;
            case Constants.FragmentState.SERVICE_PAPER_SIGN:
                fragment = ServicePaperSignFragment.newInstance(bundle);
                break;
        }

        if (fragment == null) {
            return;
        }

        if (targetFragment > 0) {
            Fragment currentFragment = fm.findFragmentById(R.id.main_container);
            fragment.setTargetFragment(currentFragment, targetFragment);
        }

        if (isAddFragment) {
            ft.add(R.id.main_fragment_container, fragment, showType);
        } else {
            ft.replace(R.id.main_fragment_container, fragment, showType);
        }

        ft.commitAllowingStateLoss();
    }

    @Override
    public void onChangeState(String fragmentState, Bundle bundle) {
        changeFragment(fragmentState, bundle);
    }

    public void setDrawerLock() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void setDrawerUnlock() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    public void drawerActive(boolean flag) {
        this.findViewById(R.id.main_fragment_container).requestFocus();

        if (mDrawerLayout != null) {
            if (flag) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        }
    }

    public boolean checkDrawerOpen() {
        boolean flag = false;
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            flag = true;
        }
        return flag;
    }

    private final View.OnClickListener mDrawerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Bundle bundle = new Bundle();
            bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
            if (v.getId() == R.id.side_ll_store_map) {
                drawerActive(false);
                onChangeState(Constants.FragmentState.STORE_MAP, bundle);
            } else if (v.getId() == R.id.side_ll_work_list) {
                drawerActive(false);
                onChangeState(Constants.FragmentState.WORK_LIST, bundle);
            } else if (v.getId() == R.id.side_ll_work_schedule) {
                drawerActive(false);
                onChangeState(Constants.FragmentState.WORK_SCHEDULE, bundle);
            } else if (v.getId() == R.id.side_btn_logout) {
                drawerActive(false);
                setDrawerLock();

                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                onChangeState(Constants.FragmentState.LOGIN, null);
                PrefStorage.clearAll(MainActivity.this, Constants.Preference.MainKey.LOGIN_INFO);
            }
        }
    };

    public void setDrawerProfile() {
        ((TextView)mDrawerLayout.findViewById(R.id.side_tv_name)).setText(PrefStorage.getStringData(MainActivity.this, Constants.Preference.MainKey.LOGIN_INFO, Constants.Preference.SubKey.USER_NAME));
        // todo set side count at R.id.side_tv_count_schedule
    }

    public void redrawDrawer() {
        initDrawer();
    }

    public void showLoadingProgress() {
        if (mProgressLayout == null) return;

        mProgressLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoadingProgress() {
        if (mProgressLayout == null) return;

        mProgressLayout.setVisibility(View.GONE);
    }

    public void startLocationService() {
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        bindService(intent, mLocationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection mLocationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DebugLog.d(TAG, "Location Service is Connected");
            mIsLocationService = true;
            LocationService.LocalBinder lb = (LocationService.LocalBinder) service;
            mLocationService = lb.getService();
            mLocationService.setContextWithListener(MainActivity.this, LOCATION_INTERVAL);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            DebugLog.d(TAG, "Location Service is Disconnected");
            mIsLocationService = false;
            mLocationService.setContextWithListener(null, 0);
        }
    };

    public void setFragmentListener(Fragment fragment){
        if (mLocationService != null){
            mLocationService.setFragmentListener(fragment);
        }
    }
}
