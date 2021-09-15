package kr.co.opensysnet.paperless.view.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.List;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.CompServicePaperDeviceDefaultBinding;
import kr.co.opensysnet.paperless.databinding.CompWorkPaperDeviceDefaultBinding;
import kr.co.opensysnet.paperless.model.request.ReqInstallDev;
import kr.co.opensysnet.paperless.model.request.ReqServiceDev;
import kr.co.opensysnet.paperless.model.request.ReqServiceFix;
import kr.co.opensysnet.paperless.model.response.RespCheckInfoCheck;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoDev;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.widget.DeviceSpinnerAdapter;

public class ServicePaperDeviceDefaultComponent extends LinearLayout {
    private static final String TAG = "ServicePaperDeviceDefaultComponent";
    private CompServicePaperDeviceDefaultBinding mBindingViews;

    private RespDeviceInfo<RespDeviceModelInfo> data = null;
    private List<RespDeviceModelInfo> modelList;
    private RespServiceInfoDev mRegData;
    private boolean isFold = true;

    private Context mContext;

    public ServicePaperDeviceDefaultComponent(Context context) {
        super(context);
        mContext = context;
        dataBinding();
        initView();
    }

    public ServicePaperDeviceDefaultComponent(Context context, RespDeviceInfo<RespDeviceModelInfo> data) {
        super(context);
        mContext = context;
        dataBinding();
        setData(data);
        initView();
    }

    public ServicePaperDeviceDefaultComponent(Context context, RespDeviceInfo<RespDeviceModelInfo> data, RespServiceInfoDev regData) {
        super(context);
        mContext = context;
        mRegData = regData;
        dataBinding();
        setData(data);
        initView();
    }

    public ServicePaperDeviceDefaultComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs);
        initView();
    }

    public ServicePaperDeviceDefaultComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs, defStyle);
        initView();
    }

    private void dataBinding() {
        mBindingViews = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.comp_service_paper_device_default, this, true);
    }

    private void initView() {
        if (data != null && data.getModelList() != null) {
            DebugLog.e(TAG, "Data is not null");
            DeviceSpinnerAdapter adapter = new DeviceSpinnerAdapter(mContext, data.getModelList());
            mBindingViews.compServiceDeviceDefaultSpSpinner.setAdapter(adapter);
        } else {
            DebugLog.e(TAG, "Data is null");
        }
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MapAttrs);

        setTypeArray(typedArray);
    }

    private void getAttrs(AttributeSet attrs, int defStyle) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MapAttrs, defStyle, 0);

        setTypeArray(typedArray);
    }

    private void setTypeArray(TypedArray typeArray) {
        String entries = typeArray.getString(R.styleable.MapAttrs_latLngBoundsNorthEastLatitude);
    }

    private void setData(RespDeviceInfo<RespDeviceModelInfo> data) {
        this.data = data;
        if (modelList == null) modelList = new ArrayList<>();
        modelList.clear();
        if (data.getModelList() != null) modelList.addAll(data.getModelList());

        mBindingViews.compServiceDeviceDefaultTvTitle.setText(data.getDevName());
        isFold = true;
        mBindingViews.compServiceDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.compServiceDeviceDefaultLlContent.setVisibility(GONE);
        mBindingViews.compServiceDeviceDefaultLlTitle.setOnClickListener((v) -> {
            isFold = !isFold;
            if (isFold) {
                mBindingViews.compServiceDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
                mBindingViews.compServiceDeviceDefaultLlContent.setVisibility(GONE);
            } else {
                mBindingViews.compServiceDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slideup));
                mBindingViews.compServiceDeviceDefaultLlContent.setVisibility(VISIBLE);
            }
        });

        // todo set checklist data
        if (data.getCheckList() != null) {
            StringBuffer checkBuffer = new StringBuffer();
            for (int i = 0; i < data.getCheckList().size(); i++) {
                RespCheckInfoCheck check = data.getCheckList().get(i);
                if (check.getCheckItem() != null) {
                    checkBuffer.append(" - ");
                    checkBuffer.append(check.getCheckItem());
                    if (i + 1 < data.getCheckList().size()) {
                        checkBuffer.append("\n");
                    }
                }
            }
            String check = checkBuffer.toString();
            if (!check.isEmpty()) mBindingViews.compServiceDeviceDefaultTvCheckList.setText(check);
        }

        if (mRegData != null) {
            mBindingViews.compServiceDeviceDefaultEtDevNum.setText(String.valueOf(mRegData.getDevNum()));
            if (mRegData.getFix() != null) {
                mBindingViews.compServiceDeviceDefaultEtReason.setText(mRegData.getFix().getProblem());
                mBindingViews.compServiceDeviceDefaultEtResult.setText(mRegData.getFix().getProcess());
            }

            if (mRegData.getModelName() != null) {
                for (int i = 0; i < modelList.size(); i++) {
                    if (mRegData.getModelName().equals(modelList.get(i).getModelName())) {
                        mBindingViews.compServiceDeviceDefaultSpSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    public ReqServiceDev getData() {
        ReqServiceDev data = new ReqServiceDev();

        data.setDev_name(this.data.getDevName());
        data.setDev_order(this.data.getDevOrder());

        data.setModel_name(this.data.getModelList().get(mBindingViews.compServiceDeviceDefaultSpSpinner.getSelectedItemPosition()).getModelName());
        try {
            int devNum = Integer.parseInt(mBindingViews.compServiceDeviceDefaultEtDevNum.getText().toString().trim());
            data.setDev_num(devNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String reason = mBindingViews.compServiceDeviceDefaultEtReason.getText().toString().trim();
        String result = mBindingViews.compServiceDeviceDefaultEtResult.getText().toString().trim();

        if (!reason.isEmpty()) {
            ReqServiceFix fix = new ReqServiceFix();
            fix.setProblem(reason);
            if (!result.isEmpty()) fix.setProcess(result);
            data.setFix(fix);
        }

        return data;
    }
}
