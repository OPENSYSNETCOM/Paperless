package kr.co.opensysnet.paperless.view.fragment;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.control.listener.Listener;
import kr.co.opensysnet.paperless.control.storage.PrefStorage;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    protected Context mContext = null;

    protected ActionBar mActionBar = null;

    protected Listener.IFragmentListener mListener = null;

    private AppCompatActivity mActivity;

    protected Bitmap mImage = null;

    protected String imagePath;

    private int progressCount = 0;

    @Override
    public void onAttach(Context context) {
        mContext = context;
        mListener = (Listener.IFragmentListener) context;
        if (mContext instanceof AppCompatActivity){
            mActivity = (AppCompatActivity)mContext;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        mContext = null;
        mListener = null;
        mActivity = null;

        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    protected View initActionBar(@NonNull Toolbar toolbar, @LayoutRes int layoutId) {
        if (layoutId == 0) {
            return null;
        }

        if (mActivity != null){
            mActivity.setSupportActionBar(toolbar);
            mActionBar = mActivity.getSupportActionBar();

            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layoutId, null, false);

            if (mActionBar != null) {
                mActionBar.setDisplayShowCustomEnabled(true);
                mActionBar.setDisplayShowTitleEnabled(false);
                mActionBar.setCustomView(view);
            }

            view.findViewById(R.id.toolbar_cl_container).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        ((MainActivity)mContext).findViewById(R.id.main_fragment_container).requestFocus();
                    }
                }
            });

            return view;
        }else {
            DebugLog.e("Base Fragment", "mActivity is null");
            return null;
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    protected void popBackStack() {
        if (getView() != null) hideSoftKeyboard(getView());
        else DebugLog.e(TAG, "get view is null");

        FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();

        if (count > 0) {
            fm.popBackStack();
        } else {
            mActivity.onBackPressed();
        }
    }

    /**
     * addBackStack()에 name을 추가한 경우 사용.
     * name 프레그먼트 상단에 쌓인 스택을 지우고 name 프레그먼트를 보여줌.
     */
    protected void popBackStackClearTop() {
        if (getView() != null) hideSoftKeyboard(getView());
        else DebugLog.e(TAG, "get view is null");

        if (mContext == null) {
            return;
        }
        FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        String backStackName = null;

        if (count > 0) {
            for (int index = count - 1; index >= 0; index--) {
                FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(index);
                backStackName = entry.getName();

                if (backStackName != null) {
                    break;
                }
            }

            if (backStackName != null) {
                fm.popBackStack(backStackName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            } else {
                fm.popBackStack();
            }
        } else {
            mActivity.onBackPressed();
        }
    }

    protected void clearBackStack() {
        if (getView() != null) hideSoftKeyboard(getView());
        else DebugLog.e(TAG, "get view is null");

        FragmentManager fm = ((AppCompatActivity) mContext).getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    protected void makeToast(final String s) {
        if (mContext != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    protected void makeToast(final int num) {
        if (mContext != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, num, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    protected void longToast(final String s) {
        if (mContext != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected void longToast(final int num) {
        if (mContext != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, num, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    protected void setStatusColor(@ColorRes int num) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(mContext, num));
        }
    }

    protected void hideSoftKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    protected class CallbackWithProgress<T> implements Callback<T> {
        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            ((MainActivity) mContext).hideLoadingProgress();
            if (mContext == null) {
                DebugLog.e(TAG, "context is null");
            }
        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            ((MainActivity) mContext).hideLoadingProgress();
            DebugLog.e(TAG, "network failed : " + t.toString());
            if (mContext == null) {
                DebugLog.e(TAG, "context is null");
            }
        }
    }

    void showProgress() {
        progressCount++;
        if (mContext == null) {
            DebugLog.e(TAG, "context is null");
            return;
        }

        if (progressCount > 0)
            ((MainActivity) mContext).showLoadingProgress();
    }

    void hideProgress() {
        progressCount--;
        if (mContext == null) {
            DebugLog.e(TAG, "context is null");
            return;
        }

        if (progressCount < 1)
            ((MainActivity) mContext).hideLoadingProgress();
    }

    Bitmap resizeBitmap(Bitmap bitmap, int maxSize){
        if (bitmap == null) {
            DebugLog.e(TAG, "Bitmap is null");
            return null;
        }
        double ratio = ((double)bitmap.getHeight() / (double)bitmap.getWidth());
        int ratioHeight = (int)(maxSize * ratio);
        Bitmap image = Bitmap.createScaledBitmap(bitmap, maxSize, ratioHeight, true);
        if (bitmap != null) {
            bitmap.recycle();
        }

        return image;
    }

    File bitmapToFile(Bitmap bitmap) {
        File f = null;
        try {
            f = new File(mContext.getCacheDir(), "signature.png");
            boolean check = f.createNewFile();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] bitmapData = baos.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return f;
    }
}
