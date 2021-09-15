package kr.co.opensysnet.paperless.view.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.model.response.RespDeviceModelInfo;

public class TextSpinnerAdapter extends ArrayAdapter {
    List<String> mData;
    Context mContext;
    LayoutInflater inflater;

    public TextSpinnerAdapter(Context context, List<String> model){
        super(context, (int) R.id.spinner_item_tv_text);
        mContext = context;
        if (mData == null) mData = new ArrayList<>();
        mData.clear();
        mData.addAll(model);

        inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        String text = mData.get(position);

        ((TextView)convertView.findViewById(R.id.spinner_item_tv_text)).setText(text);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        String text = mData.get(position);

        ((TextView)convertView.findViewById(R.id.spinner_item_tv_text)).setText(text);

        return convertView;
    }
}
