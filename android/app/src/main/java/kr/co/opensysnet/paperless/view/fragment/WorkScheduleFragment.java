package kr.co.opensysnet.paperless.view.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.co.opensysnet.paperless.R;
import kr.co.opensysnet.paperless.databinding.FragmentWorkScheduleBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkScheduleBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqSchedule;
import kr.co.opensysnet.paperless.model.response.RespSchedule;
import kr.co.opensysnet.paperless.model.response.RespScheduleInfo;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.activity.MainActivity;
import kr.co.opensysnet.paperless.view.dialog.SimpleDialog;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkScheduleFragment extends BaseFragment {
    private static final String TAG = "WorkScheduleFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentWorkScheduleBinding mBindingViews = null;

    private long mFinishTime = 0L;

    private List<RespScheduleInfo> mScheduleList;

    private DatePickerDialog mDatePicker;
    private int mSelectedYear = 0;
    private int mSelectedMonth = 0;
    private int mSelectedDay = 0;
    private String mSelectedDayName = "";

    public static WorkScheduleFragment newInstance(Bundle bundle) {
        WorkScheduleFragment fragment = new WorkScheduleFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public boolean onBackPressed() {
        if (System.currentTimeMillis() - mFinishTime >= 3000) {
            mFinishTime = System.currentTimeMillis();
            Toast.makeText(mContext, R.string.txt_exit_check, Toast.LENGTH_LONG).show();
            return true;
        } else {
            System.exit(0);
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_work_schedule, container, false);
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
        View actionBar = initActionBar(mBindingViews.workScheduleToolbar, R.layout.toolbar_image_text);
        if (actionBar != null){
            mToolbarBindingViews = DataBindingUtil.bind(actionBar);
        }
        if (mToolbarBindingViews != null){
            mToolbarBindingViews.toolbarTvTitle.setText(R.string.toolbar_title_work_schedule);
            mToolbarBindingViews.toolbarIvLeftIcon.setImageResource(R.drawable.ic_menu);

            mToolbarBindingViews.toolbarFlLeft.setOnClickListener(mToolbarOnClickListener);
        }
    }

    private final View.OnClickListener mToolbarOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view == mToolbarBindingViews.toolbarFlLeft) {
            ((MainActivity) mContext).drawerActive(true);
        }
    };

    private void initView() {
        LocalDate now = LocalDate.now();
        mSelectedYear = now.getYear();
        mSelectedMonth = now.getMonthValue();
        mSelectedDay = now.getDayOfMonth();
        setDateString();
        if (mDatePicker == null) {
            mDatePicker = new DatePickerDialog(mContext);
            mDatePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    mSelectedYear = year;
                    mSelectedMonth = month + 1;
                    mSelectedDay = day;
                    setDateString();

                    reqGetScheduleList();
                }
            });
        }
        mBindingViews.workScheduleFlNext.setOnClickListener(mViewOnClickListener);
        mBindingViews.workScheduleFlPrev.setOnClickListener(mViewOnClickListener);
        mBindingViews.workScheduleTvDate.setOnClickListener(mViewOnClickListener);
    }

    private String getStringFromSelectForDisplay() {
        LocalDate localDate = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay);
        mSelectedDayName = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ko", "KO"));
        return String.format("%d.%d.%d (%s)", mSelectedYear, mSelectedMonth, mSelectedDay, mSelectedDayName);
    }

    private String getStringFromSelectForSearch() {
        return String.format("%04d-%02d-%02d", mSelectedYear, mSelectedMonth, mSelectedDay);
    }

    private void setDateString() {
        mBindingViews.workScheduleTvDate.setText(getStringFromSelectForDisplay());
    }

    private View.OnClickListener mViewOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view.getId() == R.id.work_schedule_fl_next) {
            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).plusDays(1);
            mSelectedYear = now.getYear();
            mSelectedMonth = now.getMonthValue();
            mSelectedDay = now.getDayOfMonth();
            setDateString();
            reqGetScheduleList();
        } else if (view.getId() == R.id.work_schedule_fl_prev) {
            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).minusDays(1);
            mSelectedYear = now.getYear();
            mSelectedMonth = now.getMonthValue();
            mSelectedDay = now.getDayOfMonth();
            setDateString();
            reqGetScheduleList();
        } else if (view.getId() == R.id.work_schedule_tv_date) {
            mDatePicker.show();
        }
    };

    private void initRecycler() {
        mBindingViews.workScheduleSrRefresher.setOnRefreshListener(() -> {
            reqGetScheduleList();
        });

        if (mScheduleList == null) mScheduleList = new ArrayList<>();

        mBindingViews.workScheduleRvRecycler.setHasFixedSize(true);
        mBindingViews.workScheduleRvRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespScheduleInfo> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mScheduleList);
        mBindingViews.workScheduleRvRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mBindingViews.workScheduleRvRecycler.setLayoutManager(layoutManager);

        reqGetScheduleList();
    }

    RecyclerAdapter.IListListener mRecyclerListener = new RecyclerAdapter.IListListener() {
        @Override
        public <T> int onGetItemViewType(int position, @Nullable List<T> itemList) {
            return 0;
        }

        @Override
        public int onReqViewHolderLayout(int viewType) {
            return R.layout.recycler_item_work_schedule;
        }

        @Override
        public <T> void onBindViewHolder(@Nullable RecyclerAdapter.ViewHolder holder, int position, @Nullable List<T> itemList) {
            ViewDataBinding binding = null;
            if (holder != null) binding = holder.getBinding();

            if (binding instanceof RecyclerItemWorkScheduleBinding) {
                RespScheduleInfo data = mScheduleList.get(position);
                RecyclerItemWorkScheduleBinding itemBinding = (RecyclerItemWorkScheduleBinding) binding;

                itemBinding.riWorkScheduleTvCount.setVisibility(View.GONE);

                itemBinding.riWorkScheduleTvType.setText(data.getWorkdivision());
                itemBinding.riWorkScheduleTvTarget.setText(data.getOrgan());
                itemBinding.riWorkScheduleTvReceiver.setText(data.getReceipt());
                itemBinding.riWorkScheduleTvTime.setText(data.getVisittime());
                itemBinding.riWorkScheduleTvOwner.setText(data.getCe());
                itemBinding.riWorkScheduleTvStatus.setText(data.getStatus());
                String type = getPaperType(data.getWorkdivision());
                if (data.getPaperlessFlag() != null && data.getPaperlessFlag().equals("Y")) {
                    if (type == null || type.equals("NOT")) {
                        itemBinding.riWorkScheduleTvPaper.setText("미지원");
                        itemBinding.riWorkScheduleBtnDoc.setText("-");
                        itemBinding.riWorkScheduleBtnSign.setText("-");
                    } else if (type.equals("WORK")){
                        itemBinding.riWorkScheduleTvPaper.setText("작성완료");
                        itemBinding.riWorkScheduleBtnDoc.setText("설치확인서 수정");
                        itemBinding.riWorkScheduleBtnSign.setText("사인");
                    } else if (type.equals("SERVICE")){
                        itemBinding.riWorkScheduleTvPaper.setText("작성완료");
                        itemBinding.riWorkScheduleBtnDoc.setText("서비스확인서 수정");
                        itemBinding.riWorkScheduleBtnSign.setText("사인");
                    }
                } else {
                    if (type == null || type.equals("NOT")) {
                        itemBinding.riWorkScheduleTvPaper.setText("미지원");
                        itemBinding.riWorkScheduleBtnDoc.setText("-");
                        itemBinding.riWorkScheduleBtnSign.setText("-");
                    } else if (type.equals("WORK")){
                        itemBinding.riWorkScheduleTvPaper.setText("미작성");
                        itemBinding.riWorkScheduleBtnDoc.setText("설치확인서 작성");
                        itemBinding.riWorkScheduleBtnSign.setText("-");
                    } else if (type.equals("SERVICE")){
                        itemBinding.riWorkScheduleTvPaper.setText("미작성");
                        itemBinding.riWorkScheduleBtnDoc.setText("서비스확인서 작성");
                        itemBinding.riWorkScheduleBtnSign.setText("-");
                    }
                }

                itemBinding.riWorkScheduleBtnDoc.setTag(position);
                itemBinding.riWorkScheduleBtnSign.setTag(position);
            }
        }

        @Override
        public void onSetListItemsListener(@Nullable RecyclerAdapter.ViewHolder holder) {
            if (holder == null) {
                DebugLog.e(TAG, "holder is null");
                return;
            }
            if (holder.getBinding() instanceof RecyclerItemWorkScheduleBinding){
                RecyclerItemWorkScheduleBinding binding = (RecyclerItemWorkScheduleBinding)holder.getBinding();

                binding.riWorkScheduleBtnDoc.setOnClickListener(mRecyclerOnClickListener);
                binding.riWorkScheduleBtnSign.setOnClickListener(mRecyclerOnClickListener);
            }

        }

        @Override
        public void onViewRecycled(@Nullable RecyclerAdapter.ViewHolder holder) {

        }
    };

    private View.OnClickListener mRecyclerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int)v.getTag();

            RespScheduleInfo data = mScheduleList.get(position);
            String type = getPaperType(data.getWorkdivision());
            DebugLog.e(TAG, "type is : " + type);
            if (type == null) {
                if (data.getWorkdivision() == null) {
                    DebugLog.e(TAG, "work division is null");
                    makeToast("알 수 없는 작업 분류입니다.");
                } else {
                    DebugLog.e(TAG, "work division in first : " + data.getWorkdivision());
                    makeToast("알 수 없는 작업 분류입니다.(" + data.getWorkdivision() + ")");
                }
                return;
            }
            String division = getWorkDivision(data.getWorkdivision());
            DebugLog.e(TAG, "division is : " + division);

            if (v.getId() == R.id.ri_work_schedule_btn_doc) {
                if (type.equals("WORK")) {
                    if (data.getPaperlessFlag() != null && data.getPaperlessFlag().equals("Y")) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, true);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.WORK_PAPER_REGISTER, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, false);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.WORK_PAPER_REGISTER, bundle);
                    }
                } else if (type.equals("SERVICE")) {
                    if (data.getPaperlessFlag() != null && data.getPaperlessFlag().equals("Y")) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, true);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.SERVICE_PAPER_REGISTER, bundle);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, false);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.SERVICE_PAPER_REGISTER, bundle);
                    }
                } else {
                    DebugLog.e(TAG, "work division is null");
                    makeToast("지원하지 않는 작업 분류입니다.(" + data.getWorkdivision() + ")");
                }
            } else if (v.getId() == R.id.ri_work_schedule_btn_sign) {
                if (type.equals("WORK")) {
                    if (data.getPaperlessFlag() != null && data.getPaperlessFlag().equals("Y")) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, true);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.WORK_PAPER_SIGN, bundle);
                    } else {
                        makeToast("문서 작성이 필요합니다.");
                    }
                } else if (type.equals("SERVICE")) {
                    if (data.getPaperlessFlag() != null && data.getPaperlessFlag().equals("Y")) {
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                        bundle.putBoolean(Constants.BundleKey.IS_MODIFY, true);
                        bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                        bundle.putString(Constants.BundleKey.PAPER_CODE, data.getCode());
                        bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                        bundle.putString(Constants.BundleKey.PAPER_WORK_DIVISION, division);
                        bundle.putString(Constants.BundleKey.PAPER_CE_NAME, data.getCe());
                        mListener.onChangeState(Constants.FragmentState.SERVICE_PAPER_SIGN, bundle);
                    } else {
                        makeToast("문서 작성이 필요합니다.");
                    }
                } else {
                    DebugLog.e(TAG, "work division is null");
                    makeToast("지원하지 않는 작업 분류입니다.(" + data.getWorkdivision() + ")");
                }
            }
        }
    };

    private String getPaperType(String division) {
        if (division == null) return null;
        switch (division) {
            case "가매장설치":
            case "가매장철수":
            case "리뉴얼재설치":
            case "공문교체":
            case "신규오픈":
            case "리뉴얼철수":
            case "기타":
            case "법인전환":
            case "신분증감별기":
            case "책상공사":
            case "폐점":
            case "행사설치":
            case "행사철수":
                return "WORK";
            case "유상":
            case "정기점검":
                return "SERVICE";
            case "회선이중화":
            case "무인택배":
                return "NOT";
            default:
                return null;
        }
    }

    private String getWorkDivision(String division) {
        if (division == null) return null;
        switch (division) {
            case "가매장설치":
            case "가매장철수":
            case "기타":
            case "신분증감별기":
            case "책상공사":
            case "행사설치":
            case "행사철수":
                return "기타";
            case "리뉴얼재설치":
            case "리뉴얼철수":
                return "리뉴얼";
            case "공문교체":
                return "공문교체";
            case "신규오픈":
                return "오픈";
            case "법인전환":
                return "법인변경";
            case "폐점":
                return "폐점";
            case "유상":
                return "유상";
            case "정기점검":
                return "정기점검";
            default:
                return null;
        }
    }

    private void reqGetScheduleList() {
        showProgress();
        ReqSchedule param =  new ReqSchedule();
        param.setDate(getStringFromSelectForSearch());

        ReqBase<ReqSchedule> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespSchedule<RespScheduleInfo>> call = RetrofitService.getInstance().getService().reqScheduleList(body);
        call.enqueue(new Callback<RespSchedule<RespScheduleInfo>>() {
            @Override
            public void onResponse(Call<RespSchedule<RespScheduleInfo>> call, Response<RespSchedule<RespScheduleInfo>> response) {
                hideProgress();
                mBindingViews.workScheduleSrRefresher.setRefreshing(false);
                RespSchedule<RespScheduleInfo> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    return;
                }
                switch (responseData.getResultCode()) {
                    case Constants.ServerResultCode.SUCCESS:
                        if (mScheduleList == null) mScheduleList = new ArrayList<>();
                        mScheduleList.clear();
                        mScheduleList.addAll(responseData.getScheduleInfo());
                        if (mBindingViews.workScheduleRvRecycler.getAdapter() != null) {
                            mBindingViews.workScheduleRvRecycler.getAdapter().notifyDataSetChanged();
                        }
                        break;
                    case Constants.ServerResultCode.NOT_FOUND:
                        DebugLog.e(TAG, "store list not found");
                        break;
                    default:
                        DebugLog.e(TAG, "get store list failure");
                        break;
                }
            }

            @Override
            public void onFailure(Call<RespSchedule<RespScheduleInfo>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "get store list failure");
                mBindingViews.workScheduleSrRefresher.setRefreshing(false);
            }
        });
    }
}
