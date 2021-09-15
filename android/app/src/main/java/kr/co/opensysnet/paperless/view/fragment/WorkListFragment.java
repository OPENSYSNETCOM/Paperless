package kr.co.opensysnet.paperless.view.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioGroup;
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
import kr.co.opensysnet.paperless.databinding.FragmentWorkListBinding;
import kr.co.opensysnet.paperless.databinding.RecyclerItemWorkListBinding;
import kr.co.opensysnet.paperless.databinding.ToolbarImageTextBinding;
import kr.co.opensysnet.paperless.model.Constants;
import kr.co.opensysnet.paperless.model.request.ReqBase;
import kr.co.opensysnet.paperless.model.request.ReqInstall;
import kr.co.opensysnet.paperless.model.request.ReqService;
import kr.co.opensysnet.paperless.model.request.ReqWorking;
import kr.co.opensysnet.paperless.model.response.RespBase;
import kr.co.opensysnet.paperless.model.response.RespWorking;
import kr.co.opensysnet.paperless.model.response.RespWorkingInfo;
import kr.co.opensysnet.paperless.server.RetrofitService;
import kr.co.opensysnet.paperless.utils.DebugLog;
import kr.co.opensysnet.paperless.view.activity.MainActivity;
import kr.co.opensysnet.paperless.view.dialog.SimpleDialog;
import kr.co.opensysnet.paperless.view.widget.RecyclerAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkListFragment extends BaseFragment {
    private static final String TAG = "WorkListFragment";

    private ToolbarImageTextBinding mToolbarBindingViews = null;
    private FragmentWorkListBinding mBindingViews = null;

    private long mFinishTime = 0L;

    private List<RespWorkingInfo> mWorkList;

    private DatePickerDialog mDatePicker;
    private int mSelectedYear = 0;
    private int mSelectedMonth = 0;
    private int mSelectedDay = 0;
    private String mSelectedDayName = "";

    private SimpleDialog mSimpleDialog;

    private String mSelectedRadio = null;

    public static WorkListFragment newInstance(Bundle bundle) {
        WorkListFragment fragment = new WorkListFragment();
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
        mBindingViews = DataBindingUtil.inflate(inflater, R.layout.fragment_work_list, container, false);
        return mBindingViews.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DebugLog.e(TAG, "onViewCreated called");
        init();
    }

    private void init() {
        initToolbar();
        initView();
        initRecycler();
        initRadio();
    }

    private void initToolbar() {
        View actionBar = initActionBar(mBindingViews.workListToolbar, R.layout.toolbar_image_text);
        if (actionBar != null){
            mToolbarBindingViews = DataBindingUtil.bind(actionBar);

        }
        if (mToolbarBindingViews != null){
            mToolbarBindingViews.toolbarTvTitle.setText(R.string.toolbar_title_work_list);
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
                    DebugLog.e(TAG, "onDateSet called");
                    mSelectedYear = year;
                    mSelectedMonth = month + 1;
                    mSelectedDay = day;
                    setDateString();

                    reqGetWorkList(null);
                }
            });
        }
        mBindingViews.workListFlNext.setOnClickListener(mViewOnClickListener);
        mBindingViews.workListFlPrev.setOnClickListener(mViewOnClickListener);
        mBindingViews.workListTvDate.setOnClickListener(mViewOnClickListener);
    }

    private String getStringFromSelectForDisplay() {
        LocalDate localDate = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay);
        mSelectedDayName = localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("ko", "KO"));
        return String.format("%d.%d", mSelectedYear, mSelectedMonth);
//        return String.format("%d.%d.%d (%s)", mSelectedYear, mSelectedMonth, mSelectedDay, mSelectedDayName);
    }

    private void setDateString() {
        mBindingViews.workListTvDate.setText(getStringFromSelectForDisplay());
    }

    private View.OnClickListener mViewOnClickListener = view -> {
        hideSoftKeyboard(view);
        if (view.getId() == R.id.work_list_fl_next) {
//            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).plusDays(1);
            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).plusMonths(1);
            mSelectedYear = now.getYear();
            mSelectedMonth = now.getMonthValue();
            mSelectedDay = now.getDayOfMonth();
            setDateString();
            reqGetWorkList(null);
        } else if (view.getId() == R.id.work_list_fl_prev) {
//            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).minusDays(1);
            LocalDate now = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay).minusMonths(1);
            mSelectedYear = now.getYear();
            mSelectedMonth = now.getMonthValue();
            mSelectedDay = now.getDayOfMonth();
            setDateString();
            reqGetWorkList(null);
        } else if (view.getId() == R.id.work_list_tv_date) {
            mDatePicker.show();
        }
    };

    private void initRecycler() {
        mBindingViews.workListSrRefresher.setOnRefreshListener(() -> {
            reqGetWorkList(null);
        });

        if (mWorkList == null) mWorkList = new ArrayList<>();

        mBindingViews.workListRvRecycler.setHasFixedSize(true);
        mBindingViews.workListRvRecycler.setOverScrollMode(View.OVER_SCROLL_NEVER);

        RecyclerAdapter<RespWorkingInfo> adapter = new RecyclerAdapter<>(mContext, mRecyclerListener, mWorkList);
        mBindingViews.workListRvRecycler.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        mBindingViews.workListRvRecycler.setLayoutManager(layoutManager);
    }

    private void initRadio() {
        mBindingViews.workListRbMenuWork.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                mSelectedRadio = "INSTALL";
                DebugLog.e(TAG, "setOnCheckedChangeListener called");
                setTitle();
                reqGetWorkList("INSTALL");
            }
        });
        mBindingViews.workListRbMenuService.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                mSelectedRadio = "AS";
                DebugLog.e(TAG, "setOnCheckedChangeListener called");
                setTitle();
                reqGetWorkList("AS");
            }
        });

        if (mSelectedRadio == null) mBindingViews.workListRbMenuWork.setChecked(true);
    }

    private void setTitle() {
        if (mBindingViews.workListRbMenuWork.isChecked()) {
            mBindingViews.workListTvTitle.setText(R.string.work_list_menu_work);
        } else if (mBindingViews.workListRbMenuService.isChecked()){
            mBindingViews.workListTvTitle.setText(R.string.work_list_menu_service);
        }
    }

    RecyclerAdapter.IListListener mRecyclerListener = new RecyclerAdapter.IListListener() {
        @Override
        public <T> int onGetItemViewType(int position, @Nullable List<T> itemList) {
            return 0;
        }

        @Override
        public int onReqViewHolderLayout(int viewType) {
            return R.layout.recycler_item_work_list;
        }

        @Override
        public <T> void onBindViewHolder(@Nullable RecyclerAdapter.ViewHolder holder, int position, @Nullable List<T> itemList) {
            ViewDataBinding binding = null;
            if (holder != null) binding = holder.getBinding();

            if (binding instanceof RecyclerItemWorkListBinding) {
                RespWorkingInfo data = mWorkList.get(position);
                RecyclerItemWorkListBinding itemBinding = (RecyclerItemWorkListBinding) binding;

                itemBinding.riWorkListName.setText(data.getOrgan());
                String time = getResources().getString(R.string.work_list_working_time);
                if (data.getStarttime() == null) {
                    time += " " + getResources().getString(R.string.work_list_working_time_none);
                } else {
                    if (data.getStarttime() != null) time += " " + data.getStarttime() + " ~ ";
                    if (data.getComtime() != null) time += data.getComtime();
                }
                itemBinding.riWorkListTime.setText(time);
                itemBinding.riWorkListEdit.setTag(position);
                itemBinding.riWorkListDelete.setTag(position);
            }
        }

        @Override
        public void onSetListItemsListener(@Nullable RecyclerAdapter.ViewHolder holder) {
            if (holder == null) {
                DebugLog.e(TAG, "holder is null");
                return;
            }
            if (holder.getBinding() instanceof RecyclerItemWorkListBinding){
                RecyclerItemWorkListBinding binding = (RecyclerItemWorkListBinding)holder.getBinding();

                binding.riWorkListEdit.setOnClickListener(mRecyclerOnClickListener);
                binding.riWorkListDelete.setOnClickListener(mRecyclerOnClickListener);
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
            RespWorkingInfo data = mWorkList.get(position);

            if (v.getId() == R.id.ri_work_list_edit) {
                if (mBindingViews.workListRgMenu.getCheckedRadioButtonId() == R.id.work_list_rb_menu_work) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                    bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                    bundle.putString(Constants.BundleKey.PAPER_ORGAN, data.getOrgan());
                    mListener.onChangeState(Constants.FragmentState.WORK_PAPER_REGISTER, bundle);
                } else if (mBindingViews.workListRgMenu.getCheckedRadioButtonId() == R.id.work_list_rb_menu_service) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(Constants.IntentExtra.EXTRA_KEY_ADD_BACK_STACK, true);
                    bundle.putBoolean(Constants.BundleKey.IS_MODIFY, true);
                    bundle.putInt(Constants.BundleKey.PAPER_SEQ, data.getSeq());
                    mListener.onChangeState(Constants.FragmentState.SERVICE_PAPER_REGISTER, bundle);
                } else {
                    DebugLog.e(TAG, "Why??");
                }
            } else if (v.getId() == R.id.ri_work_list_delete) {
                if (mBindingViews.workListRgMenu.getCheckedRadioButtonId() == R.id.work_list_rb_menu_work) {
                    if (mSimpleDialog == null) mSimpleDialog = new SimpleDialog(mContext);
                    mSimpleDialog.setTitle("삭제 확인");
                    mSimpleDialog.setMessage(getResources().getString(R.string.work_list_working_delete_check_work, data.getOrgan()));
                    mSimpleDialog.setListener(new SimpleDialog.SimpleDialogListener() {
                        @Override
                        public void onConfirmListener() {
                            mSimpleDialog.dismiss();
                            reqDeleteWorkPaper(data.getSeq());
                        }

                        @Override
                        public void onCancelListener() {
                            mSimpleDialog.dismiss();
                        }
                    });
                    mSimpleDialog.show();
                } else if (mBindingViews.workListRgMenu.getCheckedRadioButtonId() == R.id.work_list_rb_menu_service) {
                    if (mSimpleDialog == null) mSimpleDialog = new SimpleDialog(mContext);
                    mSimpleDialog.setTitle("삭제 확인");
                    mSimpleDialog.setMessage(getResources().getString(R.string.work_list_working_delete_check_service, data.getOrgan()));
                    mSimpleDialog.setListener(new SimpleDialog.SimpleDialogListener() {
                        @Override
                        public void onConfirmListener() {
                            mSimpleDialog.dismiss();
                            reqDeleteServicePaper(data.getSeq());
                        }

                        @Override
                        public void onCancelListener() {
                            mSimpleDialog.dismiss();
                        }
                    });
                    mSimpleDialog.show();
                } else {
                    DebugLog.e(TAG, "Why??");
                }
            }
        }
    };

    private void reqGetWorkList(String type) {
        showProgress();
        ReqWorking param =  new ReqWorking();
        LocalDate date = LocalDate.of(mSelectedYear, mSelectedMonth, mSelectedDay);
        param.setStartdate(String.format("%04d-%02d-%02d", date.getYear(), date.getMonthValue(), 1));
        param.setEnddate(String.format("%04d-%02d-%02d", date.getYear(), date.getMonthValue(), date.lengthOfMonth()));
        if (type == null) {
            if (mBindingViews.workListRbMenuWork.isChecked()) {
                param.setType("INSTALL");
            } else if (mBindingViews.workListRbMenuService.isChecked()) {
                param.setType("AS");
            } else {
                hideProgress();
                DebugLog.e(TAG, "Why??");
                return;
            }
        } else {
            param.setType(type);
        }
//        param.setStartdate(String.format("%04d-%02d-%02d", mSelectedYear, mSelectedMonth, mSelectedDay));
//        param.setEnddate(String.format("%04d-%02d-%02d", mSelectedYear, mSelectedMonth, mSelectedDay));

        ReqBase<ReqWorking> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespWorking<RespWorkingInfo>> call = RetrofitService.getInstance().getService().reqWorkingList(body);
        call.enqueue(new Callback<RespWorking<RespWorkingInfo>>() {
            @Override
            public void onResponse(Call<RespWorking<RespWorkingInfo>> call, Response<RespWorking<RespWorkingInfo>> response) {
                hideProgress();
                mBindingViews.workListSrRefresher.setRefreshing(false);
                RespWorking<RespWorkingInfo> responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    return;
                }
                mWorkList.clear();
                switch (responseData.getResultCode()) {
                    case Constants.ServerResultCode.SUCCESS:
                        if (mWorkList == null) mWorkList = new ArrayList<>();
                        mWorkList.addAll(responseData.getWorkingInfo());
                        break;
                    case Constants.ServerResultCode.NOT_FOUND:
                        DebugLog.e(TAG, "work list not found");
                        makeToast("문서 목록이 없습니다.");
                        break;
                    default:
                        DebugLog.e(TAG, "get store list failure");
                        makeToast("문서 목록 조회에 실패했습니다.");
                        break;
                }
                if (mBindingViews.workListRvRecycler.getAdapter() != null) {
                    mBindingViews.workListRvRecycler.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<RespWorking<RespWorkingInfo>> call, Throwable t) {
                hideProgress();
                DebugLog.e(TAG, "get store list failure");
                makeToast("문서 목록 조회에 실패했습니다.");
                mBindingViews.workListSrRefresher.setRefreshing(false);
            }
        });
    }

    private void reqDeleteWorkPaper(int seq) {
        showProgress();
        ReqInstall param = new ReqInstall();
        param.setSeq(seq);

        ReqBase<ReqInstall> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespBase> call = RetrofitService.getInstance().getService().reqInstallDelete(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("문서 삭제 완료.");
                    reqGetWorkList(null);
                } else {
                    makeToast("문서 삭제에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                makeToast("문서 삭제에 실패했습니다.");
            }
        });
    }

    private void reqDeleteServicePaper(int seq) {
        showProgress();
        ReqService param = new ReqService();
        param.setSeq(seq);

        ReqBase<ReqService> body = new ReqBase<>();
        body.setReq_param(param);

        Call<RespBase> call = RetrofitService.getInstance().getService().reqServiceDelete(body);
        call.enqueue(new Callback<RespBase>() {
            @Override
            public void onResponse(Call<RespBase> call, Response<RespBase> response) {
                hideProgress();
                RespBase responseData = response.body();
                if (responseData == null) {
                    DebugLog.e(TAG, "response data is null");
                    return;
                }
                if (responseData.getResultCode() == Constants.ServerResultCode.SUCCESS) {
                    makeToast("문서 삭제 완료.");
                    reqGetWorkList(null);
                } else {
                    makeToast("문서 삭제에 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<RespBase> call, Throwable t) {
                hideProgress();
                makeToast("문서 삭제에 실패했습니다.");
            }
        });
    }
}
