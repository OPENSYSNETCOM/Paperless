package kr.co.opensysnet.paperless.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.GridLayoutManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.FragmentWorkPaperRegisterBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkDeviceListBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqInstall;
import kr.co.opensysnet.paperless.model.request.ReqInstallCheck;
import kr.co.opensysnet.paperless.model.request.ReqInstallDev;
import kr.co.opensysnet.paperless.model.request.ReqInstallDongle;
import kr.co.opensysnet.paperless.model.request.ReqInstallSam;
import kr.co.opensysnet.paperless.model.response.RespBase;
import kr.co.opensysnet.paperless.model.response.RespDevice;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstall;
import kr.co.opensysnet.paperless.model.response.RespInstallInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDongle;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.component.WorkPaperDeviceDefaultComponent;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import kr.co.opensysnet.paperless.view.widget.TextSpinnerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkPaperRegisterFragment extends BaseFragment {
    private static final String TAG = "WorkPaperRegisterFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentWorkPaperRegisterBinding mBindingViews = null;

    private List<RespDeviceInfo<RespDeviceModelInfo>> mDeviceList;

    private boolean isModify;
    private int paperSeq = -1;
    private String paperCode = null;
    private String paperOrgan = null;
    private String paperWorkDivision = null;
    private String paperCeName = null;

    private DatePickerDialog mOpenDatePicker;
    private int mOpenSelectedYear = 0;
    private int mOpenSelectedMonth = 0;
    private int mOpenSelectedDay = 0;

    private DatePickerDialog mWorkDatePicker;
    private int mWorkSelectedYear = 0;
    private int mWorkSelectedMonth = 0;
    private int mWorkSelectedDay = 0;

    private DatePickerDialog mCheckOpenDatePicker;
    private int mCheckOpenSelectedYear = 0;
    private int mCheckOpenSelectedMonth = 0;
    private int mCheckOpenSelectedDay = 0;

    private DatePickerDialog mCheckSalesDatePicker;
    private int mCheckSalesSelectedYear = 0;
    private int mCheckSalesSelectedMonth = 0;
    private int mCheckSalesSelectedDay = 0;

    private TimePickerDialog mComeTimePicker;
    private int mComeHour = 0;
    private int mComeMinute = 0;

    private TimePickerDialog mWorkTimePicker;
    private int mWorkHour = 0;
    private int mWorkMinute = 0;

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

    public static WorkPaperRegisterFragment newInstance(Bundle bundle) {
        WorkPaperRegisterFragment fragment = new WorkPaperRegisterFragment();
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
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_work_paper_register, container, false);
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
        initSpinner();
        initRecycler();
    }

    private void initToolbar() {
        View actionBar = initActionBar(mBindingViews.workPaperRegisterToolbar, R.layout.toolbar_image_text);
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
            if (isModify) {
                reqWorkPaperMod();
            } else {
                reqWorkPaperReg();
            }
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
        mBindingViews.workPaperRegTvStoreCode.setText(paperCode);
        mBindingViews.workPaperRegTvStoreName.setText(paperOrgan);
        mBindingViews.workPaperRegTvWorkDivision.setText(paperWorkDivision);
        reqGetPaperInfo();

        LocalDate now = LocalDate.now();
        if (!isModify) {
            mOpenSelectedYear = now.getYear();
            mOpenSelectedMonth = now.getMonthValue();
            mOpenSelectedDay = now.getDayOfMonth();
            setTextAtView(
                    mBindingViews.workPaperRegTvOpenDate,
                    getDateString(mOpenSelectedYear, mOpenSelectedMonth, mOpenSelectedDay)
            );
        }
        if (mOpenDatePicker == null) {
            mOpenDatePicker = new DatePickerDialog(mContext);
            mOpenDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mOpenSelectedYear = year;
                    mOpenSelectedMonth = month + 1;
                    mOpenSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegTvOpenDate,
                            getDateString(mOpenSelectedYear, mOpenSelectedMonth, mOpenSelectedDay)
                    );
                }
            });
        }
        mBindingViews.workPaperRegClOpenDate.setOnClickListener(mViewOnClickListener);

        if (!isModify) {
            mWorkSelectedYear = now.getYear();
            mWorkSelectedMonth = now.getMonthValue();
            mWorkSelectedDay = now.getDayOfMonth();
            setTextAtView(
                    mBindingViews.workPaperRegTvWorkDate,
                    getDateString(mWorkSelectedYear, mWorkSelectedMonth, mWorkSelectedDay)
            );
        }
        if (mWorkDatePicker == null) {
            mWorkDatePicker = new DatePickerDialog(mContext);
            mWorkDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mWorkSelectedYear = year;
                    mWorkSelectedMonth = month + 1;
                    mWorkSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegTvWorkDate,
                            getDateString(mWorkSelectedYear, mWorkSelectedMonth, mWorkSelectedDay)
                    );
                }
            });
        }
        mBindingViews.workPaperRegClWorkDate.setOnClickListener(mViewOnClickListener);

        if (!isModify) {
            mCheckOpenSelectedYear = now.getYear();
            mCheckOpenSelectedMonth = now.getMonthValue();
            mCheckOpenSelectedDay = now.getDayOfMonth();
            setTextAtView(
                    mBindingViews.workPaperRegIncCheckDate.compCheckDateTvOpenDate,
                    getDateString(mCheckOpenSelectedYear, mCheckOpenSelectedMonth, mCheckOpenSelectedDay)
            );
        }
        if (mCheckOpenDatePicker == null) {
            mCheckOpenDatePicker = new DatePickerDialog(mContext);
            mCheckOpenDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mCheckOpenSelectedYear = year;
                    mCheckOpenSelectedMonth = month + 1;
                    mCheckOpenSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegIncCheckDate.compCheckDateTvOpenDate,
                            getDateString(mCheckOpenSelectedYear, mCheckOpenSelectedMonth, mCheckOpenSelectedDay)
                    );
                }
            });
        }
        mBindingViews.workPaperRegIncCheckDate.compCheckDateClOpenDate.setOnClickListener(mViewOnClickListener);

        if (!isModify) {
            mCheckSalesSelectedYear = now.getYear();
            mCheckSalesSelectedMonth = now.getMonthValue();
            mCheckSalesSelectedDay = now.getDayOfMonth();
            setTextAtView(
                    mBindingViews.workPaperRegIncCheckDate.compCheckDateTvSalesDate,
                    getDateString(mCheckSalesSelectedYear, mCheckSalesSelectedMonth, mCheckSalesSelectedDay)
            );
        }
        if (mCheckSalesDatePicker == null) {
            mCheckSalesDatePicker = new DatePickerDialog(mContext);
            mCheckSalesDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mCheckSalesSelectedYear = year;
                    mCheckSalesSelectedMonth = month + 1;
                    mCheckSalesSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegIncCheckDate.compCheckDateTvSalesDate,
                            getDateString(mCheckSalesSelectedYear, mCheckSalesSelectedMonth, mCheckSalesSelectedDay)
                    );
                }
            });
        }
        mBindingViews.workPaperRegIncCheckDate.compCheckDateClSalesDate.setOnClickListener(mViewOnClickListener);

        LocalTime nowTime = LocalTime.now();
        if (!isModify) {
            mWorkHour = nowTime.getHour();
            mWorkMinute = nowTime.getMinute();
            setTextAtView(
                    mBindingViews.workPaperRegTvWorkTime,
                    getTimeString(mWorkHour, mWorkMinute)
            );
        }
        if (mWorkTimePicker == null) {
            mWorkTimePicker = new TimePickerDialog(mContext, (picker, hour, min) -> {
                mWorkHour = hour;
                mWorkMinute = min;
                setTextAtView(
                        mBindingViews.workPaperRegTvWorkTime,
                        getTimeString(mWorkHour, mWorkMinute)
                );
            }, mWorkHour, mWorkMinute, true);
        }
        mBindingViews.workPaperRegClWorkTime.setOnClickListener(mViewOnClickListener);

        if (!isModify) {
            mComeHour = nowTime.getHour();
            mComeMinute = nowTime.getMinute();
            setTextAtView(
                    mBindingViews.workPaperRegTvComeTime,
                    getTimeString(mComeHour, mComeMinute)
            );
        }
        if (mComeTimePicker == null) {
            mComeTimePicker = new TimePickerDialog(mContext, (picker, hour, min) -> {
                mComeHour = hour;
                mComeMinute = min;
                setTextAtView(
                        mBindingViews.workPaperRegTvComeTime,
                        getTimeString(mComeHour, mComeMinute)
                );
            }, mComeHour, mComeMinute, true);
        }
        mBindingViews.workPaperRegClComeTime.setOnClickListener(mViewOnClickListener);

        isDeviceFold = true;
        isCheckFold = true;
        isRemarkFold = true;
        mBindingViews.workPaperRegIvMenuDevice.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperRegIvMenuCheck.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperRegIvMenuRemark.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.workPaperRegLlMenuDeviceContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegLlMenuCheckContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegLlMenuRemarkContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegLlMenuDevice.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegLlMenuCheck.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegLlMenuRemark.setOnClickListener(mViewOnClickListener);

        isSamFold = true;
        mBindingViews.workPaperRegIncDeviceSam.compDeviceSamIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncDeviceSam.compDeviceSamLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncDeviceSam.compDeviceSamLlTitle.setOnClickListener(mViewOnClickListener);

        isCheckPosNumFold = true;
        mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosVerFold = true;
        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckDateFold = true;
        mBindingViews.workPaperRegIncCheckDate.compCheckDateIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckDate.compCheckDateLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckDate.compCheckDateLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckGotFold = true;
        mBindingViews.workPaperRegIncCheckGot.compCheckGotIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckGot.compCheckGotLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckGot.compCheckGotLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckInternetFold = true;
        mBindingViews.workPaperRegIncCheckInternet.compCheckInternetIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckInternet.compCheckInternetLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckInternet.compCheckInternetLlTitle.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegLlAfterInternet.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckInternet.compCheckInternetRbStatusYes.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegLlAfterInternet.setVisibility(View.VISIBLE);
        });
        mBindingViews.workPaperRegIncCheckInternet.compCheckInternetRbStatusNo.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegLlAfterInternet.setVisibility(View.GONE);
        });
        isCheckScPgmFold = true;
        mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosCashFold = true;
        mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosCreditFold = true;
        mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckPosPointFold = true;
        mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckScWorkFold = true;
        mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkLlTitle.setOnClickListener(mViewOnClickListener);
        isCheckGotUpdateFold = true;
        mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateLlTitle.setOnClickListener(mViewOnClickListener);

        isCheckTmoneyFold = true;
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlTitle.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamYes.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlReason.setVisibility(View.GONE);
        });
        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamNo.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlReason.setVisibility(View.VISIBLE);
        });
        isCheckHanpayFold = true;
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlTitle.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamYes.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlReason.setVisibility(View.GONE);
        });
        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamNo.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlReason.setVisibility(View.VISIBLE);
        });
        isCheckMybeeFold = true;
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlTitle.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamYes.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlReason.setVisibility(View.GONE);
        });
        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamNo.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlReason.setVisibility(View.VISIBLE);
        });
        isCheckCashbeeFold = true;
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlContent.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlTitle.setOnClickListener(mViewOnClickListener);
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlReason.setVisibility(View.GONE);
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamYes.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlReason.setVisibility(View.GONE);
        });
        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamNo.setOnCheckedChangeListener((button, checked) -> {
            if (checked) mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlReason.setVisibility(View.VISIBLE);
        });
    }

    private String getDateString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    private String getTimeString(int hour, int min) {
        return String.format("%02d:%02d", hour, min);
    }

    private void setTextAtView(TextView tv, String text) {
        DebugLog.e(TAG, text);
        tv.setText(text);
    }

    private View.OnClickListener mViewOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view == mBindingViews.workPaperRegLlMenuDevice) {
            isDeviceFold = !isDeviceFold;
            changeFoldWhite(
                    mBindingViews.workPaperRegIvMenuDevice,
                    mBindingViews.workPaperRegLlMenuDeviceContent,
                    isDeviceFold
            );
        } else if (view == mBindingViews.workPaperRegLlMenuCheck) {
            isCheckFold = !isCheckFold;
            changeFoldWhite(
                    mBindingViews.workPaperRegIvMenuCheck,
                    mBindingViews.workPaperRegLlMenuCheckContent,
                    isCheckFold
            );
        } else if (view == mBindingViews.workPaperRegLlMenuRemark) {
            isRemarkFold = !isRemarkFold;
            changeFoldWhite(
                    mBindingViews.workPaperRegIvMenuRemark,
                    mBindingViews.workPaperRegLlMenuRemarkContent,
                    isRemarkFold
            );
        } else if (view == mBindingViews.workPaperRegClOpenDate) {
            mOpenDatePicker.show();
        } else if (view == mBindingViews.workPaperRegClWorkDate) {
            mWorkDatePicker.show();
        } else if (view == mBindingViews.workPaperRegClComeTime) {
            mComeTimePicker.show();
        } else if (view == mBindingViews.workPaperRegClWorkTime) {
            mWorkTimePicker.show();
        } else if (view == mBindingViews.workPaperRegIncCheckDate.compCheckDateClOpenDate) {
            mCheckOpenDatePicker.show();
        } else if (view == mBindingViews.workPaperRegIncCheckDate.compCheckDateClSalesDate) {
            mCheckSalesDatePicker.show();
        } else if (view == mBindingViews.workPaperRegIncDeviceSam.compDeviceSamLlTitle) {
            isSamFold = !isSamFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncDeviceSam.compDeviceSamIvTitle,
                    mBindingViews.workPaperRegIncDeviceSam.compDeviceSamLlContent,
                    isSamFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumLlTitle) {
            isCheckPosNumFold = !isCheckPosNumFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumIvTitle,
                    mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumLlContent,
                    isCheckPosNumFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerLlTitle) {
            isCheckPosVerFold = !isCheckPosVerFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerIvTitle,
                    mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerLlContent,
                    isCheckPosVerFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckDate.compCheckDateLlTitle) {
            isCheckDateFold = !isCheckDateFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckDate.compCheckDateIvTitle,
                    mBindingViews.workPaperRegIncCheckDate.compCheckDateLlContent,
                    isCheckDateFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckGot.compCheckGotLlTitle) {
            isCheckGotFold = !isCheckGotFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckGot.compCheckGotIvTitle,
                    mBindingViews.workPaperRegIncCheckGot.compCheckGotLlContent,
                    isCheckGotFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckInternet.compCheckInternetLlTitle) {
            isCheckInternetFold = !isCheckInternetFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckInternet.compCheckInternetIvTitle,
                    mBindingViews.workPaperRegIncCheckInternet.compCheckInternetLlContent,
                    isCheckInternetFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmLlTitle) {
            isCheckScPgmFold = !isCheckScPgmFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmIvTitle,
                    mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmLlContent,
                    isCheckScPgmFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashLlTitle) {
            isCheckPosCashFold = !isCheckPosCashFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashIvTitle,
                    mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashLlContent,
                    isCheckPosCashFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditLlTitle) {
            isCheckPosCreditFold = !isCheckPosCreditFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditIvTitle,
                    mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditLlContent,
                    isCheckPosCreditFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceLlTitle) {
            isCheckPosPointFold = !isCheckPosPointFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceIvTitle,
                    mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceLlContent,
                    isCheckPosPointFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkLlTitle) {
            isCheckScWorkFold = !isCheckScWorkFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkIvTitle,
                    mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkLlContent,
                    isCheckScWorkFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateLlTitle) {
            isCheckGotUpdateFold = !isCheckGotUpdateFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateIvTitle,
                    mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateLlContent,
                    isCheckGotUpdateFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlTitle) {
            isCheckTmoneyFold = !isCheckTmoneyFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyIvTitle,
                    mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyLlContent,
                    isCheckTmoneyFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlTitle) {
            isCheckHanpayFold = !isCheckHanpayFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayIvTitle,
                    mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayLlContent,
                    isCheckHanpayFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlTitle) {
            isCheckMybeeFold = !isCheckMybeeFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeIvTitle,
                    mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeLlContent,
                    isCheckMybeeFold
            );
        } else if (view == mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlTitle) {
            isCheckCashbeeFold = !isCheckCashbeeFold;
            changeFoldBlack(
                    mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeIvTitle,
                    mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeLlContent,
                    isCheckCashbeeFold
            );
        }
    };

    private void initSpinner() {
        if (mOrganTypes == null) mOrganTypes = new ArrayList<>();
        mOrganTypes.add("직영");
        mOrganTypes.add("A");
        mOrganTypes.add("B");
        mOrganTypes.add("기타");
        TextSpinnerAdapter adapter = new TextSpinnerAdapter(mContext, mOrganTypes);
        mBindingViews.workPaperRegSpStoreType.setAdapter(adapter);
    }

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

        mBindingViews.workPaperRegRvDevice.setHasFixedSize(true);
        mBindingViews.workPaperRegRvDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespDeviceInfo<RespDeviceModelInfo>> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mDeviceList);
        mBindingViews.workPaperRegRvDevice.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false);
        mBindingViews.workPaperRegRvDevice.setLayoutManager(layoutManager);

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
                        itemBinding.riWorkDeviceListTitle.setOnCheckedChangeListener(null);
                        itemBinding.riWorkDeviceListTitle.setChecked(true);
                        itemBinding.riWorkDeviceListTitle.setOnCheckedChangeListener(mRecyclerCheckedChangeListener);
                    }
                }
            }
        }

        @Override
        public void onSetListItemsListener(@Nullable RecyclerAdapter.ViewHolder holder) {
            if (holder == null) {
                DebugLog.e(TAG, "holder is null");
                return;
            }
            if (holder.getBinding() instanceof RecyclerItemWorkDeviceListBinding){
                RecyclerItemWorkDeviceListBinding binding = (RecyclerItemWorkDeviceListBinding)holder.getBinding();

                binding.riWorkDeviceListTitle.setOnCheckedChangeListener(mRecyclerCheckedChangeListener);
            }

        }

        @Override
        public void onViewRecycled(@Nullable RecyclerAdapter.ViewHolder holder) {

        }
    };

    private CompoundButton.OnCheckedChangeListener mRecyclerCheckedChangeListener = (button, checked) -> {
        int position = (int)button.getTag();
        RespDeviceInfo<RespDeviceModelInfo> data = mDeviceList.get(position);
        DebugLog.e(TAG, "device name" + data.getDevName());
        DebugLog.e(TAG, "device order" + data.getDevOrder());
        if (checked) {
            WorkPaperDeviceDefaultComponent component = new WorkPaperDeviceDefaultComponent(mContext, data);
            mDeviceComponents.add(component);
            mBindingViews.workPaperRegLlDeviceList.addView(component);
        } else {
            for (WorkPaperDeviceDefaultComponent component : mDeviceComponents) {
                if (component.getData().getDev_name().equals(data.getDevName()) && component.getData().getDev_order() == data.getDevOrder()) {
                    boolean removeCheck = mDeviceComponents.remove(component);
                    mBindingViews.workPaperRegLlDeviceList.removeView(component);
                    DebugLog.e(TAG, "Remove data : " + removeCheck);
                    break;
                }
            }
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
                    makeToast("문서 수정입니다.");
                    setData(responseData.getInstallInfo());
                } else if (responseData.getResultCode() == Constants.ServerResultCode.NOT_FOUND) {
                    isModify = false;
                    makeToast("신규 작성입니다.");
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
        DebugLog.e(TAG, "setData called");
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
        mBindingViews.workPaperRegTvWorkDivision.setText(paperWorkDivision);
        mBindingViews.workPaperRegTvStoreName.setText(data.getOrgan());
        mBindingViews.workPaperRegTvStoreCode.setText(data.getCode());
        mBindingViews.workPaperRegSpStoreType.setSelection(mOrganTypes.indexOf(data.getOrganType()));
        if (data.getWorkType() != null) {
            if (data.getWorkType().equals("설치")) mBindingViews.workPaperRegRbWorkTypeAdd.setChecked(true);
            else if (data.getWorkType().equals("철수")) mBindingViews.workPaperRegRbWorkTypeRemove.setChecked(true);
        }
        if (data.getOpenday() != null) {
            try {
                String yearStr = data.getOpenday().substring(0, 4);
                String monthStr = data.getOpenday().substring(5, 7);
                String dayStr = data.getOpenday().substring(8, 10);
                DebugLog.e(TAG, "yearStr : " + yearStr);
                DebugLog.e(TAG, "monthStr : " + monthStr);
                DebugLog.e(TAG, "dayStr : " + dayStr);
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                mOpenSelectedYear = year;
                mOpenSelectedMonth = month;
                mOpenSelectedDay = day;
                setTextAtView(
                        mBindingViews.workPaperRegTvOpenDate,
                        getDateString(mOpenSelectedYear, mOpenSelectedMonth, mOpenSelectedDay)
                );
                mOpenDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                    mOpenSelectedYear = y;
                    mOpenSelectedMonth = m + 1;
                    mOpenSelectedDay = d;
                    setTextAtView(
                            mBindingViews.workPaperRegTvOpenDate,
                            getDateString(mOpenSelectedYear, mOpenSelectedMonth, mOpenSelectedDay)
                    );
                }, mOpenSelectedYear, mOpenSelectedMonth - 1, mOpenSelectedDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DebugLog.e(TAG, "openday is null");
        }
        if (data.getSetupday() != null) {
            DebugLog.e(TAG, "setupday : " + data.getSetupday());
            try {
                String yearStr = data.getSetupday().substring(0, 4);
                String monthStr = data.getSetupday().substring(5, 7);
                String dayStr = data.getSetupday().substring(8, 10);
                DebugLog.e(TAG, "yearStr : " + yearStr);
                DebugLog.e(TAG, "monthStr : " + monthStr);
                DebugLog.e(TAG, "dayStr : " + dayStr);
                int year = Integer.parseInt(yearStr);
                int month = Integer.parseInt(monthStr);
                int day = Integer.parseInt(dayStr);
                mWorkSelectedYear = year;
                mWorkSelectedMonth = month;
                mWorkSelectedDay = day;
                setTextAtView(
                        mBindingViews.workPaperRegTvWorkDate,
                        getDateString(mWorkSelectedYear, mWorkSelectedMonth, mWorkSelectedDay)
                );
                mWorkDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                    mWorkSelectedYear = y;
                    mWorkSelectedMonth = m + 1;
                    mWorkSelectedDay = d;
                    setTextAtView(
                            mBindingViews.workPaperRegTvWorkDate,
                            getDateString(mWorkSelectedYear, mWorkSelectedMonth, mWorkSelectedDay)
                    );
                }, mWorkSelectedYear, mWorkSelectedMonth - 1, mWorkSelectedDay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DebugLog.e(TAG, "setupday is null");
        }
        if (data.getVisitDatetime() != null) {
            DebugLog.e(TAG, "visit datetime : " + data.getVisitDatetime());
            try {
                String[] dates = data.getVisitDatetime().split(" ");
                if (dates.length == 2) {
                    int hour = Integer.parseInt(dates[1].substring(0, 2));
                    int min = Integer.parseInt(dates[1].substring(3, 5));
                    mComeHour = hour;
                    mComeMinute = min;
                    setTextAtView(
                            mBindingViews.workPaperRegTvComeTime,
                            getTimeString(mComeHour, mComeMinute)
                    );
                    mComeTimePicker = new TimePickerDialog(mContext, (picker, h, m) -> {
                        mComeHour = h;
                        mComeMinute = m;
                        setTextAtView(
                                mBindingViews.workPaperRegTvComeTime,
                                getTimeString(mComeHour, mComeMinute)
                        );
                    }, mComeHour, mComeMinute, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DebugLog.e(TAG, "visitdatetime is null");
        }
        if (data.getComDatetime() != null) {
            DebugLog.e(TAG, "com datetime : " + data.getComDatetime());
            try {
                String[] dates = data.getComDatetime().split(" ");
                if (dates.length == 2) {
                    int hour = Integer.parseInt(dates[1].substring(0, 2));
                    int min = Integer.parseInt(dates[1].substring(3, 5));
                    mWorkHour = hour;
                    mWorkMinute = min;
                    setTextAtView(
                            mBindingViews.workPaperRegTvWorkTime,
                            getTimeString(mWorkHour, mWorkMinute)
                    );
                    mWorkTimePicker = new TimePickerDialog(mContext, (picker, h, m) -> {
                        mWorkHour = h;
                        mWorkMinute = m;
                        setTextAtView(
                                mBindingViews.workPaperRegTvWorkTime,
                                getTimeString(mWorkHour, mWorkMinute)
                        );
                    }, mWorkHour, mWorkMinute, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            DebugLog.e(TAG, "comdatetime is null");
        }

        if (data.getSamInfo() != null) {
            mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtId1.setText(data.getSamInfo().getIntsam1());
            mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtTmoney1.setText(data.getSamInfo().getTmoney1());
            mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtId2.setText(data.getSamInfo().getIntsam2());
            mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtTmoney2.setText(data.getSamInfo().getTmoney2());
        }

        if (data.getCheckInfo() != null) {
            RespInstallInfoCheck info = data.getCheckInfo();
            if (info.getPosNumCheckFlag() != null && info.getPosNumCheckFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumRbStatusYes.setChecked(true);
                if (info.getPosNumVal() != null) {
                    String[] vars = info.getPosNumVal().split("|");
                    for (String var : vars) {
                        if (var.equals(mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.getText().toString().trim()))
                            mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.setChecked(true);
                        else if (var.equals(mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos2.getText().toString().trim()))
                            mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos2.setChecked(true);
                    }
                }
            } else {
                mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumRbStatusNo.setChecked(true);
            }

            if (info.getPosVerCheckFlag() != null && info.getPosVerCheckFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerRbStatusYes.setChecked(true);
                if (info.getPosVerVal() != null) {
                    String[] vars = info.getPosVerVal().split("|");
                    if (vars.length > 1) {
                        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer1.setText(vars[0]);
                        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer2.setText(vars[1]);
                    } else if (vars.length == 1) {
                        mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer1.setText(vars[0]);
                    }
                }
            } else {
                mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerRbStatusNo.setChecked(true);
            }

            if (info.getOpenday() != null) {
                DebugLog.e(TAG, "openday : " + info.getOpenday());
                try {
                    String yearStr = info.getOpenday().substring(0, 4);
                    String monthStr = info.getOpenday().substring(5, 7);
                    String dayStr = info.getOpenday().substring(8, 10);
                    DebugLog.e(TAG, "yearStr : " + yearStr);
                    DebugLog.e(TAG, "monthStr : " + monthStr);
                    DebugLog.e(TAG, "dayStr : " + dayStr);
                    int year = Integer.parseInt(yearStr);
                    int month = Integer.parseInt(monthStr);
                    int day = Integer.parseInt(dayStr);
                    mCheckOpenSelectedYear = year;
                    mCheckOpenSelectedMonth = month;
                    mCheckOpenSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegIncCheckDate.compCheckDateTvOpenDate,
                            getDateString(mCheckOpenSelectedYear, mCheckOpenSelectedMonth, mCheckOpenSelectedDay)
                    );
                    mCheckOpenDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                        mCheckOpenSelectedYear = y;
                        mCheckOpenSelectedMonth = m + 1;
                        mCheckOpenSelectedDay = d;
                        setTextAtView(
                                mBindingViews.workPaperRegIncCheckDate.compCheckDateTvOpenDate,
                                getDateString(mCheckOpenSelectedYear, mCheckOpenSelectedMonth, mCheckOpenSelectedDay)
                        );
                    }, mCheckOpenSelectedYear, mCheckOpenSelectedMonth - 1, mCheckOpenSelectedDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DebugLog.e(TAG, "openday is null");
            }

            if (info.getStartday() != null) {
                DebugLog.e(TAG, "startday : " + info.getStartday());
                try {
                    String yearStr = info.getStartday().substring(0, 4);
                    String monthStr = info.getStartday().substring(5, 7);
                    String dayStr = info.getStartday().substring(8, 10);
                    DebugLog.e(TAG, "yearStr : " + yearStr);
                    DebugLog.e(TAG, "monthStr : " + monthStr);
                    DebugLog.e(TAG, "dayStr : " + dayStr);
                    int year = Integer.parseInt(yearStr);
                    int month = Integer.parseInt(monthStr);
                    int day = Integer.parseInt(dayStr);
                    mCheckSalesSelectedYear = year;
                    mCheckSalesSelectedMonth = month;
                    mCheckSalesSelectedDay = day;
                    setTextAtView(
                            mBindingViews.workPaperRegIncCheckDate.compCheckDateTvSalesDate,
                            getDateString(mCheckSalesSelectedYear, mCheckSalesSelectedMonth, mCheckSalesSelectedDay)
                    );
                    mCheckSalesDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                        mCheckSalesSelectedYear = y;
                        mCheckSalesSelectedMonth = m + 1;
                        mCheckSalesSelectedDay = d;
                        setTextAtView(
                                mBindingViews.workPaperRegIncCheckDate.compCheckDateTvSalesDate,
                                getDateString(mCheckSalesSelectedYear, mCheckSalesSelectedMonth, mCheckSalesSelectedDay)
                        );
                    }, mCheckSalesSelectedYear, mCheckSalesSelectedMonth - 1, mCheckSalesSelectedDay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                DebugLog.e(TAG, "startday is null");
            }

            if (info.getGotCheckFlag() != null && info.getGotCheckFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckGot.compCheckGotRbStatusYes.setChecked(true);
                if (info.getGotVal() != null) {
                    if (info.getGotVal().equals(mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWpa.getText().toString().trim()))
                        mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWpa.setChecked(true);
                    else if (info.getGotVal().equals(mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWep.getText().toString().trim()))
                        mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWep.setChecked(true);
                }
            } else {
                mBindingViews.workPaperRegIncCheckGot.compCheckGotRbStatusNo.setChecked(true);
            }

            if (info.getInternetFlag() != null && info.getInternetFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckInternet.compCheckInternetRbStatusYes.setChecked(true);
                if (info.getInternetVal() != null)
                    mBindingViews.workPaperRegIncCheckInternet.compCheckInternetEtAgency.setText(info.getInternetVal());
            } else {
                mBindingViews.workPaperRegIncCheckInternet.compCheckInternetRbStatusNo.setChecked(true);
            }

            if (info.getScPgmFlag() != null && info.getScPgmFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmRbStatusYes.setChecked(true);
                if (info.getScPgmVal() != null)
                    mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmEtVersion.setText(info.getScPgmVal());
            } else {
                mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmRbStatusNo.setChecked(true);
            }

            if (info.getPosCashFlag() != null && info.getPosCashFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashRbStatusYes.setChecked(true);
                if (info.getPosCashVal() != null)
                    mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashEtNumber.setText(info.getPosCashVal());
            } else {
                mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashRbStatusNo.setChecked(true);
            }

            if (info.getPosCreditFlag() != null && info.getPosCreditFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditRbStatusYes.setChecked(true);
                if (info.getPosCreditVal() != null)
                    mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditEtNumber.setText(info.getPosCreditVal());
            } else {
                mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditRbStatusNo.setChecked(true);
            }

            if (info.getPosPointFlag() != null && info.getPosPointFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceRbStatusYes.setChecked(true);
                if (info.getPosPointVal() != null) {
                    String[] vars = info.getPosPointVal().split("|");
                    if (vars.length == 2) {
                        mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceEtType.setText(vars[0]);
                        mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceEtPrice.setText(vars[1]);
                    }
                }
                    mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceEtType.setText(info.getPosPointVal());
            } else {
                mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceRbStatusNo.setChecked(true);
            }

            if (info.getScWorkFlag() != null && info.getScWorkFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkRbStatusYes.setChecked(true);
                if (info.getScWorkVal() != null)
                    mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkEtProduce.setText(info.getScWorkVal());
            } else {
                mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkRbStatusNo.setChecked(true);
            }

            if (info.getGotUpdateFlag() != null && info.getGotUpdateFlag().equals("Y")) {
                mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateRbStatusYes.setChecked(true);
                if (info.getGotUpdateVal() != null)
                    mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateEtVersion.setText(info.getGotUpdateVal());
            } else {
                mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateRbStatusNo.setChecked(true);
            }
        }

        if (data.getDongleList() != null) {
            for (RespInstallInfoDongle info : data.getDongleList()) {
                if (info.getLsamName() != null && info.getLsamName().equals("티머니")) {
                    mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyEtReason.setText(info.getReason());
                    if (info.getLsamFlag() != null && info.getLsamFlag().equals("O"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamYes.setChecked(true);
                    else if (info.getLsamFlag() != null && info.getLsamFlag().equals("X"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamNo.setChecked(true);
                    if (info.getCr_test() != null && info.getCr_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbChargeYes.setChecked(true);
                    else if (info.getCr_test() != null && info.getCr_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbChargeNo.setChecked(true);
                    if (info.getPay_test() != null && info.getPay_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbPayYes.setChecked(true);
                    else if (info.getPay_test() != null && info.getPay_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbPayNo.setChecked(true);
                } else if (info.getLsamName() != null && info.getLsamName().equals("한페이")) {
                    mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayEtReason.setText(info.getReason());
                    if (info.getLsamFlag() != null && info.getLsamFlag().equals("O"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamYes.setChecked(true);
                    else if (info.getLsamFlag() != null && info.getLsamFlag().equals("X"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamNo.setChecked(true);
                    if (info.getCr_test() != null && info.getCr_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbChargeYes.setChecked(true);
                    else if (info.getCr_test() != null && info.getCr_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbChargeNo.setChecked(true);
                    if (info.getPay_test() != null && info.getPay_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbPayYes.setChecked(true);
                    else if (info.getPay_test() != null && info.getPay_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbPayNo.setChecked(true);
                } else if (info.getLsamName() != null && info.getLsamName().equals("마이비")) {
                    mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeEtReason.setText(info.getReason());
                    if (info.getLsamFlag() != null && info.getLsamFlag().equals("O"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamYes.setChecked(true);
                    else if (info.getLsamFlag() != null && info.getLsamFlag().equals("X"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamNo.setChecked(true);
                    if (info.getCr_test() != null && info.getCr_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbChargeYes.setChecked(true);
                    else if (info.getCr_test() != null && info.getCr_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbChargeNo.setChecked(true);
                    if (info.getPay_test() != null && info.getPay_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbPayYes.setChecked(true);
                    else if (info.getPay_test() != null && info.getPay_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbPayNo.setChecked(true);
                } else if (info.getLsamName() != null && info.getLsamName().equals("캐시비")) {
                    mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeEtReason.setText(info.getReason());
                    if (info.getLsamFlag() != null && info.getLsamFlag().equals("O"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamYes.setChecked(true);
                    else if (info.getLsamFlag() != null && info.getLsamFlag().equals("X"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamNo.setChecked(true);
                    if (info.getCr_test() != null && info.getCr_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbChargeYes.setChecked(true);
                    else if (info.getCr_test() != null && info.getCr_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbChargeNo.setChecked(true);
                    if (info.getPay_test() != null && info.getPay_test().equals("O"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbPayYes.setChecked(true);
                    else if (info.getPay_test() != null && info.getPay_test().equals("X"))
                        mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbPayNo.setChecked(true);
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
                    mBindingViews.workPaperRegLlDeviceList.addView(component);
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
                    if (mBindingViews.workPaperRegRvDevice.getAdapter() != null)
                        mBindingViews.workPaperRegRvDevice.getAdapter().notifyDataSetChanged();
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

    private ReqInstall getInstallData() {
        ReqInstall param = new ReqInstall();
        param.setSeq(paperSeq);
        param.setCode(paperCode);
        param.setOrgan(paperOrgan);
        param.setOrgan_type(mOrganTypes.get(mBindingViews.workPaperRegSpStoreType.getSelectedItemPosition()));
        if (mBindingViews.workPaperRegRbWorkTypeAdd.isChecked()) {
            param.setWork_type("설치");
        } else if (mBindingViews.workPaperRegRbWorkTypeRemove.isChecked()) {
            param.setWork_type("철수");
        }
        param.setWorkdivision(paperWorkDivision);
        String openDay = String.format("%04d%02d%02d", mOpenSelectedYear, mOpenSelectedMonth, mOpenSelectedDay);
        param.setOpenday(openDay);
        String workDay = String.format("%04d%02d%02d", mWorkSelectedYear, mWorkSelectedMonth, mWorkSelectedDay);
        param.setSetupday(workDay);
        String visitTime = String.format("%02d%02d00", mComeHour, mComeMinute);
        String comTime = String.format("%02d%02d00", mWorkHour, mWorkMinute);
        param.setVisit_datetime(workDay + " " + visitTime);
        param.setCom_datetime(workDay + " " + comTime);
        param.setCe_name(paperCeName);
        LocalDate now = LocalDate.now();
        param.setCreate_date(String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth()));
        List<ReqInstallDev> devList = new ArrayList<>();
        for (WorkPaperDeviceDefaultComponent component : mDeviceComponents) {
            ReqInstallDev data = component.getData();
            if (data != null) devList.add(data);
        }
        param.setDev_list(devList);

        ReqInstallSam samInfo = new ReqInstallSam();
        String sam1 = mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtId1.getText().toString().trim();
        String tm1 = mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtTmoney1.getText().toString().trim();
        String sam2 = mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtId2.getText().toString().trim();
        String tm2 = mBindingViews.workPaperRegIncDeviceSam.compDeviceSamEtTmoney2.getText().toString().trim();
        boolean samCheck = false;
        if (!sam1.isEmpty()) {
            samInfo.setIntsam_1(sam1);
            samCheck = true;
        }
        if (!tm1.isEmpty()) {
            samInfo.setTmoney_1(tm1);
            samCheck = true;
        }
        if (!sam2.isEmpty()) {
            samInfo.setIntsam_2(sam2);
            samCheck = true;
        }
        if (!tm2.isEmpty()) {
            samInfo.setTmoney_2(tm2);
            samCheck = true;
        }
        if (samCheck)
            param.setSam_info(samInfo);

        ReqInstallCheck checkInfo = new ReqInstallCheck();
        boolean posNumCheck = mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumRbStatusYes.isChecked();
        if (posNumCheck) {
            checkInfo.setPos_num_check_flag("Y");
            String posNumVal = "";
            if (mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.isChecked())
                posNumVal += mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.getText().toString().trim();
            if (mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos2.isChecked()) {
                if (posNumVal.isEmpty()) {
                    posNumVal += mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.getText().toString().trim();
                } else {
                    posNumVal += "|" + mBindingViews.workPaperRegIncCheckPosNum.compCheckPosNumCbSetPos1.getText().toString().trim();
                }
            }
            if (!posNumVal.isEmpty()) checkInfo.setPos_num_val(posNumVal);
        } else {
            checkInfo.setPos_num_check_flag("N");
        }

        boolean posVerCheck = mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerRbStatusYes.isChecked();
        if (posVerCheck) {
            checkInfo.setPos_ver_check_flag("Y");
            String posVerVal = "";
            posVerVal += mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer1.getText().toString().trim();
            if (posVerVal.isEmpty()) {
                posVerVal += mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer2.getText().toString().trim();
            } else {
                posVerVal += "|" + mBindingViews.workPaperRegIncCheckPosVer.compCheckPosVerEtVer2.getText().toString().trim();
            }
            if (!posVerVal.isEmpty()) checkInfo.setPos_num_val(posVerVal);
        } else {
            checkInfo.setPos_ver_check_flag("N");
        }

        String checkOpenday = String.format("%04d%02d%02d", mCheckOpenSelectedYear, mCheckOpenSelectedMonth, mCheckOpenSelectedDay);
        checkInfo.setOpenday(checkOpenday);
        String checkSalesday = String.format("%04d%02d%02d", mCheckSalesSelectedYear, mCheckSalesSelectedMonth, mCheckSalesSelectedDay);
        checkInfo.setStartday(checkSalesday);

        boolean gotCheck = mBindingViews.workPaperRegIncCheckGot.compCheckGotRbStatusYes.isChecked();
        if (gotCheck) {
            checkInfo.setGot_check_flag("Y");
            if (mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWpa.isChecked()) {
                checkInfo.setGot_val(mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWpa.getText().toString().trim());
            } else if (mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWep.isChecked()) {
                checkInfo.setGot_val(mBindingViews.workPaperRegIncCheckGot.compCheckGotRbDivisionWep.getText().toString().trim());
            }
        } else {
            checkInfo.setGot_check_flag("N");
        }

        boolean internetCheck = mBindingViews.workPaperRegIncCheckInternet.compCheckInternetRbStatusYes.isChecked();
        if (internetCheck) {
            checkInfo.setInternet_flag("Y");
            String internetVal = mBindingViews.workPaperRegIncCheckInternet.compCheckInternetEtAgency.getText().toString().trim();
            if (!internetVal.isEmpty()) checkInfo.setInternet_val(internetVal);

            boolean scPgmCheck = mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmRbStatusYes.isChecked();
            if (scPgmCheck) {
                checkInfo.setSc_pgm_flag("Y");
                String scPgmVal = mBindingViews.workPaperRegIncCheckScPgm.compCheckScPgmEtVersion.getText().toString().trim();
                if (!scPgmVal.isEmpty()) checkInfo.setSc_pgm_val(scPgmVal);
            } else {
                checkInfo.setSc_pgm_flag("N");
            }

            boolean posCashCheck = mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashRbStatusYes.isChecked();
            if (posCashCheck) {
                checkInfo.setPos_cash_flag("Y");
                String posCashVal = mBindingViews.workPaperRegIncCheckPosCash.compCheckPosCashEtNumber.getText().toString().trim();
                if (!posCashVal.isEmpty()) checkInfo.setPos_cash_val(posCashVal);
            } else {
                checkInfo.setPos_cash_flag("N");
            }

            boolean posCreditCheck = mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditRbStatusYes.isChecked();
            if (posCreditCheck) {
                checkInfo.setPos_credit_flag("Y");
                String posCreditVal = mBindingViews.workPaperRegIncCheckPosCredit.compCheckPosCreditEtNumber.getText().toString().trim();
                if (!posCreditVal.isEmpty()) checkInfo.setPos_credit_val(posCreditVal);
            } else {
                checkInfo.setPos_credit_flag("N");
            }

            boolean posPointCheck = mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceRbStatusYes.isChecked();
            if (posPointCheck) {
                checkInfo.setPos_point_flag("Y");
                String type = mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceEtType.getText().toString().trim();
                String price = mBindingViews.workPaperRegIncCheckPosService.compCheckPosServiceEtPrice.getText().toString().trim();
                checkInfo.setPos_point_val(type + "|" + price);
            } else {
                checkInfo.setPos_point_flag("N");
            }

            boolean scWorkCheck = mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkRbStatusYes.isChecked();
            if (scWorkCheck) {
                checkInfo.setSc_work_flag("Y");
                String scWorkVal = mBindingViews.workPaperRegIncCheckScWork.compCheckScWorkEtProduce.getText().toString().trim();
                if (!scWorkVal.isEmpty()) checkInfo.setSc_work_val(scWorkVal);
            } else {
                checkInfo.setSc_work_flag("N");
            }

            boolean gotUpdateCheck = mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateRbStatusYes.isChecked();
            if (gotUpdateCheck) {
                checkInfo.setGot_update_flag("Y");
                String gotUpdateVal = mBindingViews.workPaperRegIncCheckGotUpdate.compCheckGotUpdateEtVersion.getText().toString().trim();
                if (!gotUpdateVal.isEmpty()) checkInfo.setGot_update_val(gotUpdateVal);
            } else {
                checkInfo.setGot_update_flag("N");
            }

            List<ReqInstallDongle> dongleList = new ArrayList<>();
            ReqInstallDongle tmoney = new ReqInstallDongle();
            tmoney.setLsam_name("티머니");
            if (mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamYes.isChecked()) {
                tmoney.setLsam_flag("O");
            } else if (mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbLsamNo.isChecked()) {
                tmoney.setLsam_flag("X");
                String reason = mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyEtReason.getText().toString().trim();
                if (!reason.isEmpty()) tmoney.setReason(reason);
            } else {
                tmoney = null;
            }
            if (tmoney != null) {
                if (mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbChargeYes.isChecked()) {
                    tmoney.setCr_test("O");
                } else {
                    tmoney.setCr_test("X");
                }
                if (mBindingViews.workPaperRegIncCheckTmoney.compCheckTmoneyRbPayYes.isChecked()) {
                    tmoney.setPay_test("O");
                } else {
                    tmoney.setPay_test("X");
                }
                dongleList.add(tmoney);
            }

            ReqInstallDongle hanpay = new ReqInstallDongle();
            hanpay.setLsam_name("한페이");
            if (mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamYes.isChecked()) {
                hanpay.setLsam_flag("O");
            } else if (mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbLsamNo.isChecked()) {
                hanpay.setLsam_flag("X");
                String reason = mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayEtReason.getText().toString().trim();
                if (!reason.isEmpty()) hanpay.setReason(reason);
            } else {
                hanpay = null;
            }
            if (hanpay != null) {
                if (mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbChargeYes.isChecked()) {
                    hanpay.setCr_test("O");
                } else {
                    hanpay.setCr_test("X");
                }
                if (mBindingViews.workPaperRegIncCheckHanpay.compCheckHanpayRbPayYes.isChecked()) {
                    hanpay.setPay_test("O");
                } else {
                    hanpay.setPay_test("X");
                }
                dongleList.add(hanpay);
            }

            ReqInstallDongle mybee = new ReqInstallDongle();
            mybee.setLsam_name("마이비");
            if (mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamYes.isChecked()) {
                mybee.setLsam_flag("O");
            } else if (mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbLsamNo.isChecked()) {
                mybee.setLsam_flag("X");
                String reason = mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeEtReason.getText().toString().trim();
                if (!reason.isEmpty()) mybee.setReason(reason);
            } else {
                mybee = null;
            }
            if (mybee != null) {
                if (mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbChargeYes.isChecked()) {
                    mybee.setCr_test("O");
                } else {
                    mybee.setCr_test("X");
                }
                if (mBindingViews.workPaperRegIncCheckMybee.compCheckMybeeRbPayYes.isChecked()) {
                    mybee.setPay_test("O");
                } else {
                    mybee.setPay_test("X");
                }
                dongleList.add(mybee);
            }

            ReqInstallDongle cashbee = new ReqInstallDongle();
            cashbee.setLsam_name("캐시비");
            if (mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamYes.isChecked()) {
                cashbee.setLsam_flag("O");
            } else if (mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbLsamNo.isChecked()) {
                cashbee.setLsam_flag("X");
                String reason = mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeEtReason.getText().toString().trim();
                if (!reason.isEmpty()) cashbee.setReason(reason);
            } else {
                cashbee = null;
            }
            if (cashbee != null) {
                if (mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbChargeYes.isChecked()) {
                    cashbee.setCr_test("O");
                } else {
                    cashbee.setCr_test("X");
                }
                if (mBindingViews.workPaperRegIncCheckCashbee.compCheckCashbeeRbPayYes.isChecked()) {
                    cashbee.setPay_test("O");
                } else {
                    cashbee.setPay_test("X");
                }
                dongleList.add(cashbee);
            }
            if (dongleList.size() > 0)
                param.setDongle_list(dongleList);
        } else {
            checkInfo.setInternet_flag("N");
            checkInfo.setSc_pgm_flag("N");
            checkInfo.setPos_cash_flag("N");
            checkInfo.setPos_credit_flag("N");
            checkInfo.setPos_point_flag("N");
            checkInfo.setSc_work_flag("N");
            checkInfo.setGot_update_flag("N");
        }

        param.setCheck_info(checkInfo);
        return param;
    }

    private void reqWorkPaperReg() {
        showProgress();

        ReqBase<ReqInstall> body = new ReqBase<>();
        body.setReq_param(getInstallData());

        Call<RespBase> call = RetrofitService.getInstance().getService().reqInstallReg(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "responseData is null");
                    makeToast("설치확인서 등록에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("설치확인서 등록 완료.");
                    onBackPressed();
                } else {
                    makeToast("설치확인서 등록에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "work paper reg failure");
                makeToast("설치확인서 등록에 실패했습니다.");
            }
        });
    }

    private void reqWorkPaperMod() {
        showProgress();

        ReqBase<ReqInstall> body = new ReqBase<>();
        body.setReq_param(getInstallData());

        Call<RespBase> call = RetrofitService.getInstance().getService().reqInstallModify(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "responseData is null");
                    makeToast("설치확인서 수정에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("설치확인서 수정 완료.");
                    onBackPressed();
                } else {
                    makeToast("설치확인서 수정에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "work paper mod failure");
                makeToast("설치확인서 수정에 실패했습니다.");
            }
        });
    }
}
