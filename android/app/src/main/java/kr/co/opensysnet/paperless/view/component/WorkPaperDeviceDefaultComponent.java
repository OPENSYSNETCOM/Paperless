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
import kr.co.opensysnet.paperless.databinding.CompWorkPaperDeviceDefaultBinding;
import kr.co.opensysnet.paperless.model.request.ReqInstallDev;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.widget.DeviceSpinnerAdapter;

public class WorkPaperDeviceDefaultComponent extends LinearLayout {
    private static final String TAG = "WorkPaperDeviceDefaultComponent";
    private CompWorkPaperDeviceDefaultBinding mBindingViews;

    private RespDeviceInfo<RespDeviceModelInfo> data = null;
    private List<RespDeviceModelInfo> modelList;
    private RespInstallInfoDev mRegData;
    private boolean isFold = true;

    private Context mContext;

    public WorkPaperDeviceDefaultComponent(Context context) {
        super(context);
        mContext = context;
        dataBinding();
        initView();
    }

    public WorkPaperDeviceDefaultComponent(Context context, RespDeviceInfo<RespDeviceModelInfo> data) {
        super(context);
        mContext = context;
        dataBinding();
        setData(data);
        initView();
    }

    public WorkPaperDeviceDefaultComponent(Context context, RespDeviceInfo<RespDeviceModelInfo> data, RespInstallInfoDev regData) {
        super(context);
        mContext = context;
        mRegData = regData;
        dataBinding();
        setData(data);
        initView();
    }

    public WorkPaperDeviceDefaultComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs);
        initView();
    }

    public WorkPaperDeviceDefaultComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs, defStyle);
        initView();
    }

    private void dataBinding() {
        mBindingViews = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.comp_work_paper_device_default, this, true);
    }

    private void initView() {
        if (data != null && data.getModelList() != null) {
            DebugLog.e(TAG, "Data is not null");
            DeviceSpinnerAdapter adapter = new DeviceSpinnerAdapter(mContext, data.getModelList());
            mBindingViews.compWorkDeviceDefaultSpSpinner.setAdapter(adapter);
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

        mBindingViews.compWorkDeviceDefaultTvTitle.setText(data.getDevName());
        isFold = true;
        mBindingViews.compWorkDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
        mBindingViews.compWorkDeviceDefaultLlContent.setVisibility(GONE);
        mBindingViews.compWorkDeviceDefaultLlTitle.setOnClickListener((v) -> {
            isFold = !isFold;
            if (isFold) {
                mBindingViews.compWorkDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slidedown));
                mBindingViews.compWorkDeviceDefaultLlContent.setVisibility(GONE);
            } else {
                mBindingViews.compWorkDeviceDefaultIvTitle.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_slideup));
                mBindingViews.compWorkDeviceDefaultLlContent.setVisibility(VISIBLE);
            }
        });

        if (mRegData != null) {
            mBindingViews.compWorkDeviceDefaultEtCode.setText(mRegData.getAssetCode());

            if (mRegData.getDevType() != null) {
                if (mRegData.getDevType().equals("신규"))
                    mBindingViews.compWorkDeviceDefaultRbTypeNew.setChecked(true);
                else if (mRegData.getDevType().equals("중고"))
                    mBindingViews.compWorkDeviceDefaultRbTypeOld.setChecked(true);
            }

            if (mRegData.getStatus() != null) {
                if (mRegData.getStatus().equals("양호"))
                    mBindingViews.compWorkDeviceDefaultRbStatusGood.setChecked(true);
                else if (mRegData.getStatus().equals("불량"))
                    mBindingViews.compWorkDeviceDefaultRbStatusBad.setChecked(true);
            }

            if (mRegData.getModelName() != null) {
                for (int i = 0; i < modelList.size(); i++) {
                    if (mRegData.getModelName().equals(modelList.get(i).getModelName())) {
                        mBindingViews.compWorkDeviceDefaultSpSpinner.setSelection(i);
                        break;
                    }
                }
            }

            mBindingViews.compWorkDeviceDefaultEtRemark.setText(mRegData.getDescription());
        }
    }

    public ReqInstallDev getData() {
        ReqInstallDev data = new ReqInstallDev();

        data.setDev_name(this.data.getDevName());
        data.setDev_order(this.data.getDevOrder());

        if (mBindingViews.compWorkDeviceDefaultRbTypeNew.isChecked()) {
            data.setDev_type("신규");
        } else if (mBindingViews.compWorkDeviceDefaultRbTypeOld.isChecked()) {
            data.setDev_type("중고");
        }
        data.setModel_name(this.data.getModelList().get(mBindingViews.compWorkDeviceDefaultSpSpinner.getSelectedItemPosition()).getModelName());
        String code = mBindingViews.compWorkDeviceDefaultEtCode.getText().toString().trim();
        if (!code.isEmpty()) data.setAsset_code(code);

        if (mBindingViews.compWorkDeviceDefaultRbStatusGood.isChecked()) {
            data.setStatus("양호");
        } else if (mBindingViews.compWorkDeviceDefaultRbStatusBad.isChecked()) {
            data.setStatus("불량");
        }
        data.setDescription(mBindingViews.compWorkDeviceDefaultEtRemark.getText().toString().trim());

        return data;
    }
}
