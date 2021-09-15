package kr.co.opensysnet.paperless.view.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.control.storage.PrefStorage;
import kr.co.opensysnet.paperless.databinding.FragmentLoginBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqLogin;
import kr.co.opensysnet.paperless.model.response.RespBase;
import kr.co.opensysnet.paperless.server.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends BaseFragment {
    private static final String TAG = "LoginFragment";

    private FragmentLoginBinding mBindingViews = null;

    private long mFinishTime = 0L;

    public static LoginFragment newInstance(Bundle bundle) {
        LoginFragment fragment = new LoginFragment();
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
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return mBindingViews.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initView();
    }

    private void initView() {
        String id = PrefStorage.getStringData(mContext, Constants.Preference.MainKey.LOGIN_INFO, Constants.Preference.SubKey.USER_ID);
        String pwd = PrefStorage.getStringData(mContext, Constants.Preference.MainKey.LOGIN_INFO, Constants.Preference.SubKey.PASSWORD);
        if (id != null && pwd != null && !id.isEmpty() && !pwd.isEmpty()) {
            mBindingViews.loginEtId.setText(id);
            mBindingViews.loginEtPwd.setText(pwd);
            mBindingViews.loginCbAuto.setChecked(true);
            reqLogin(id, pwd);
        }
        mBindingViews.loginBtnLogin.setOnClickListener(mViewOnClickListener);
    }

    private View.OnClickListener mViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            hideSoftKeyboard(view);
            if (view == mBindingViews.loginBtnLogin) {
                String id = mBindingViews.loginEtId.getText().toString().trim();
                String pwd = mBindingViews.loginEtPwd.getText().toString().trim();
                if (id.isEmpty()) {
                    makeToast("아이디를 입력해주세요");
                } else if (pwd.isEmpty()) {
                    makeToast("비밀번호를 입력해주세요");
                } else {
                    reqLogin(id, pwd);
                }
            }
        }
    };

    private void reqLogin(String id, String pwd) {
        showProgress();
        ReqLogin param = new ReqLogin();
        param.setUser_id(id);
        param.setPasswd(pwd);

        ReqBase<ReqLogin> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespBase> call = RetrofitService.getInstance().getService().reqLogin(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    return;
                }

                switch (responseData.getResultCode()) {
                    case Constants.ServerResultCode.SUCCESS:
                        if (mBindingViews.loginCbAuto.isChecked()) {
                            PrefStorage.saveStringData(mContext, Constants.Preference.MainKey.LOGIN_INFO, Constants.Preference.SubKey.USER_ID, id);
                            PrefStorage.saveStringData(mContext, Constants.Preference.MainKey.LOGIN_INFO, Constants.Preference.SubKey.PASSWORD, pwd);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        mListener.onChangeState(Constants.FragmentState.STORE_MAP, bundle);
                        break;
                    default:
                        makeToast("로그인에 실패했습니다.");
                        break;
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                makeToast("로그인에 실패했습니다.");
            }
        });
    }
}
