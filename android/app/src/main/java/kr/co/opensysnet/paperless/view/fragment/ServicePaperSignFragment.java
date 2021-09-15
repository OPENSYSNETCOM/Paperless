package kr.co.opensysnet.paperless.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
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

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.FragmentServicePaperRegisterBinding;
import kr.co.opensysnet.paperless.databinding.FragmentServicePaperSignBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkDeviceListBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
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
import kr.co.opensysnet.paperless.model.response.RespService;
import kr.co.opensysnet.paperless.model.response.RespServiceInfo;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoDev;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoFix;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoPart;
import kr.co.opensysnet.paperless.model.response.RespSign;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.component.ServicePaperDeviceDefaultComponent;
import kr.co.opensysnet.paperless.view.component.ServicePaperPartComponent;
import kr.co.opensysnet.paperless.view.dialog.SignatureDialog;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicePaperSignFragment extends BaseFragment {
    private static final String TAG = "ServicePaperSignFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentServicePaperSignBinding mBindingViews = null;

    private List<RespDeviceInfo<RespDeviceModelInfo>> mDeviceList;

    private boolean isModify;
    private int paperSeq = -1;
    private String paperCode = null;
    private String paperOrgan = null;
    private String paperWorkDivision = null;
    private String paperCeName = null;

    private boolean isDeviceFold = true;
    private boolean isPartFold = true;

    private List<ServicePaperDeviceDefaultComponent> mDeviceComponents = new ArrayList<>();
    private List<ServicePaperPartComponent> mPartComponents = new ArrayList<>();

    private RespServiceInfo mPaperInfo;

    private SignatureDialog mSignatureDialog;
    private Bitmap mOwnerSignature;
    private Bitmap mTechSignature;

    public static ServicePaperSignFragment newInstance(Bundle bundle) {
        ServicePaperSignFragment fragment = new ServicePaperSignFragment();
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
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_service_paper_sign, container, false);
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
        View actionBar = initActionBar(mBindingViews.servicePaperSignToolbar, R.layout.toolbar_image_text);
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
            if (mOwnerSignature == null) {
                makeToast("점포 확인자 사인이 등록되지 않았습니다.");
                return;
            }
            if (mTechSignature == null) {
                makeToast("기술 담당자 사인이 등록되지 않았습니다.");
                return;
            }
            reqServicePaperSign("OWNER", mOwnerSignature);
            reqServicePaperSign("TECH", mTechSignature);
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
        mBindingViews.servicePaperSignTvStoreName.setText(paperOrgan);
        mBindingViews.servicePaperSignTvStoreCode.setText(paperCode);

        mBindingViews.servicePaperSignTvStoreName.setText("-");
        mBindingViews.servicePaperSignTvStoreCode.setText("-");
        mBindingViews.servicePaperSignTvType.setText("-");
        mBindingViews.servicePaperSignTvPrice.setText("-");

        mBindingViews.servicePaperSignTvReceiptDate.setText("-");
        mBindingViews.servicePaperSignTvReceiptTime.setText("-");
        mBindingViews.servicePaperSignTvVisitDate.setText("-");
        mBindingViews.servicePaperSignTvVisitTime.setText("-");
        mBindingViews.servicePaperSignTvCompDate.setText("-");
        mBindingViews.servicePaperSignTvCompTime.setText("-");

        reqGetPaperInfo();

        isDeviceFold = true;
        isPartFold = true;
        mBindingViews.servicePaperSignIvMenuDevice.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.servicePaperSignIvMenuPart.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown_wh));
        mBindingViews.servicePaperSignLlMenuDeviceContent.setVisibility(View.GONE);
        mBindingViews.servicePaperSignLlMenuPartContent.setVisibility(View.GONE);
        mBindingViews.servicePaperSignLlMenuDevice.setOnClickListener(mViewOnClickListener);
        mBindingViews.servicePaperSignLlMenuPart.setOnClickListener(mViewOnClickListener);

        mBindingViews.servicePaperSignIncSignature.paperSignTvMessage.setText(R.string.paper_sign_msg_work);
        mBindingViews.servicePaperSignIncSignature.paperSignFlTechSign.setOnClickListener(mViewOnClickListener);
        mBindingViews.servicePaperSignIncSignature.paperSignFlOwnerSign.setOnClickListener(mViewOnClickListener);
        mBindingViews.servicePaperSignIncSignature.paperSignTvTechSign.setVisibility(View.VISIBLE);
        mBindingViews.servicePaperSignIncSignature.paperSignIvTechSign.setVisibility(View.GONE);
        mBindingViews.servicePaperSignIncSignature.paperSignTvOwnerSign.setVisibility(View.VISIBLE);
        mBindingViews.servicePaperSignIncSignature.paperSignIvOwnerSign.setVisibility(View.GONE);
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
        if (view == mBindingViews.servicePaperSignLlMenuDevice) {
            isDeviceFold = !isDeviceFold;
            changeFoldWhite(
                    mBindingViews.servicePaperSignIvMenuDevice,
                    mBindingViews.servicePaperSignLlMenuDeviceContent,
                    isDeviceFold
            );
        } else if (view == mBindingViews.servicePaperSignLlMenuPart) {
            isPartFold = !isPartFold;
            changeFoldWhite(
                    mBindingViews.servicePaperSignIvMenuPart,
                    mBindingViews.servicePaperSignLlMenuPartContent,
                    isPartFold
            );
        } else if (view == mBindingViews.servicePaperSignIncSignature.paperSignFlTechSign) {
            if (mSignatureDialog == null) {
                mSignatureDialog = new SignatureDialog(mContext);
            }
            mSignatureDialog.setTitle("기술 담당자 사인");
            mSignatureDialog.setListener(new SignatureDialog.SimpleDialogListener() {
                @Override
                public void onConfirmListener(Bitmap bitmap) {
                    mSignatureDialog.dismiss();
                    mTechSignature = bitmap;
                    mBindingViews.servicePaperSignIncSignature.paperSignTvTechSign.setVisibility(View.GONE);
                    mBindingViews.servicePaperSignIncSignature.paperSignIvTechSign.setVisibility(View.VISIBLE);
                    mBindingViews.servicePaperSignIncSignature.paperSignIvTechSign.setImageBitmap(bitmap);
                }

                @Override
                public void onCancelListener() {
                    mSignatureDialog.dismiss();
                }
            });
            mSignatureDialog.show();
        } else if (view == mBindingViews.servicePaperSignIncSignature.paperSignFlOwnerSign) {
            mSignatureDialog.setTitle("점포 확인자 사인");
            mSignatureDialog.setListener(new SignatureDialog.SimpleDialogListener() {
                @Override
                public void onConfirmListener(Bitmap bitmap) {
                    mSignatureDialog.dismiss();
                    mOwnerSignature = bitmap;
                    mBindingViews.servicePaperSignIncSignature.paperSignTvOwnerSign.setVisibility(View.GONE);
                    mBindingViews.servicePaperSignIncSignature.paperSignIvOwnerSign.setVisibility(View.VISIBLE);
                    mBindingViews.servicePaperSignIncSignature.paperSignIvOwnerSign.setImageBitmap(bitmap);
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

        mBindingViews.servicePaperSignRvDevice.setHasFixedSize(true);
        mBindingViews.servicePaperSignRvDevice.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespDeviceInfo<RespDeviceModelInfo>> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mDeviceList);
        mBindingViews.servicePaperSignRvDevice.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false);
        mBindingViews.servicePaperSignRvDevice.setLayoutManager(layoutManager);

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
            mBindingViews.servicePaperSignLlDeviceList.addView(component);
        } else {
            for (ServicePaperDeviceDefaultComponent component : mDeviceComponents) {
                if (component.getData().getDev_name().equals(data.getDevName()) && component.getData().getDev_order() == data.getDevOrder()) {
                    boolean removeCheck = mDeviceComponents.remove(component);
                    mBindingViews.servicePaperSignLlDeviceList.removeView(component);
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
        mBindingViews.servicePaperSignTvStoreName.setText(data.getOrgan());
        mBindingViews.servicePaperSignTvStoreCode.setText(data.getCode());

        if (data.getWorkType() != null)
            mBindingViews.servicePaperSignTvType.setText(data.getWorkType());
        if (data.getWorkdivision() != null)
            mBindingViews.servicePaperSignTvPrice.setText(data.getWorkdivision());
        if (data.getReceiptDatetime() != null && data.getReceiptDatetime().length() == 15) {
            try {
                String[] dates = data.getReceiptDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 3));
                int month = Integer.parseInt(dates[0].substring(4, 5));
                int day = Integer.parseInt(dates[0].substring(6, 7));

                int hour = Integer.parseInt(dates[1].substring(0, 1));
                int min = Integer.parseInt(dates[1].substring(2, 3));

                setTextAtView(
                        mBindingViews.servicePaperSignTvReceiptDate,
                        getDateString(year, month, day)
                );
                setTextAtView(
                        mBindingViews.servicePaperSignTvReceiptTime,
                        getTimeString(hour, min)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getVisitDatetime() != null && data.getVisitDatetime().length() == 15) {
            try {
                String[] dates = data.getVisitDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 3));
                int month = Integer.parseInt(dates[0].substring(4, 5));
                int day = Integer.parseInt(dates[0].substring(6, 7));

                int hour = Integer.parseInt(dates[1].substring(0, 1));
                int min = Integer.parseInt(dates[1].substring(2, 3));

                setTextAtView(
                        mBindingViews.servicePaperSignTvVisitDate,
                        getDateString(year, month, day)
                );
                setTextAtView(
                        mBindingViews.servicePaperSignTvVisitTime,
                        getTimeString(hour, min)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (data.getComDatetime() != null && data.getComDatetime().length() == 15) {
            try {
                String[] dates = data.getComDatetime().split(" ");
                int year = Integer.parseInt(dates[0].substring(0, 3));
                int month = Integer.parseInt(dates[0].substring(4, 5));
                int day = Integer.parseInt(dates[0].substring(6, 7));

                int hour = Integer.parseInt(dates[1].substring(0, 1));
                int min = Integer.parseInt(dates[1].substring(2, 3));

                setTextAtView(
                        mBindingViews.servicePaperSignTvCompDate,
                        getDateString(year, month, day)
                );
                setTextAtView(
                        mBindingViews.servicePaperSignTvCompTime,
                        getTimeString(hour, min)
                );
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
                mBindingViews.servicePaperSignLlMenuPartList.addView(component);
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
                    mBindingViews.servicePaperSignLlDeviceList.addView(component);
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
                    if (mBindingViews.servicePaperSignRvDevice.getAdapter() != null)
                        mBindingViews.servicePaperSignRvDevice.getAdapter().notifyDataSetChanged();
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

    private void reqServicePaperSign(String type, Bitmap signature) {
        showProgress();
        String reqParam = String.format("{\"seq\":%d,\"type\":\"%s\"}", paperSeq, type);
        // todo get signature image

        File file = bitmapToFile(signature);
        if (file == null) {
            makeToast("사인 파일 변환에 실패했습니다.");
            hideProgress();
            return;
        }
        RequestBody picture = RequestBody.create(file, MediaType.parse("image/*"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("sign_file", file.getName(), picture);

        Call<RespSign> call = RetrofitService.getInstance().getService().reqServiceSign(body, reqParam);
        call.enqueue(new Callback<RespSign>() {
            @Override
            public void onResponse(Call<RespSign> call, Response<RespSign> response) {
                hideProgress();
                RespSign responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "service paper sign failure");
                    makeToast("서비스확인서 사인 등록에 실패했습니다.");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    DebugLog.e(TAG, "service paper sign success");
                    makeToast("서비스확인서 사인 등록이 완료되었습니다.");
                } else {
                    DebugLog.e(TAG, "service paper sign failure");
                    makeToast("서비스확인서 사인 등록에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespSign> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "service paper sign failure");
                makeToast("서비스확인서 사인 등록에 실패했습니다.");
            }
        });
    }
}
