package kr.co.opensysnet.paperless.view.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    protected Context mContext = null;

    protected List<T> mItemList = null;

    private IListListener mListener = null;

    public interface IListListener {
        /**
         * 리스트 내 아이템마다 다른 레이아웃을 적용하기 위한 필수 메소드.
         *
         * @param position 아이템 포지션.
         * @param itemList 리스트 아이템 데이터 셋.
         * @return {@link IListListener#onReqViewHolderLayout(int)} 에서 사용되어질 레이아웃 정의 값.
         */
        <T> int onGetItemViewType(int position, @Nullable List<T> itemList);

        /**
         * 리스트에서 사용할 레이아웃 ID를 넘겨주도록 구현되어야 하는 필수 메소드.
         *
         * @param viewType 사용자가 사용할 view type.
         * @return 리스트에서 보여질 각 행의 레이아웃 ID.
         */
        @LayoutRes
        int onReqViewHolderLayout(int viewType);

        <T> void onBindViewHolder(@Nullable ViewHolder holder, int position, @Nullable List<T> itemList);

        /**
         * 리스트의 클릭 이벤트를 등록하기 위해 구현되어야 하는 필수 메소드.
         *
         * @param holder 아이템의 클릭 이벤트를 등록하기 위해 ViewHolder를 전달.
         */
        void onSetListItemsListener(@Nullable ViewHolder holder);

        void onViewRecycled(@Nullable ViewHolder holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding mBindingViews;

        public ViewHolder(View view) {
            super(view);
            mBindingViews = DataBindingUtil.bind(view);
        }

        public ViewDataBinding getBinding() {
            return mBindingViews;
        }
    }

    /**
     * Adapter 생성자.
     *
     * @param context  Activity context.
     * @param listener 리스트 아이템 선택 또는 내부의 위젯 클릭 시 이벤트 처리를 위한 리스너.
     * @param itemList 리스트에 보여주기 위한 데이터 집합.
     */
    public RecyclerAdapter(@NonNull Context context, @Nullable IListListener listener, @NonNull List<T> itemList) {
        mContext = context;
        mListener = listener;
        mItemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = requestViewHolderLayout(viewType);

        if (layoutId > 0) {
            View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
            ViewHolder holder = new ViewHolder(view);

            if (mListener != null) {
                mListener.onSetListItemsListener(holder);
            }

            return holder;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mListener != null) {
            mListener.onBindViewHolder(holder, position, mItemList);
        }
    }

    @Override
    public int getItemCount() {
        if (mItemList == null) {
            return 0;
        }

        return mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mListener != null) {
            return mListener.onGetItemViewType(position, mItemList);
        }

        return super.getItemViewType(position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        if (holder != null && mListener != null) {
            mListener.onViewRecycled(holder);
        }

        super.onViewRecycled(holder);
    }

    /**
     * 리스트에서 사용할 레이아웃 ID를 넘겨주도록 구현되어야 하는 필수 메소드.
     *
     * @param viewType 사용자가 사용할 view type.
     * @return 리스트에서 보여질 각 행의 레이아웃 ID.
     */
    private int requestViewHolderLayout(int viewType) {
        int ret = 0;

        if (mListener != null) {
            ret = mListener.onReqViewHolderLayout(viewType);
        }

        return ret;
    }
}
