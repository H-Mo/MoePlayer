package moe.div.moeplayer.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moe.div.moeplayer.App;
import moe.div.moeplayer.R;
import moe.div.moeplayer.activity.MainActivity;
import moe.div.moeplayer.activity.PlayActivity;
import moe.div.moeplayer.adapter.VideoListAdapter;
import moe.div.moeplayer.bean.VideoItem;
import moe.div.moeplayer.constant.AppConst;
import moe.div.moeplayer.event.ChangePageEvent;
import moe.div.moeplayer.utils.SDCardFileUtil;
import moe.hmo.mobase.fragment.BaseFragment;

/**
 * @author 林墨
 * @time 20/2/2  13:22
 * @desc 数据列表页面
 */
public class DataListFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate {

    private LinearLayout main_ll;
    private RelativeLayout empty_rl;
    private LinearLayout empty_ll;

    private RelativeLayout progress_rl;
    private LinearLayout progress_ll;
    private RelativeLayout error_rl;
    private LinearLayout error_ll;

    private BGARefreshLayout refresh_bga;
    private RecyclerView video_rv;

    private int mPage = 1;
    private RxPermissions rxPermissions;
    private VideoListAdapter mAdapter;


    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_data_list, container, false);
        main_ll = view.findViewById(R.id.main_ll);
        empty_rl = view.findViewById(R.id.empty_rl);
        empty_ll = view.findViewById(R.id.empty_ll);

        progress_rl = view.findViewById(R.id.progress_rl);
        progress_ll = view.findViewById(R.id.progress_ll);
        error_rl = view.findViewById(R.id.error_rl);
        error_ll = view.findViewById(R.id.error_ll);

        refresh_bga = view.findViewById(R.id.refresh_bga);
        video_rv = view.findViewById(R.id.video_rv);

        // 初始化Rv
        mAdapter = new VideoListAdapter();
        video_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        video_rv.setAdapter(mAdapter);
        // 初始化 BGARefreshLayout
        initRefreshLayout();
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        rxPermissions = new RxPermissions(this);
        // 加载数据
        loadData(true);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        View.OnClickListener loadDataEvent = v -> {
            // 请求权限
            rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        // 获得权限
                        empty_ll.setVisibility(View.GONE);
                        error_ll.setVisibility(View.GONE);
                        // 加载数据
                        loadData(true);
                    } else {
                        // 没有权限
                        showErrorDialog("能赐予我权限吗？");
                    }
                });
        };
        empty_ll.setOnClickListener(loadDataEvent);
        error_ll.setOnClickListener(loadDataEvent);
        // 子项视频点击事件
        mAdapter.setOnItemClickListener((parent, view, position, id) -> {
            // 跳转播放界面
            VideoItem item = mAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PlayActivity.class);
            intent.putExtra(PlayActivity.Tag_Link, item.getFilePath());
            intent.putExtra(PlayActivity.Tag_Title, item.getFileName());
            Pair pair = new Pair<>(view, ViewCompat.getTransitionName(view));
            ActivityOptionsCompat transitionActivityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), pair);
            startActivity(intent);
        });
    }

    // 初始化刷新控件
    private void initRefreshLayout() {
        // 为BGARefreshLayout 设置代理
        refresh_bga.setDelegate(this);
        // 设置下拉刷新和上拉加载更多的风格     参数1：应用程序上下文，参数2：是否具有上拉加载更多功能
        BGAMoocStyleRefreshViewHolder refreshViewHolder = new BGAMoocStyleRefreshViewHolder(
            getActivity(),
            true
        );
        refreshViewHolder.setOriginalImage(R.mipmap.logo);
        refreshViewHolder.setUltimateColor(R.color.colorPrimary);

        // 设置下拉刷新和上拉加载更多的风格
        refresh_bga.setRefreshViewHolder(refreshViewHolder);

        // 为了增加下拉刷新头部和加载更多的通用性，提供了以下可选配置选项  -------------START
        // 设置正在加载更多时不显示加载更多控件
        // mRefreshLayout.setIsShowLoadingMoreView(false);
        // 设置正在加载更多时的文本
        //        refreshViewHolder.setLoadingMoreText("正在加载更多...");
        // 设置整个加载更多控件的背景颜色资源 id
        //        refreshViewHolder.setLoadMoreBackgroundColorRes(R.color.colorWhite);
        // 设置整个加载更多控件的背景 drawable 资源 id
        //        refreshViewHolder.setLoadMoreBackgroundDrawableRes(loadMoreBackgroundDrawableRes);
        // 设置下拉刷新控件的背景颜色资源 id
        //        refreshViewHolder.setRefreshViewBackgroundColorRes(refreshViewBackgroundColorRes);
        // 设置下拉刷新控件的背景 drawable 资源 id
        //        refreshViewHolder.setRefreshViewBackgroundDrawableRes(refreshViewBackgroundDrawableRes);
        // 设置自定义头部视图（也可以不用设置）     参数1：自定义头部视图（例如广告位）， 参数2：上拉加载更多是否可用
        //        mBulletinListRefresh.setCustomHeaderView(mBanner, false);
        // 可选配置  -------------END
    }

    private void loadData(boolean isRefresh){
        // 判断是否是刷新
        if(isRefresh){
            // 重置页码
            mPage = 1;
        }else {
            mPage ++;
        }
        Observable.create((ObservableOnSubscribe<List<VideoItem>>) emitter -> {
            List<VideoItem> videoList = SDCardFileUtil.getVideos(
                getActivity(),
                mPage,
                AppConst.Video_List_Limit
            );
            emitter.onNext(videoList);
            emitter.onComplete();
        }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<List<VideoItem>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(List<VideoItem> videoItems) {
                    refresh_bga.endRefreshing();
                    refresh_bga.endLoadingMore();
                    if(isRefresh){
                        // 下拉刷新
                        progress_rl.setVisibility(View.GONE);
                        // 如果没有数据
                        if(videoItems == null || videoItems.size() == 0){
                            empty_rl.setVisibility(View.VISIBLE);
                            return;
                        }
                        // 设置给适配器
                        refresh_bga.setVisibility(View.VISIBLE);
                        mAdapter.setData(videoItems);
                    }else {
                        // 加载更多
                        if(videoItems == null || videoItems.size() == 0){
                            return;
                        }
                        mAdapter.addData(videoItems);
                    }
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    // 出错了
                    refresh_bga.endRefreshing();
                    refresh_bga.endLoadingMore();
                    progress_rl.setVisibility(View.GONE);
                    error_rl.setVisibility(View.VISIBLE);
                }

                @Override
                public void onComplete() {

                }
            });
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
       App.getHandler().postDelayed(() -> loadData(true), 1000);
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        // 没有更多时,关闭加载更多
        if(mAdapter.isFooterNoMoreData()){
            return false;
        }
        App.getHandler().postDelayed(() -> loadData(false), 100);
        return true;
    }
}
