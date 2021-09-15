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
import kr.co.opensysnet.paperless.databinding.CompServicePaperPartBinding;
import kr.co.opensysnet.paperless.databinding.CompWorkPaperDeviceDefaultBinding;
import kr.co.opensysnet.paperless.model.request.ReqInstallDev;
import kr.co.opensysnet.paperless.model.request.ReqServicePart;
import kr.co.opensysnet.paperless.model.response.RespDeviceInfo;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;
import kr.co.opensysnet.paperless.model.response.RespInstallInfoDev;
import kr.co.opensysnet.paperless.model.response.RespServiceInfoPart;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.widget.DeviceSpinnerAdapter;

public class ServicePaperPartComponent extends LinearLayout {
    private static final String TAG = "ServicePaperPartComponent";
    private CompServicePaperPartBinding mBindingViews;

    private RespServiceInfoPart data = null;

    private Context mContext;

    public ServicePaperPartComponent(Context context) {
        super(context);
        mContext = context;
        dataBinding();
        initView();
    }

    public ServicePaperPartComponent(Context context, RespServiceInfoPart data) {
        super(context);
        mContext = context;
        dataBinding();
        setData(data);
        initView();
    }

    public ServicePaperPartComponent(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs);
        initView();
    }

    public ServicePaperPartComponent(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        mContext = context;
        dataBinding();
        getAttrs(attrs, defStyle);
        initView();
    }

    private void dataBinding() {
        mBindingViews = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.comp_service_paper_part, this, true);
    }

    private void initView() {
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

    private void setData(RespServiceInfoPart data) {
        this.data = data;
        if (data == null) return;

        mBindingViews.compServicePartEtName.setText(data.getPartName());
        mBindingViews.compServicePartEtNum.setText(String.valueOf(data.getPartNum()));
    }

    public ReqServicePart getData() {
        ReqServicePart data = new ReqServicePart();
        String partName = mBindingViews.compServicePartEtName.getText().toString().trim();
        if (partName.isEmpty()) return null;
        data.setPart_name(partName);
        try {
            int num = Integer.parseInt(mBindingViews.compServicePartEtNum.getText().toString().trim());
            data.setPart_num(num);
        } catch (Exception e) {
            return null;
        }
        return data;
    }
}
