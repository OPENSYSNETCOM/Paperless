package kr.co.opensysnet.paperless.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import kr.co.opensysnet.paperless.BuildConfig;
import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.FragmentIntroBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.activity.MainActivity;

public class IntroFragment extends BaseFragment {
    private static final String TAG = "IntroFragment";

    private FragmentIntroBinding mBindingViews = null;

    public static IntroFragment newInstance(Bundle bundle) {
        IntroFragment fragment = new IntroFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_intro, container, false);
        return mBindingViews.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBindingViews.introTvVersion.setText(getResources().getString(R.string.intro_version, BuildConfig.VERSION_NAME));

        checkPermission();
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            init();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    init();
                } else {
                    makeToast("권한요청이 거부되었습니다.");
                }
            });

    private void init() {
        ((MainActivity) mContext).startLocationService();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (mContext == null) {
                DebugLog.e(TAG, "mContext is null");
                return;
            }
            mListener.onChangeState(Constants.FragmentState.LOGIN, null);
        }, 3000);
    }
}
