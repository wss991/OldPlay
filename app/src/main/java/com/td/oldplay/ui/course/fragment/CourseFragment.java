package com.td.oldplay.ui.course.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.td.oldplay.R;
import com.td.oldplay.base.BaseFragment;
import com.td.oldplay.base.EventMessage;
import com.td.oldplay.base.adapter.recyclerview.MultiItemTypeAdapter;
import com.td.oldplay.base.adapter.recyclerview.wrapper.LoadMoreWrapper;
import com.td.oldplay.bean.CourseBean;
import com.td.oldplay.bean.CourseTypeBean;
import com.td.oldplay.contants.MContants;
import com.td.oldplay.http.HttpManager;
import com.td.oldplay.http.callback.OnResultCallBack;
import com.td.oldplay.http.subscriber.HttpSubscriber;
import com.td.oldplay.ui.course.activity.TeacherDetailActivity;
import com.td.oldplay.ui.course.adapter.CourserAdapter;
import com.td.oldplay.ui.window.CustomDialog;
import com.td.oldplay.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CourseFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoadMoreWrapper.OnLoadMoreListener,
        CustomDialog.DialogClick {

    @BindView(R.id.swipeToLoadLayout)
    SwipeRefreshLayout swipeToLoadLayout;
    Unbinder unbinder;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    int page = 1;
    private List<CourseBean> datas;
    private LoadMoreWrapper adapter;
    private CourserAdapter courserAdapter;
    private CustomDialog customDialog;
    private String id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course, container, false);
        unbinder = ButterKnife.bind(this, view);
        id = mActivity.getIntent().getStringExtra("id");
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected void init(View view) {
        datas = new ArrayList<>();
        datas = new ArrayList<>();

        swipeToLoadLayout.setOnRefreshListener(this);
        customDialog = new CustomDialog(mActivity);
        customDialog.setTitleVisible(View.GONE);
        customDialog.setDialogClick(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(mActivity));
        courserAdapter = new CourserAdapter(mActivity, R.layout.item_courese, datas);
        adapter = new LoadMoreWrapper(courserAdapter);
        //adapter.setLoadMoreView(R.layout.default_loading);
        adapter.setOnLoadMoreListener(this);
        swipeTarget.setAdapter(adapter);
        courserAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ((TeacherDetailActivity) mActivity).currentBean = datas.get(position);
                customDialog.setContent("支付" + datas.get(position).price + "元购买课程");
                customDialog.show();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });

        getData();

    }

    private void getData() {
        HttpManager.getInstance().getcoursesInTeacher(page, id, new HttpSubscriber<List<CourseBean>>(new OnResultCallBack<List<CourseBean>>() {


            @Override
            public void onSuccess(List<CourseBean> courseBeen) {
                swipeToLoadLayout.setRefreshing(false);
                if (courseBeen != null && courseBeen.size() > 0) {
                    if (page == 1) {
                        datas.clear();
                        if (courseBeen.size() >= MContants.PAGENUM) {
                            adapter.setLoadMoreView(R.layout.default_loading);
                        }

                    }
                    datas.addAll(courseBeen);
                } else {
                    if (page > 1) {
                        adapter.setLoadMoreView(0);
                        ToastUtil.show("没有更多数据了");
                    }
                }
                adapter.notifyDataSetChanged();
                if (page == 1 && datas.size() > 0) {
                    ((TeacherDetailActivity) mActivity).currentBean = datas.get(0);
                    EventBus.getDefault().post(new EventMessage("changeCourse"));
                }


            }

            @Override
            public void onError(int code, String errorMsg) {
                swipeToLoadLayout.setRefreshing(false);
                ToastUtil.show(errorMsg);
            }

            @Override
            public void onSubscribe(Disposable d) {
                addDisposable(d);
            }
        }));
    }

    @Override
    public void onRefresh() {
        page = 1;
        getData();
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        getData();
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onOk() {
        EventBus.getDefault().post(new EventMessage("changeCourse"));
    }


}
