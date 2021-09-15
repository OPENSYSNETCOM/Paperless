package kr.co.opensysnet.paperless.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.opensysnet.paperless.databinding.DialogSimpleBinding;
import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.utils.DebugLog;

public class SimpleDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "MessageDialog";
    private Context mContext;

    private DialogSimpleBinding mBindingViews;

    private SimpleDialogListener mListener;

    private String title;
    private String message;

    public interface SimpleDialogListener {
        void onConfirmListener();
        void onCancelListener();
    }

    public SimpleDialog(Context context) {
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
        mBindingViews = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.dialog_simple, null, false);
        setContentView(mBindingViews.getRoot());

        setLayout();
        setCancelable(true);
    }

    private void setLayout() {
        mBindingViews.dialogSimpleTvTitle.setText(title);
        mBindingViews.dialogSimpleTvMessage.setText(message);
        mBindingViews.dialogSimpleBtnConfirm.setOnClickListener(this);
        mBindingViews.dialogSimpleBtnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBindingViews.dialogSimpleBtnConfirm) {
            if (mListener == null) {
                dismiss();
            } else {
                mListener.onConfirmListener();
            }
        } else if (v == mBindingViews.dialogSimpleBtnCancel) {
            if (mListener == null) {
                dismiss();
            } else {
                mListener.onCancelListener();
            }
        }
    }

    public void setListener(SimpleDialogListener listener) {
        mListener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
