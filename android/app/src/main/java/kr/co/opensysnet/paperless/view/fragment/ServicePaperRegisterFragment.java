package kr.co.opensysnet.paperless.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import kr.co.opensysnet.paperless.databinding.FragmentServicePaperRegisterBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkDeviceListBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqInstall;
import kr.co.opensysnet.paperless.model.request.ReqInstallCheck;
import kr.co.opensysnet.paperless.model.request.ReqInstallDev;
import kr.co.opensysnet.paperless.model.request.ReqInstallDongle;
import kr.co.opensysnet.paperless.model.request.ReqInstallSam;
import kr.co.opensysnet.paperless.model.request.ReqService;
import kr.co.opensysnet.paperless.model.request.ReqServiceDev;
import kr.co.opensysnet.paperless.model.request.ReqServiceFix;
import kr.co.opensysnet.paperless.model.request.ReqServicePart;
import kr.co.opensysnet.paperless.model.response.RespBase;
import kr.co.opensysnet.paperless.model.response.RespCheck;
import kr.co.opensysnet.paperless.model.response.RespCheckInfo;
import kr.co.opensysnet.paperless.model.response.RespCheckInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespDevice;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstall;
import kr.co.opensysnet.paperless.model.response.RespInstallInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDongle;
import kr.co.opensysnet.paperless.model.response.RespService;
import kr.co.opensysnet.paperless.model.response.RespServiceInfo;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoDev;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoFix;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoPart;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.component.ServicePaperDeviceDefaultComponent;
import kr.co.opensysnet.paperless.view.component.ServicePaperPartComponent;
import kr.co.opensysnet.paperless.view.component.WorkPaperDeviceDefaultComponent;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import kr.co.opensysnet.paperless.view.widget.TextSpinnerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicePaperRegisterFragment extends BaseFragment {
    private static final String TAG = "ServicePaperRegisterFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentServicePaperRegisterBinding mBindingViews = null;

    private List<RespDeviceInfo<RespDeviceModelInfo>> mDeviceList;

    private boolean isModify;
    private int paperSeq = -1;
    private String paperCode = null;
    private String paperOrgan = null;
    private String paperWorkDivision = null;
    private String paperCeName = null;

    private String mErrorMessage;

    private DatePickerDialog mReceiptDatePicker;
    private int mReceiptSelectedYear = 0;
    private int mReceiptSelectedMonth = 0;
    private int mReceiptSelectedDay = 0;

    private DatePickerDialog mVisitDatePicker;
    private int mVisitSelectedYear = 0;
    private int mVisitSelectedMonth = 0;
    private int mVisitSelectedDay = 0;

    private DatePickerDialog mCompDatePicker;
    private int mCompSelectedYear = 0;
    private int mCompSelectedMonth = 0;
    private int mCompSelectedDay = 0;

    private TimePickerDialog mReceiptTimePicker;
    private int mReceiptHour = 0;
    private int mReceiptMinute = 0;

    private TimePickerDialog mVisitTimePicker;
    private int mVisitHour = 0;
    private int mVisitMinute = 0;

    private TimePickerDialog mCompTimePicker;
    private int mCompHour = 0;
    private int mCompMinute = 0;

    private boolean isDeviceFold = true;
    private boolean isPartFold = true;

    private List<ServicePaperDeviceDefaultComponent> mDeviceComponents = new ArrayList<>();
    private List<ServicePaperPartComponent> mPartComponents = new ArrayList<>();

    private RespServiceInfo mPaperInfo;

    public static ServicePaperRegisterFragment newInstance(Bundle bundle) {
        ServicePaperRegisterFragment fragment = new ServicePaperRegisterFragment();
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
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_service_paper_register, container, false);
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
        View actionBar = initActionBar(mBindingViews.servicePaperRegisterToolbar, R.layout.toolbar_image_text);
        if (actionBar != null){
            mToolbarBindingViews = DataBindingUtil.bind(actionBar);

        }
        if (mToolbarBindingViews != null){
            mToolbarBindingViews.toolbarTvTitle.setText(R.string.toolbar_title_service_paper);
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
                reqServicePaperMod();
            } else {
                reqServicePaperReg();
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
        mBindingViews.servicePaperRegTvStoreName.setText(paperOrgan);
        mBindingViews.servicePaperRegTvStoreCode.setText(paperCode);
        reqGetPaperInfo();

        LocalDate now = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        mReceiptSelectedYear = now.getYear();
        mReceiptSelectedMonth = now.getMonthValue();
        mReceiptSelectedDay = now.getDayOfMonth();
        setTextAtView(
                mBindingViews.servicePaperRegTvReceiptDate,
                getDateString(mReceiptSelectedYear, mReceiptSelectedMonth, mReceiptSelectedDay)
        );
        if (mReceiptDatePicker == null) {
            mReceiptDatePicker = new DatePickerDialog(mContext);
            mReceiptDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mReceiptSelectedYear = year;
                    mReceiptSelectedMonth = month + 1;
                    mReceiptSelectedDay = day;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvReceiptDate,
                            getDateString(mReceiptSelectedYear, mReceiptSelectedMonth, mReceiptSelectedDay)
                    );
                }
            });
        }
        mBindingViews.servicePaperRegClReceiptDate.setOnClickListener(mViewOnClickListener);
        mReceiptHour = nowTime.getHour();
        mReceiptMinute = nowTime.getMinute();
        setTextAtView(
                mBindingViews.servicePaperRegTvReceiptTime,
                getTimeString(mReceiptHour, mReceiptMinute)
        );
        if (mReceiptTimePicker == null) {
            mReceiptTimePicker = new TimePickerDialog(mContext, (picker, hour, min) -> {
                mReceiptHour = hour;
                mReceiptMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvReceiptTime,
                        getTimeString(mReceiptHour, mReceiptMinute)
                );
            }, mReceiptHour, mReceiptMinute, true);
        }
        mBindingViews.servicePaperRegClReceiptTime.setOnClickListener(mViewOnClickListener);

        mVisitSelectedYear = now.getYear();
        mVisitSelectedMonth = now.getMonthValue();
        mVisitSelectedDay = now.getDayOfMonth();
        setTextAtView(
                mBindingViews.servicePaperRegTvVisitDate,
                getDateString(mVisitSelectedYear, mVisitSelectedMonth, mVisitSelectedDay)
        );
        if (mVisitDatePicker == null) {
            mVisitDatePicker = new DatePickerDialog(mContext);
            mVisitDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mVisitSelectedYear = year;
                    mVisitSelectedMonth = month + 1;
                    mVisitSelectedDay = day;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvVisitDate,
                            getDateString(mVisitSelectedYear, mVisitSelectedMonth, mVisitSelectedDay)
                    );
                }
            });
        }
        mBindingViews.servicePaperRegClVisitDate.setOnClickListener(mViewOnClickListener);
        mVisitHour = nowTime.getHour();
        mVisitMinute = nowTime.getMinute();
        setTextAtView(
                mBindingViews.servicePaperRegTvVisitTime,
                getTimeString(mVisitHour, mVisitMinute)
        );
        if (mVisitTimePicker == null) {
            mVisitTimePicker = new TimePickerDialog(mContext, (picker, hour, min) -> {
                mVisitHour = hour;
                mVisitMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvVisitTime,
                        getTimeString(mVisitHour, mVisitMinute)
                );
            }, mVisitHour, mVisitMinute, true);
        }
        mBindingViews.servicePaperRegClVisitTime.setOnClickListener(mViewOnClickListener);

        mCompSelectedYear = now.getYear();
        mCompSelectedMonth = now.getMonthValue();
        mCompSelectedDay = now.getDayOfMonth();
        setTextAtView(
                mBindingViews.servicePaperRegTvCompDate,
                getDateString(mCompSelectedYear, mCompSelectedMonth, mCompSelectedDay)
        );
        if (mCompDatePicker == null) {
            mCompDatePicker = new DatePickerDialog(mContext);
            mCompDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mCompSelectedYear = year;
                    mCompSelectedMonth = month + 1;
                    mCompSelectedDay = day;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvCompDate,
                            getDateString(mCompSelectedYear, mCompSelectedMonth, mCompSelectedDay)
                    );
                }
            });
        }
        mBindingViews.servicePaperRegClCompDate.setOnClickListener(mViewOnClickListener);
        mCompHour = nowTime.getHour();
        mCompMinute = nowTime.getMinute();
        setTextAtView(
                mBindingViews.servicePaperRegTvCompTime,
                getTimeString(mCompHour, mCompMinute)
        );
        if (mCompTimePicker == null) {
            mCompTimePicker = new TimePickerDialog(mContext, (picker, hour, min) -> {
                mCompHour = hour;
                mCompMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvCompTime,
                        getTimeString(mCompHour, mCompMinute)
                );
            }, mCompHour, mCompMinute, true);
        }
        mBindingViews.servicePaperRegClCompTime.setOnClickListener(mViewOnClickListener);

        isDeviceFold = true;
        isPartFold = true;
        mBindingViews.servicePaperRegIvMenuDevice.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.servicePaperRegIvMenuPart.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.servicePaperRegLlMenuDeviceContent.setVisibility(View.GONE);
        mBindingViews.servicePaperRegLlMenuPartContent.setVisibility(View.GONE);
        mBindingViews.servicePaperRegLlMenuDevice.setOnClickListener(mViewOnClickListener);
        mBindingViews.servicePaperRegLlMenuPart.setOnClickListener(mViewOnClickListener);
        mBindingViews.servicePaperRegFlPartAdd.setOnClickListener(mViewOnClickListener);
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
        if (view == mBindingViews.servicePaperRegLlMenuDevice) {
            isDeviceFold = !isDeviceFold;
            changeFoldWhite(
                    mBindingViews.servicePaperRegIvMenuDevice,
                    mBindingViews.servicePaperRegLlMenuDeviceContent,
                    isDeviceFold
            );
        } else if (view == mBindingViews.servicePaperRegLlMenuPart) {
            isPartFold = !isPartFold;
            changeFoldWhite(
                    mBindingViews.servicePaperRegIvMenuPart,
                    mBindingViews.servicePaperRegLlMenuPartContent,
                    isPartFold
            );
        } else if (view == mBindingViews.servicePaperRegClReceiptDate) {
            mReceiptDatePicker.show();
        } else if (view == mBindingViews.servicePaperRegClReceiptTime) {
            mReceiptTimePicker.show();
        } else if (view == mBindingViews.servicePaperRegClVisitDate) {
            mVisitDatePicker.show();
        } else if (view == mBindingViews.servicePaperRegClVisitTime) {
            mVisitTimePicker.show();
        } else if (view == mBindingViews.servicePaperRegClCompDate) {
            mCompDatePicker.show();
        } else if (view == mBindingViews.servicePaperRegClCompTime) {
            mCompTimePicker.show();
        } else if (view == mBindingViews.servicePaperRegFlPartAdd) {
            ServicePaperPartComponent component = new ServicePaperPartComponent(mContext, (RespServiceInfoPart) null);
            mPartComponents.add(component);
            mBindingViews.servicePaperRegLlMenuPartList.addView(component);
            mBindingViews.servicePaperRegisterSvContent.post(() -> {
                mBindingViews.servicePaperRegisterSvContent.fullScroll(View.FOCUS_DOWN);
            });
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

        mBindingViews.servicePaperRegRvDevice.setHasFixedSize(true);
        mBindingViews.servicePaperRegRvDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespDeviceInfo<RespDeviceModelInfo>> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mDeviceList);
        mBindingViews.servicePaperRegRvDevice.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false);
        mBindingViews.servicePaperRegRvDevice.setLayoutManager(layoutManager);

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
                for (ServicePaperDeviceDefaultComponent component : mDeviceComponents) {
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
            ServicePaperDeviceDefaultComponent component = new ServicePaperDeviceDefaultComponent(mContext, data);
            mDeviceComponents.add(component);
            mBindingViews.servicePaperRegLlDeviceList.addView(component);
        } else {
            for (ServicePaperDeviceDefaultComponent component : mDeviceComponents) {
                if (component.getData().getDev_name().equals(data.getDevName()) && component.getData().getDev_order() == data.getDevOrder()) {
                    boolean removeCheck = mDeviceComponents.remove(component);
                    mBindingViews.servicePaperRegLlDeviceList.removeView(component);
                    DebugLog.e(TAG, "Remove data : " + removeCheck);
                    break;
                }
            }
        }
    };

    private void reqGetPaperInfo() {
        showProgress();

        ReqService param = new ReqService();
        param.setSeq(paperSeq);

        ReqBase<ReqService> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespService<RespServiceInfo>> call = RetrofitService.getInstance().getService().reqServiceInfo(body);
        call.enqueue(new Callback<RespService<RespServiceInfo>>() {
            @Override
            public void onResponse(Call<RespService<RespServiceInfo>> call, Response<RespService<RespServiceInfo>> response) {
                hideProgress();
                RespService<RespServiceInfo> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    makeToast("서비스확인서 조회에 실패했습니다.");
                    onBackPressed();
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    isModify = true;
                    makeToast("문서 수정입니다.");
                    setData(responseData.getServiceInfo());
                } else if (responseData.getResultCode() == Constants.ServerResultCode.NOT_FOUND) {
                    isModify = false;
                    makeToast("신규 작성입니다.");
                } else {
                    DebugLog.e(TAG, "reqGetPaperInfo failure");
                    makeToast("서비스확인서 조회에 실패했습니다.");
                    onBackPressed();
                }

            }

            @Override
            public void onFailure(Call<RespService<RespServiceInfo>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "reqGetPaperInfo failure");
                makeToast("서비스확인서 조회에 실패했습니다.");
                onBackPressed();
            }
        });
    }

    private void setData(RespServiceInfo data) {
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
        mBindingViews.servicePaperRegTvStoreName.setText(data.getOrgan());
        mBindingViews.servicePaperRegTvStoreCode.setText(data.getCode());
        if (data.getWorkdivision() != null) {
            if (data.getWorkdivision().equals("A/S")) mBindingViews.servicePaperRegRbTypeAs.setChecked(true);
            else if (data.getWorkdivision().equals("정기점검")) mBindingViews.servicePaperRegRbTypeCycle.setChecked(true);
        }
        if (data.getWorkType() != null) {
            if (data.getWorkType().equals("유상")) mBindingViews.servicePaperRegRbPriceCharge.setChecked(true);
            else if (data.getWorkType().equals("무상")) mBindingViews.servicePaperRegRbPriceFree.setChecked(true);
        }
        if (data.getReceiptDatetime() != null) {
            try {
                String[] dates = data.getReceiptDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 4));
                int month = Integer.parseInt(dates[0].substring(5, 7));
                int day = Integer.parseInt(dates[0].substring(8, 10));

                int hour = Integer.parseInt(dates[1].substring(0, 2));
                int min = Integer.parseInt(dates[1].substring(3, 5));

                mReceiptSelectedYear = year;
                mReceiptSelectedMonth = month;
                mReceiptSelectedDay = day;
                setTextAtView(
                        mBindingViews.servicePaperRegTvReceiptDate,
                        getDateString(mReceiptSelectedYear, mReceiptSelectedMonth, mReceiptSelectedDay)
                );
                mReceiptDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                    mReceiptSelectedYear = y;
                    mReceiptSelectedMonth = m + 1;
                    mReceiptSelectedDay = d;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvReceiptDate,
                            getDateString(mReceiptSelectedYear, mReceiptSelectedMonth, mReceiptSelectedDay)
                    );
                }, mReceiptSelectedYear, mReceiptSelectedMonth - 1, mReceiptSelectedDay);
                mReceiptHour = hour;
                mReceiptMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvReceiptTime,
                        getTimeString(mReceiptHour, mReceiptMinute)
                );
                mReceiptTimePicker = new TimePickerDialog(mContext, (picker, h, m) -> {
                    mReceiptHour = h;
                    mReceiptMinute = m;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvReceiptTime,
                            getTimeString(mReceiptHour, mReceiptMinute)
                    );
                }, mReceiptHour, mReceiptMinute, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getVisitDatetime() != null) {
            try {
                String[] dates = data.getVisitDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 4));
                int month = Integer.parseInt(dates[0].substring(5, 7));
                int day = Integer.parseInt(dates[0].substring(8, 10));

                int hour = Integer.parseInt(dates[1].substring(0, 2));
                int min = Integer.parseInt(dates[1].substring(3, 5));

                mVisitSelectedYear = year;
                mVisitSelectedMonth = month;
                mVisitSelectedDay = day;
                setTextAtView(
                        mBindingViews.servicePaperRegTvVisitDate,
                        getDateString(mVisitSelectedYear, mVisitSelectedMonth, mVisitSelectedDay)
                );
                mVisitDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                    mVisitSelectedYear = y;
                    mVisitSelectedMonth = m + 1;
                    mVisitSelectedDay = d;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvVisitDate,
                            getDateString(mVisitSelectedYear, mVisitSelectedMonth, mVisitSelectedDay)
                    );
                }, mVisitSelectedYear, mVisitSelectedMonth - 1, mVisitSelectedDay);

                mVisitHour = hour;
                mVisitMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvVisitTime,
                        getTimeString(mVisitHour, mVisitMinute)
                );
                mVisitTimePicker = new TimePickerDialog(mContext, (picker, h, m) -> {
                    mVisitHour = h;
                    mVisitMinute = m;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvVisitTime,
                            getTimeString(mVisitHour, mVisitMinute)
                    );
                }, mVisitHour, mVisitMinute, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getComDatetime() != null) {
            try {
                String[] dates = data.getComDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 4));
                int month = Integer.parseInt(dates[0].substring(5, 7));
                int day = Integer.parseInt(dates[0].substring(8, 10));

                int hour = Integer.parseInt(dates[1].substring(0, 2));
                int min = Integer.parseInt(dates[1].substring(3, 5));

                mCompSelectedYear = year;
                mCompSelectedMonth = month;
                mCompSelectedDay = day;
                setTextAtView(
                        mBindingViews.servicePaperRegTvCompDate,
                        getDateString(mCompSelectedYear, mCompSelectedMonth, mCompSelectedDay)
                );
                mCompDatePicker = new DatePickerDialog(mContext, (picker, y, m, d) -> {
                    mCompSelectedYear = y;
                    mCompSelectedMonth = m + 1;
                    mCompSelectedDay = d;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvCompDate,
                            getDateString(mCompSelectedYear, mCompSelectedMonth, mCompSelectedDay)
                    );
                }, mCompSelectedYear, mCompSelectedMonth - 1, mCompSelectedDay);

                mCompHour = hour;
                mCompMinute = min;
                setTextAtView(
                        mBindingViews.servicePaperRegTvCompTime,
                        getTimeString(mCompHour, mCompMinute)
                );
                mCompTimePicker = new TimePickerDialog(mContext, (picker, h, m) -> {
                    mCompHour = h;
                    mCompMinute = m;
                    setTextAtView(
                            mBindingViews.servicePaperRegTvCompTime,
                            getTimeString(mCompHour, mCompMinute)
                    );
                }, mCompHour, mCompMinute, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (data.getFixList() != null) {
            for (RespServiceInfoDev dev : data.getDevList()) {
                for (RespServiceInfoFix fix : data.getFixList()) {
                    if (dev.getDevName() != null && dev.getDevName().equals(fix.getDevName())) {
                        dev.setFix(fix);
                    }
                }
            }
        }

        if (data.getPartList() != null) {
            for (RespServiceInfoPart part : data.getPartList()) {
                ServicePaperPartComponent component = new ServicePaperPartComponent(mContext, part);
                mPartComponents.add(component);
                mBindingViews.servicePaperRegLlMenuPartList.addView(component);
            }
        }

        if (mDeviceList != null && mDeviceList.size() > 0) {
            setDeviceData();
        }
    }

    private void setDeviceData() {
        if (mPaperInfo == null || mPaperInfo.getDevList() == null) return;

        if (mPaperInfo.getFixList() != null) {
            for (RespServiceInfoDev info : mPaperInfo.getDevList()) {
                for (RespServiceInfoFix fix : mPaperInfo.getFixList()) {
                    if (fix.getDevName() != null && fix.getDevName().equals(info.getDevName())) {
                        info.setFix(fix);
                    }
                }
            }
        }

        for (RespServiceInfoDev info : mPaperInfo.getDevList()) {
            for (int i = 0; i < mDeviceList.size(); i++) {
                RespDeviceInfo<RespDeviceModelInfo> device = mDeviceList.get(i);

                if (info.getDevName() != null && info.getDevName().equals(device.getDevName())) {
                    ServicePaperDeviceDefaultComponent component = new ServicePaperDeviceDefaultComponent(mContext, device, info);
                    mDeviceComponents.add(component);
                    mBindingViews.servicePaperRegLlDeviceList.addView(component);
                }
            }
        }
    }

    private void reqGetDeviceList() {
        showProgress();
        Call<RespDevice<RespDeviceInfo<RespDeviceModelInfo>>> call = RetrofitService.getInstance().getService().reqServiceDev();
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
                    reqGetCheckList();
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

    private void reqGetCheckList() {
        showProgress();
        Call<RespCheck<RespCheckInfo<RespCheckInfoCheck>>> call = RetrofitService.getInstance().getService().reqServiceCheck();
        call.enqueue(new Callback<RespCheck<RespCheckInfo<RespCheckInfoCheck>>>() {
            @Override
            public void onResponse(Call<RespCheck<RespCheckInfo<RespCheckInfoCheck>>> call, Response<RespCheck<RespCheckInfo<RespCheckInfoCheck>>> response) {
                hideProgress();
                RespCheck<RespCheckInfo<RespCheckInfoCheck>> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    makeToast("점검 목록 조회에 실패했습니다.");
                    onBackPressed();
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    List<RespCheckInfo<RespCheckInfoCheck>> checkList = responseData.getCheckList();

                    if (checkList != null) {
                        for (RespCheckInfo<RespCheckInfoCheck> check : checkList) {
                            for (RespDeviceInfo<RespDeviceModelInfo> device : mDeviceList) {
                                if (device.getDevName() != null && device.getDevName().startsWith(check.getDevName())) {
                                    if (check.getCheckList() != null) {
                                        Collections.sort(check.getCheckList(), (t1, t2) -> {
                                            if (t1.getCheckOrder() < t2.getCheckOrder()) return -1;
                                            else if (t1.getCheckOrder() > t2.getCheckOrder()) return 1;
                                            return 0;
                                        });
                                    }
                                    device.setCheckList(check.getCheckList());
                                }
                            }
                        }
                    }
                    if (mBindingViews.servicePaperRegRvDevice.getAdapter() != null)
                        mBindingViews.servicePaperRegRvDevice.getAdapter().notifyDataSetChanged();
                    if (isModify) setDeviceData();
                } else {
                    DebugLog.e(TAG, "get check list failure");
                    makeToast("점검 목록 조회에 실패했습니다.");
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<RespCheck<RespCheckInfo<RespCheckInfoCheck>>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "get check list failure");
                makeToast("점검 목록 조회에 실패했습니다.");
                onBackPressed();
            }
        });
    }

    private ReqService getServiceData() {
        ReqService param = new ReqService();
        param.setSeq(paperSeq);
        param.setCode(paperCode);
        param.setOrgan(paperOrgan);
        if (mBindingViews.servicePaperRegRbTypeAs.isChecked()) {
            param.setWork_type("A/S");
        } else if (mBindingViews.servicePaperRegRbTypeCycle.isChecked()) {
            param.setWork_type("정기점검");
        } else {
            mErrorMessage = "작업 구분을 선택해주세요";
            return null;
        }
        if (mBindingViews.servicePaperRegRbPriceCharge.isChecked()) {
            param.setWork_type("유상");
        } else if (mBindingViews.servicePaperRegRbPriceFree.isChecked()) {
            param.setWork_type("무상");
        } else {
            mErrorMessage = "작업 비용을 선택해주세요";
            return null;
        }
        param.setWorkdivision(paperWorkDivision);
        String receiptDay = String.format("%04d%02d%02d", mReceiptSelectedYear, mReceiptSelectedMonth, mReceiptSelectedDay);
        String receiptTime = String.format("%02d%02d00", mReceiptHour, mReceiptMinute);
        param.setReceipt_datetime(receiptDay + " " + receiptTime);
        String visitDay = String.format("%04d%02d%02d", mVisitSelectedYear, mVisitSelectedMonth, mVisitSelectedDay);
        String visitTime = String.format("%02d%02d00", mVisitHour, mVisitMinute);
        param.setVisit_datetime(visitDay + " " + visitTime);
        String compDay = String.format("%04d%02d%02d", mCompSelectedYear, mCompSelectedMonth, mCompSelectedDay);
        String compTime = String.format("%02d%02d00", mCompHour, mCompMinute);
        param.setCom_datetime(compDay + " " + compTime);
        param.setCe_name(paperCeName);
        LocalDate now = LocalDate.now();
        param.setCreate_date(String.format("%04d%02d%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth()));
        List<ReqServiceDev> devList = new ArrayList<>();
        List<ReqServiceFix> fixList = new ArrayList<>();
        for (ServicePaperDeviceDefaultComponent component : mDeviceComponents) {
            ReqServiceDev data = component.getData();
            if (data != null) {
                devList.add(data);
                if (data.getFix() != null) {
                    data.getFix().setDev_name(data.getDev_name());
                    fixList.add(data.getFix());
                    data.setFix(null);
                }
            }
        }
        if (devList.size() > 0)
            param.setDev_list(devList);
        if (fixList.size() > 0)
            param.setFix_list(fixList);



        List<ReqServicePart> partList = new ArrayList<>();
        for (ServicePaperPartComponent component : mPartComponents) {
            ReqServicePart data = component.getData();
            if (data != null) partList.add(data);
        }
        if (partList.size() > 0)
            param.setPart_list(partList);
        return param;
    }

    private void reqServicePaperReg() {
        showProgress();

        ReqService param = getServiceData();
        if (param == null) {
            makeToast(mErrorMessage);
            hideProgress();
            return;
        }
        ReqBase<ReqService> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespBase> call = RetrofitService.getInstance().getService().reqServiceReg(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "responseData is null");
                    makeToast("서비스확인서 등록에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("서비스확인서 등록 완료.");
                    onBackPressed();
                } else {
                    makeToast("서비스확인서 등록에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "work paper reg failure");
                makeToast("서비스확인서 등록에 실패했습니다.");
            }
        });
    }

    private void reqServicePaperMod() {
        showProgress();

        ReqService param = getServiceData();
        if (param == null) {
            makeToast(mErrorMessage);
            hideProgress();
            return;
        }

        ReqBase<ReqService> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespBase> call = RetrofitService.getInstance().getService().reqServiceModify(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "responseData is null");
                    makeToast("서비스확인서 수정에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("서비스확인서 수정 완료.");
                    onBackPressed();
                } else {
                    makeToast("서비스확인서 수정에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "work paper mod failure");
                makeToast("서비스확인서 수정에 실패했습니다.");
            }
        });
    }
}
