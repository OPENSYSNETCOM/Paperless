package kr.co.opensysnet.paperless.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.DialogSignatureBinding;
import kr.co.opensysnet.paperless.databinding.DialogSimpleBinding;
import kr.co.opensysnet.paperless.utils.DebugLog;

public class SignatureDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "MessageDialog";
    private Context mContext;

    private DialogSignatureBinding mBindingViews;

    private SimpleDialogListener mListener;

    private String title;
    private String message;

    public interface SimpleDialogListener {
        void onConfirmListener(Bitmap bitmap);
        void onCancelListener();
    }

    public SignatureDialog(Context context) {
        super(context, R.style.Dialog);
        mContext = context;
    }

    @Override
    public void show() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            if (mBindingViews != null) {
                setLayout();
            }
            super.show();
        } else {
            DebugLog.e(TAG, "context error");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBindingViews = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_signature, null, false);
        setContentView(mBindingViews.getRoot());

        setLayout();
        setCancelable(true);
    }

    private void setLayout() {
        mBindingViews.dialogSignatureTvTitle.setText(title);
        mBindingViews.dialogSignatureSpPad.clear();
        mBindingViews.dialogSignatureBtnConfirm.setOnClickListener(this);
        mBindingViews.dialogSignatureBtnCancel.setOnClickListener(this);
        mBindingViews.dialogSignatureBtnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBindingViews.dialogSignatureBtnConfirm) {
            if (mListener == null) {
                dismiss();
            } else {
                mListener.onConfirmListener(mBindingViews.dialogSignatureSpPad.getSignatureBitmap());
            }
        } else if (v == mBindingViews.dialogSignatureBtnCancel) {
            if (mListener == null) {
                dismiss();
            } else {
                mListener.onCancelListener();
            }
        } else if (v == mBindingViews.dialogSignatureBtnClear) {
            mBindingViews.dialogSignatureSpPad.clear();
        }
    }

    public void setListener(SimpleDialogListener listener) {
        mListener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
