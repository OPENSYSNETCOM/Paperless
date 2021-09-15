package kr.co.opensysnet.paperless.view.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.FragmentWorkPaperSignBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkDeviceListBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqInstall;
import kr.co.opensysnet.paperless.model.response.RespDevice;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstall;
import kr.co.opensysnet.paperless.model.response.RespInstallInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDongle;
import kr.co.opensysnet.paperless.model.response.RespSign;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.component.WorkPaperDeviceDefaultComponent;
import kr.co.opensysnet.paperless.view.dialog.SignatureDialog;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkPaperSignFragment extends BaseFragment {
    private static final String TAG = "WorkPaperSignFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentWorkPaperSignBinding mBindingViews = null;

    private List<RespDeviceInfo<RespDeviceModelInfo>> mDeviceList;

    private boolean isModify;
    private int paperSeq = -1;
    private String paperCode = null;
    private String paperOrgan = null;
    private String paperWorkDivision = null;
    private String paperCeName = null;

    private List<String> mOrganTypes;

    private boolean isDeviceFold = true;
    private boolean isCheckFold = true;
    private boolean isRemarkFold = true;

    private boolean isSamFold = true;

    private boolean isCheckPosNumFold = true;
    private boolean isCheckPosVerFold = true;
    private boolean isCheckDateFold = true;
    private boolean isCheckGotFold = true;
    private boolean isCheckInternetFold = true;
    private boolean isCheckScPgmFold = true;
    private boolean isCheckPosCashFold = true;
    private boolean isCheckPosCreditFold = true;
    private boolean isCheckPosPointFold = true;
    private boolean isCheckScWorkFold = true;
    private boolean isCheckGotUpdateFold = true;

    private boolean isCheckTmoneyFold = true;
    private boolean isCheckHanpayFold = true;
    private boolean isCheckMybeeFold = true;
    private boolean isCheckCashbeeFold = true;

    private List<WorkPaperDeviceDefaultComponent> mDeviceComponents = new ArrayList<>();

    private RespInstallInfo mPaperInfo;

    private SignatureDialog mSignatureDialog;
    private Bitmap mOwnerSignature;
    private Bitmap mTechSignature;

    public static WorkPaperSignFragment newInstance(Bundle bundle) {
        WorkPaperSignFragment fragment = new WorkPaperSignFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        popBackStack();
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_work_paper_sign, container, false);
        return mBindingViews.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        initToolbar();
        initView();
        initRecycler();
    }

    private void initToolbar() {
        View actionBar = initActionBar(mBindingViews.workPaperSignToolbar, R.layout.toolbar_image_text);
        if (actionBar != null){
            mToolbarBindingViews = DataBindingUtil.bind(actionBar);

        }
        if (mToolbarBindingViews != null){
            mToolbarBindingViews.toolbarTvTitle.setText(R.string.toolbar_title_work_paper);
            mToolbarBindingViews.toolbarIvLeftIcon.setImageResource(R.drawable.ic_back);
            mToolbarBindingViews.toolbarTvRightText.setText(R.string.toolbar_button_check);

            mToolbarBindingViews.toolbarFlLeft.setOnClickListener(mToolbarOnClickListener);
            mToolbarBindingViews.toolbarFlRight.setOnClickListener(mToolbarOnClickListener);
        }
    }

    private final View.OnClickListener mToolbarOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view == mToolbarBindingViews.toolbarFlLeft) {
            onBackPressed();
        } else if (view == mToolbarBindingViews.toolbarFlRight) {
            if (mOwnerSignature == null) {
                makeToast("점포 확인자 사인이 등록되지 않았습니다.");
                return;
            }
            if (mTechSignature == null) {
                makeToast("기술 담당자 사인이 등록되지 않았습니다.");
                return;
            }
            reqWorkPaperSign("OWNER", mOwnerSignature);
            reqWorkPaperSign("TECH", mTechSignature);
        }
    };

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            paperSeq = bundle.getInt(Constants.BundleKey.PAPER_SEQ, -1);
            paperCode = bundle.getString(Constants.BundleKey.PAPER_CODE, null);
            paperOrgan = bundle.getString(Constants.BundleKey.PAPER_ORGAN, null);
            paperWorkDivision = bundle.getString(Constants.BundleKey.PAPER_WORK_DIVISION, null);
            paperCeName = bundle.getString(Constants.BundleKey.PAPER_CE_NAME, null);
        }
        DebugLog.e(TAG, "Seq : " + paperSeq);
        DebugLog.e(TAG, "Code : " + paperCode);
        DebugLog.e(TAG, "Organ : " + paperOrgan);
        DebugLog.e(TAG, "WorkDivision : " + paperWorkDivision);
        DebugLog.e(TAG, "CeName : " + paperCeName);
        if (paperSeq < 0) {
            makeToast("비정상적인 데이터 입니다.");
            onBackPressed();
            return;
        }

        mBindingViews.workPaperSignTvStoreCode.setText(paperCode);
        mBindingViews.workPaperSignTvStoreName.setText(paperOrgan);
        mBindingViews.workPaperSignTvWorkDivision.setText(paperWorkDivision);

        mBindingViews.workPaperSignTvWorkType.setText("-");
        mBindingViews.workPaperSignTvOpenDate.setText("-");
        mBindingViews.workPaperSignTvWorkDate.setText("-");
        mBindingViews.workPaperSignTvComeTime.setText("-");
        mBindingViews.workPaperSignTvWorkTime.setText("-");

        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvId1.setText("-");
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvTmoney1.setText("-");
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvId2.setText("-");
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvTmoney2.setText("-");

        mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewTvSetPos.setText("-");
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvVer1.setText("-");
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvVer2.setText("-");
        mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvOpenDate.setText("-");
        mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvSalesDate.setText("-");
        mBindingViews.workPaperSignIncCheckGot.compCheckGotViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckGot.compCheckGotViewTvDivision.setText("-");
        mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewTvAgency.setText("-");
        mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewTvVersion.setText("-");
        mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewTvNumber.setText("-");
        mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewTvNumber.setText("-");
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvType.setText("-");
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvPrice.setText("-");
        mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewTvProduce.setText("-");
        mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewTvStatus.setText("-");
        mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewTvVersion.setText("-");

        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvReason.setText("-");
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvLsam.setText("-");
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvCharge.setText("-");
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvPay.setText("-");

        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvReason.setText("-");
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvLsam.setText("-");
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvCharge.setText("-");
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvPay.setText("-");

        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvReason.setText("-");
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvLsam.setText("-");
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvCharge.setText("-");
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvPay.setText("-");

        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvReason.setText("-");
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvLsam.setText("-");
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvCharge.setText("-");
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvPay.setText("-");

        reqGetPaperInfo();

        isDeviceFold = true;
        isCheckFold = true;
        isRemarkFold = true;
        mBindingViews.workPaperSignIvMenuDevice.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperSignIvMenuCheck.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperSignIvMenuRemark.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperSignLlMenuDeviceContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignLlMenuCheckContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignLlMenuRemarkContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignLlMenuDevice.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperSignLlMenuCheck.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperSignLlMenuRemark.setOnClickListener(mViewOnClickListener);

        isSamFold = true;
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewLlTitle.setOnClickListener(mViewOnClickListener);

        isCheckPosNumFold = true;
        mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosVerFold = true;
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckDateFold = true;
        mBindingViews.workPaperSignIncCheckDate.compCheckDateViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckDate.compCheckDateViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckDate.compCheckDateViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckGotFold = true;
        mBindingViews.workPaperSignIncCheckGot.compCheckGotViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckGot.compCheckGotViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckGot.compCheckGotViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckInternetFold = true;
        mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckScPgmFold = true;
        mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosCashFold = true;
        mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosCreditFold = true;
        mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosPointFold = true;
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckScWorkFold = true;
        mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckGotUpdateFold = true;
        mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckTmoneyFold = true;
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckHanpayFold = true;
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckMybeeFold = true;
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckCashbeeFold = true;
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlTitle.setOnClickListener(mViewOnClickListener);

        mBindingViews.workPaperSignIncSignature.paperSignTvMessage.setText(R.string.paper_sign_msg_work);
        mBindingViews.workPaperSignIncSignature.paperSignFlTechSign.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperSignIncSignature.paperSignFlOwnerSign.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperSignIncSignature.paperSignTvTechSign.setVisibility(View.VISIBLE);
        mBindingViews.workPaperSignIncSignature.paperSignIvTechSign.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncSignature.paperSignTvOwnerSign.setVisibility(View.VISIBLE);
        mBindingViews.workPaperSignIncSignature.paperSignIvOwnerSign.setVisibility(View.GONE);
    }

    private String getDateString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String getTimeString(int hour, int min) {
        return String.format("%02d:%02d", hour, min);
    }

    private void setTextAtView(TextView tv, String text) {
        tv.setText(text);
    }

    private View.OnClickListener mViewOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view == mBindingViews.workPaperSignLlMenuDevice) {
            isDeviceFold = !isDeviceFold;
            changeFoldWhite(
                    mBindingViews.workPaperSignIvMenuDevice,
                    mBindingViews.workPaperSignLlMenuDeviceContent,
                    isDeviceFold
            );
        } else if (view == mBindingViews.workPaperSignLlMenuCheck) {
            isCheckFold = !isCheckFold;
            changeFoldWhite(
                    mBindingViews.workPaperSignIvMenuCheck,
                    mBindingViews.workPaperSignLlMenuCheckContent,
                    isCheckFold
            );
        } else if (view == mBindingViews.workPaperSignLlMenuRemark) {
            isRemarkFold = !isRemarkFold;
            changeFoldWhite(
                    mBindingViews.workPaperSignIvMenuRemark,
                    mBindingViews.workPaperSignLlMenuRemarkContent,
                    isRemarkFold
            );
        } else if (view == mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewLlTitle) {
            isSamFold = !isSamFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewIvTitle,
                    mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewLlContent,
                    isSamFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewLlTitle) {
            isCheckPosNumFold = !isCheckPosNumFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewIvTitle,
                    mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewLlContent,
                    isCheckPosNumFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewLlTitle) {
            isCheckPosVerFold = !isCheckPosVerFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewIvTitle,
                    mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewLlContent,
                    isCheckPosVerFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckDate.compCheckDateViewLlTitle) {
            isCheckDateFold = !isCheckDateFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckDate.compCheckDateViewIvTitle,
                    mBindingViews.workPaperSignIncCheckDate.compCheckDateViewLlContent,
                    isCheckDateFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckGot.compCheckGotViewLlTitle) {
            isCheckGotFold = !isCheckGotFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckGot.compCheckGotViewIvTitle,
                    mBindingViews.workPaperSignIncCheckGot.compCheckGotViewLlContent,
                    isCheckGotFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewLlTitle) {
            isCheckInternetFold = !isCheckInternetFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewIvTitle,
                    mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewLlContent,
                    isCheckInternetFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewLlTitle) {
            isCheckScPgmFold = !isCheckScPgmFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewIvTitle,
                    mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewLlContent,
                    isCheckScPgmFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewLlTitle) {
            isCheckPosCashFold = !isCheckPosCashFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewIvTitle,
                    mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewLlContent,
                    isCheckPosCashFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewLlTitle) {
            isCheckPosCreditFold = !isCheckPosCreditFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewIvTitle,
                    mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewLlContent,
                    isCheckPosCreditFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewLlTitle) {
            isCheckPosPointFold = !isCheckPosPointFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewIvTitle,
                    mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewLlContent,
                    isCheckPosPointFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewLlTitle) {
            isCheckScWorkFold = !isCheckScWorkFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewIvTitle,
                    mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewLlContent,
                    isCheckScWorkFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewLlTitle) {
            isCheckGotUpdateFold = !isCheckGotUpdateFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewIvTitle,
                    mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewLlContent,
                    isCheckGotUpdateFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlTitle) {
            isCheckTmoneyFold = !isCheckTmoneyFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewIvTitle,
                    mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlContent,
                    isCheckTmoneyFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlTitle) {
            isCheckHanpayFold = !isCheckHanpayFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewIvTitle,
                    mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlContent,
                    isCheckHanpayFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlTitle) {
            isCheckMybeeFold = !isCheckMybeeFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewIvTitle,
                    mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlContent,
                    isCheckMybeeFold
            );
        } else if (view == mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlTitle) {
            isCheckCashbeeFold = !isCheckCashbeeFold;
            changeFoldBlack(
                    mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewIvTitle,
                    mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlContent,
                    isCheckCashbeeFold
            );
        } else if (view == mBindingViews.workPaperSignIncSignature.paperSignFlTechSign) {
            if (mSignatureDialog == null) {
                mSignatureDialog = new SignatureDialog(mContext);
            }
            mSignatureDialog.setTitle("기술 담당자 사인");
            mSignatureDialog.setListener(new SignatureDialog.SimpleDialogListener() {
                @Override
                public void onConfirmListener(Bitmap bitmap) {
                    mSignatureDialog.dismiss();
                    mTechSignature = bitmap;
                    mBindingViews.workPaperSignIncSignature.paperSignTvTechSign.setVisibility(View.GONE);
                    mBindingViews.workPaperSignIncSignature.paperSignIvTechSign.setVisibility(View.VISIBLE);
                    mBindingViews.workPaperSignIncSignature.paperSignIvTechSign.setImageBitmap(bitmap);
                }

                @Override
                public void onCancelListener() {
                    mSignatureDialog.dismiss();
                }
            });
            mSignatureDialog.show();
        } else if (view == mBindingViews.workPaperSignIncSignature.paperSignFlOwnerSign) {
            mSignatureDialog.setTitle("점포 확인자 사인");
            mSignatureDialog.setListener(new SignatureDialog.SimpleDialogListener() {
                @Override
                public void onConfirmListener(Bitmap bitmap) {
                    mSignatureDialog.dismiss();
                    mOwnerSignature = bitmap;
                    mBindingViews.workPaperSignIncSignature.paperSignTvOwnerSign.setVisibility(View.GONE);
                    mBindingViews.workPaperSignIncSignature.paperSignIvOwnerSign.setVisibility(View.VISIBLE);
                    mBindingViews.workPaperSignIncSignature.paperSignIvOwnerSign.setImageBitmap(bitmap);
                }

                @Override
                public void onCancelListener() {
                    mSignatureDialog.dismiss();
                }
            });
            mSignatureDialog.show();
        }
    };

    private void changeFoldWhite(ImageView label, LinearLayout container, boolean flag) {
        if (flag) {
            label.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
            container.setVisibility(View.GONE);
        } else {
            label.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slideup_wh));
            container.setVisibility(View.VISIBLE);
        }
    }

    private void changeFoldBlack(ImageView label, LinearLayout container, boolean flag) {
        if (flag) {
            label.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
            container.setVisibility(View.GONE);
        } else {
            label.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slideup));
            container.setVisibility(View.VISIBLE);
        }
    }

    private void initRecycler() {
        if (mDeviceList == null) mDeviceList = new ArrayList<>();

        mBindingViews.workPaperSignRvDevice.setHasFixedSize(true);
        mBindingViews.workPaperSignRvDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespDeviceInfo<RespDeviceModelInfo>> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mDeviceList);
        mBindingViews.workPaperSignRvDevice.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false);
        mBindingViews.workPaperSignRvDevice.setLayoutManager(layoutManager);

        reqGetDeviceList();
    }

    RecyclerAdapter.IListListener mRecyclerListener = new RecyclerAdapter.IListListener() {
        @Override
        public <T> int onGetItemViewType(int position, @Nullable List<T> itemList) {
            return 0;
        }

        @Override
        public int onReqViewHolderLayout(int viewType) {
            return R.layout.recycler_item_work_device_list;
        }

        @Override
        public <T> void onBindViewHolder(@Nullable RecyclerAdapter.ViewHolder holder, int position, @Nullable List<T> itemList) {
            ViewDataBinding binding = null;
            if (holder != null) binding = holder.getBinding();

            if (binding instanceof RecyclerItemWorkDeviceListBinding) {
                RespDeviceInfo<RespDeviceModelInfo> data = mDeviceList.get(position);
                RecyclerItemWorkDeviceListBinding itemBinding = (RecyclerItemWorkDeviceListBinding) binding;

                itemBinding.riWorkDeviceListTitle.setText(data.getDevName());
                itemBinding.riWorkDeviceListTitle.setTag(position);
                for (WorkPaperDeviceDefaultComponent component : mDeviceComponents) {
                    if (component.getData().getDev_name().equals(data.getDevName())) {
                        itemBinding.riWorkDeviceListTitle.setChecked(true);
                    }
                }
            }
        }

        @Override
        public void onSetListItemsListener(@Nullable RecyclerAdapter.ViewHolder holder) {

        }

        @Override
        public void onViewRecycled(@Nullable RecyclerAdapter.ViewHolder holder) {

        }
    };

    private void reqGetPaperInfo() {
        showProgress();

        ReqInstall param = new ReqInstall();
        param.setSeq(paperSeq);

        ReqBase<ReqInstall> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespInstall<RespInstallInfo>> call = RetrofitService.getInstance().getService().reqInstallInfo(body);
        call.enqueue(new Callback<RespInstall<RespInstallInfo>>() {
            @Override
            public void onResponse(Call<RespInstall<RespInstallInfo>> call, Response<RespInstall<RespInstallInfo>> response) {
                hideProgress();
                RespInstall<RespInstallInfo> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    makeToast("설치확인서 조회에 실패했습니다.");
                    onBackPressed();
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    isModify = true;
                    setData(responseData.getInstallInfo());
                } else {
                    DebugLog.e(TAG, "reqGetPaperInfo failure");
                    makeToast("설치확인서 조회에 실패했습니다.");
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<RespInstall<RespInstallInfo>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "reqGetPaperInfo failure");
                makeToast("설치확인서 조회에 실패했습니다.");
                onBackPressed();
            }
        });
    }

    private void setData(RespInstallInfo data) {
        mPaperInfo = data;
        if (data == null) {
            makeToast("비정상적인 데이터입니다.");
            onBackPressed();
            return;
        }
        paperSeq = data.getSeq();
        paperCode = data.getCode();
        paperOrgan = data.getOrgan();
        paperCeName = data.getCeName();
        paperWorkDivision = data.getWorkdivision();
        mBindingViews.workPaperSignTvStoreName.setText(data.getOrgan());
        mBindingViews.workPaperSignTvStoreCode.setText(data.getCode());
        mBindingViews.workPaperSignTvStoreType.setText(data.getOrganType());
        if (data.getWorkType() != null) {
            mBindingViews.workPaperSignTvWorkType.setText(data.getWorkType());
        }
        if (data.getOpenday() != null) {
            try {
                int year = Integer.parseInt(data.getOpenday().substring(0, 4));
                int month = Integer.parseInt(data.getOpenday().substring(5, 7));
                int day = Integer.parseInt(data.getOpenday().substring(8, 10));
                setTextAtView(
                        mBindingViews.workPaperSignTvOpenDate,
                        getDateString(year, month, day)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getSetupday() != null) {
            try {
                int year = Integer.parseInt(data.getSetupday().substring(0, 4));
                int month = Integer.parseInt(data.getSetupday().substring(5, 7));
                int day = Integer.parseInt(data.getSetupday().substring(8, 10));
                setTextAtView(
                        mBindingViews.workPaperSignTvWorkDate,
                        getDateString(year, month, day)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getVisitDatetime() != null) {
            try {
                String[] dates = data.getVisitDatetime().split(" ");
                if (dates.length == 2) {
                    int hour = Integer.parseInt(dates[1].substring(0, 2));
                    int min = Integer.parseInt(dates[1].substring(3, 5));
                    setTextAtView(
                            mBindingViews.workPaperSignTvComeTime,
                            getTimeString(hour, min)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getComDatetime() != null) {
            try {
                String[] dates = data.getComDatetime().split(" ");
                if (dates.length == 2) {
                    int hour = Integer.parseInt(dates[1].substring(0, 2));
                    int min = Integer.parseInt(dates[1].substring(3, 5));
                    setTextAtView(
                            mBindingViews.workPaperSignTvWorkTime,
                            getTimeString(hour, min)
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data.getSamInfo() != null) {
            if (data.getSamInfo().getIntsam1() != null)
                mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvId1.setText(data.getSamInfo().getIntsam1());
            if (data.getSamInfo().getTmoney1() != null)
                mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvTmoney1.setText(data.getSamInfo().getTmoney1());
            if (data.getSamInfo().getIntsam2() != null)
                mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvId2.setText(data.getSamInfo().getIntsam2());
            if (data.getSamInfo().getTmoney2() != null)
                mBindingViews.workPaperSignIncDeviceSam.compDeviceSamViewTvTmoney2.setText(data.getSamInfo().getTmoney2());
        }

        if (data.getCheckInfo() != null) {
            RespInstallInfoCheck info = data.getCheckInfo();
            if (info.getPosNumCheckFlag() != null) {
                mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewTvStatus.setText(info.getPosNumCheckFlag());
                if (info.getPosNumVal() != null) {
                    mBindingViews.workPaperSignIncCheckPosNum.compCheckPosNumViewTvSetPos.setText(info.getPosNumVal().replaceAll("|", ","));
                }
            }

            if (info.getPosVerCheckFlag() != null) {
                mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvStatus.setText(info.getPosVerCheckFlag());
                if (info.getPosVerVal() != null) {
                    String[] vars = info.getPosVerVal().split("|");
                    if (vars.length > 0)
                        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvVer1.setText(vars[0]);
                    if (vars.length > 1)
                        mBindingViews.workPaperSignIncCheckPosVer.compCheckPosVerViewTvVer2.setText(vars[1]);
                }
            }

            if (info.getOpenday() != null) {
                try {
                    int year = Integer.parseInt(info.getOpenday().substring(0, 4));
                    int month = Integer.parseInt(info.getOpenday().substring(5, 7));
                    int day = Integer.parseInt(info.getOpenday().substring(8, 10));
                    setTextAtView(
                            mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvOpenDate,
                            getDateString(year, month, day)
                    );
                } catch (Exception e) {
                    mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvOpenDate.setText(info.getOpenday());
                    e.printStackTrace();
                }
            }

            if (info.getStartday() != null) {
                try {
                    int year = Integer.parseInt(info.getStartday().substring(0, 4));
                    int month = Integer.parseInt(info.getStartday().substring(5, 7));
                    int day = Integer.parseInt(info.getStartday().substring(8, 10));
                    setTextAtView(
                            mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvSalesDate,
                            getDateString(year, month, day)
                    );
                } catch (Exception e) {
                    mBindingViews.workPaperSignIncCheckDate.compCheckDateViewTvSalesDate.setText(info.getStartday());
                    e.printStackTrace();
                }
            }

            if (info.getGotCheckFlag() != null) {
                mBindingViews.workPaperSignIncCheckGot.compCheckGotViewTvStatus.setText(info.getGotCheckFlag());
                if (info.getGotVal() != null)
                    mBindingViews.workPaperSignIncCheckGot.compCheckGotViewTvDivision.setText(info.getGotVal());
            }

            mBindingViews.workPaperSignLlAfterInternet.setVisibility(View.GONE);
            if (info.getInternetFlag() != null) {
                mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewTvStatus.setText(info.getInternetFlag());
                if (info.getInternetFlag().equals("Y"))
                    mBindingViews.workPaperSignLlAfterInternet.setVisibility(View.VISIBLE);
                if (info.getInternetVal() != null)
                    mBindingViews.workPaperSignIncCheckInternet.compCheckInternetViewTvAgency.setText(info.getInternetVal());
            }

            if (info.getScPgmFlag() != null) {
                mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewTvStatus.setText(info.getScPgmFlag());
                if (info.getScPgmVal() != null)
                    mBindingViews.workPaperSignIncCheckScPgm.compCheckScPgmViewTvVersion.setText(info.getScPgmVal());
            }

            if (info.getPosCashFlag() != null) {
                mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewTvStatus.setText(info.getPosCashFlag());
                if (info.getPosCashVal() != null)
                    mBindingViews.workPaperSignIncCheckPosCash.compCheckPosCashViewTvNumber.setText(info.getPosCashVal());
            }

            if (info.getPosCreditFlag() != null) {
                mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewTvStatus.setText(info.getPosCreditFlag());
                if (info.getPosCreditVal() != null)
                    mBindingViews.workPaperSignIncCheckPosCredit.compCheckPosCreditViewTvNumber.setText(info.getPosCreditVal());
            }

            if (info.getPosPointFlag() != null) {
                mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvStatus.setText(info.getPosPointFlag());
                if (info.getPosPointVal() != null) {
                    String[] vars = info.getPosPointVal().split("|");
                    if (vars.length == 2) {
                        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvType.setText(vars[0]);
                        mBindingViews.workPaperSignIncCheckPosService.compCheckPosServiceViewTvPrice.setText(vars[1]);
                    }
                }
            }

            if (info.getScWorkFlag() != null) {
                mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewTvStatus.setText(info.getScWorkFlag());
                if (info.getScWorkVal() != null)
                    mBindingViews.workPaperSignIncCheckScWork.compCheckScWorkViewTvProduce.setText(info.getScWorkVal());
            }

            if (info.getGotUpdateFlag() != null) {
                mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewTvStatus.setText(info.getGotUpdateFlag());
                if (info.getGotUpdateVal() != null)
                    mBindingViews.workPaperSignIncCheckGotUpdate.compCheckGotUpdateViewTvVersion.setText(info.getGotUpdateVal());
            }
        }

        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlReason.setVisibility(View.GONE);

        if (data.getDongleList() != null) {
            for (RespInstallInfoDongle info : data.getDongleList()) {
                if (info.getLsamName() != null && info.getLsamName().equals("티머니")) {
                    if (info.getReason() != null)
                        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvReason.setText(info.getReason());
                    if (info.getLsamFlag() != null) {
                        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvLsam.setText(info.getLsamFlag());
                        if (info.getLsamFlag().equals("X"))
                            mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewLlReason.setVisibility(View.VISIBLE);
                    }
                    if (info.getCr_test() != null)
                        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvCharge.setText(info.getCr_test());
                    if (info.getPay_test() != null)
                        mBindingViews.workPaperSignIncCheckTmoney.compCheckTmoneyViewTvPay.setText(info.getPay_test());
                } else if (info.getLsamName() != null && info.getLsamName().equals("한페이")) {
                    if (info.getReason() != null)
                        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvReason.setText(info.getReason());
                    if (info.getLsamFlag() != null) {
                        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvLsam.setText(info.getLsamFlag());
                        if (info.getLsamFlag().equals("X"))
                            mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewLlReason.setVisibility(View.VISIBLE);
                    }
                    if (info.getCr_test() != null)
                        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvCharge.setText(info.getCr_test());
                    if (info.getPay_test() != null)
                        mBindingViews.workPaperSignIncCheckHanpay.compCheckHanpayViewTvPay.setText(info.getPay_test());
                } else if (info.getLsamName() != null && info.getLsamName().equals("마이비")) {
                    if (info.getReason() != null)
                        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvReason.setText(info.getReason());
                    if (info.getLsamFlag() != null) {
                        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvLsam.setText(info.getLsamFlag());
                        if (info.getLsamFlag().equals("X"))
                            mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewLlReason.setVisibility(View.VISIBLE);
                    }
                    if (info.getCr_test() != null)
                        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvCharge.setText(info.getCr_test());
                    if (info.getPay_test() != null)
                        mBindingViews.workPaperSignIncCheckMybee.compCheckMybeeViewTvPay.setText(info.getPay_test());
                } else if (info.getLsamName() != null && info.getLsamName().equals("캐시비")) {
                    if (info.getReason() != null)
                        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvReason.setText(info.getReason());
                    if (info.getLsamFlag() != null) {
                        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvLsam.setText(info.getLsamFlag());
                        if (info.getLsamFlag().equals("X"))
                            mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewLlReason.setVisibility(View.VISIBLE);
                    }
                    if (info.getCr_test() != null)
                        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvCharge.setText(info.getCr_test());
                    if (info.getPay_test() != null)
                        mBindingViews.workPaperSignIncCheckCashbee.compCheckCashbeeViewTvPay.setText(info.getPay_test());
                }
            }
        }


        if (mDeviceList != null && mDeviceList.size() > 0) {
            setDeviceData();
        }
    }

    private void setDeviceData() {
        if (mPaperInfo == null || mPaperInfo.getDevList() == null) return;

        for (RespInstallInfoDev info : mPaperInfo.getDevList()) {
            for (int i = 0; i < mDeviceList.size(); i++) {
                RespDeviceInfo<RespDeviceModelInfo> device = mDeviceList.get(i);

                if (info.getDevName() != null && info.getDevName().equals(device.getDevName())) {
                    WorkPaperDeviceDefaultComponent component = new WorkPaperDeviceDefaultComponent(mContext, device, info);
                    mDeviceComponents.add(component);
                    mBindingViews.workPaperSignLlDeviceList.addView(component);
                }
            }
        }
    }

    private void reqGetDeviceList() {
        showProgress();
        Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> call = RetrofitService.getInstance().getService().reqInstallDev();
        call.enqueue(new Callback<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>>() {
            @Override
            public void onResponse(Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> call, Response<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> response) {
                hideProgress();
                RespDevice<RespDeviceInfo<RespDeviceModelInfo>> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    makeToast("기기목록 조회에 실패했습니다.");
                    onBackPressed();
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    List<RespDeviceInfo<RespDeviceModelInfo>> deviceList = responseData.getDeviceList();
                    Collections.sort(deviceList, (t1, t2) -> {
                        if (t1.getDevOrder() < t2.getDevOrder()) return -1;
                        else if (t1.getDevOrder() > t2.getDevOrder()) return 1;
                        return 0;
                    });
                    for (RespDeviceInfo<RespDeviceModelInfo> device : deviceList) {
                        Collections.sort(device.getModelList(), (t1, t2) -> {
                            if (t1.getModelOrder() < t2.getModelOrder()) return -1;
                            else if (t1.getModelOrder() > t2.getModelOrder()) return 1;
                            return 0;
                        });
                    }
                    if (mDeviceList == null) mDeviceList = new ArrayList<>();
                    mDeviceList.clear();
                    mDeviceList.addAll(responseData.getDeviceList());
                    if (mBindingViews.workPaperSignRvDevice.getAdapter() != null)
                        mBindingViews.workPaperSignRvDevice.getAdapter().notifyDataSetChanged();
                    if (isModify) setDeviceData();
                } else {
                    DebugLog.e(TAG, "get device list failure");
                    makeToast("기기목록 조회에 실패했습니다.");
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "get device list failure");
                makeToast("기기목록 조회에 실패했습니다.");
                onBackPressed();
            }
        });
    }

    private void reqWorkPaperSign(String type, Bitmap signature) {
        showProgress();
        String reqParam = "{\"seq\":" + paperSeq + ",\"type\":\"" + type + "\"}";
        try {
            JSONObject object = new JSONObject();
            object.put("seq", paperSeq);
            object.put("type", type);
            reqParam = object.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File file = bitmapToFile(signature);
        if (file == null) {
            makeToast("사인 파일 변환에 실패했습니다.");
            hideProgress();
            return;
        }
        RequestBody picture = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("sign_file", file.getName(), picture);

        Call<RespSign> call = RetrofitService.getInstance().getService().reqInstallSign(body, reqParam);
        call.enqueue(new Callback<RespSign>() {
            @Override
            public void onResponse(Call<RespSign> call, Response<RespSign> response) {
                hideProgress();
                RespSign responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "work paper sign failure");
                    makeToast("설치확인서 사인 등록에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    DebugLog.e(TAG, "work paper sign success");
                    makeToast("설치확인서 사인 등록이 완료되었습니다.");
                } else {
                    DebugLog.e(TAG, "work paper sign failure");
                    makeToast("설치확인서 사인 등록에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespSign> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "work paper sign failure");
                makeToast("설치확인서 사인 등록에 실패했습니다.");
            }
        });
    }
}
