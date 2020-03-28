package moe.div.moeplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.MissingFormatArgumentException;

import moe.div.moeplayer.R;
import moe.div.moeplayer.activity.PlayActivity;
import moe.div.moeplayer.adapter.FolderListAdapter;
import moe.div.moeplayer.adapter.VideoListAdapter;
import moe.div.moeplayer.bean.FolderItem;
import moe.hmo.mobase.fragment.BaseFragment;

/**
 * @author 林墨
 * @time 20/2/2  13:38
 * @desc 浏览器列表页面
 */
public class FolderListFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private FolderListAdapter mAdapter;

    private File mRootPathFile;           // 根路径文件对象
//    private File mPrevPathFile;            // 上一级目录文件对象，处于根目录是为 null
//    private File mCurrentPathFile;        // 当前路径文件对象

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorer_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        // 初始化RV
        mAdapter = new FolderListAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        // 初始化当前路径为SD卡根路径
        mRootPathFile = Environment.getExternalStorageDirectory();
        // 加载数据
        loadData(mRootPathFile);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        // 列表item点击事件
        mAdapter.setOnItemClickListener((parent, view, position, id) -> {
            // 取出item
            FolderItem item = mAdapter.getItem(position);
            // 判断点击的是什么
            String type = item.getType();
            switch (type) {
                case "Prev" :       // 返回上一级
                case "Path" :       // 文件夹
                    loadData(item.getFile());
                    break;
                case "Video" :
                    Intent intent = new Intent(getActivity(), PlayActivity.class);
                    intent.putExtra(PlayActivity.Tag_Link, item.getFile().getAbsolutePath());
                    intent.putExtra(PlayActivity.Tag_Title, item.getTitle());
                    getActivity().startActivity(intent);
                    break;
                default:
                    showTwiceConfirmDialog("提示", "这个文件可能不是视频文件，确认要尝试播放吗？", (dialog, which) -> {
                        Intent intent2 = new Intent(getActivity(), PlayActivity.class);
                        intent2.putExtra(PlayActivity.Tag_Link, item.getFile().getAbsolutePath());
                        intent2.putExtra(PlayActivity.Tag_Title, item.getTitle());
                        getActivity().startActivity(intent2);
                        dialog.cancel();
                    }, null);
            }

            // 判断点击的是文件夹还是文件
        });
    }

    // 加载数据
    private void loadData(File path){
        // 准备一个空集合
        List<FolderItem> list = new ArrayList<>();
        // 如果需要加载的路径不等于根路径，就添加一个返回上一层
        if(!mRootPathFile.getAbsolutePath().equals(path.getAbsolutePath())){
            FolderItem prev = new FolderItem();
            prev.setFile(path.getParentFile());
            prev.setTitle("返回上一级");
            prev.setType("Prev");

            list.add(prev);
        }
        // 获取当前路径下的所有文件对象
        File[] files = path.listFiles();
        if(files == null){
            return;
        }
        // 进行排序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                return o1.getName().compareTo(o2.getName());
            }
        });
        // 遍历转换并添加到集合中
        for (File file : fileList) {
            // 准备容器对象
            FolderItem item = new FolderItem();
            // 设置信息
            item.setFile(file);
            item.setTitle(file.getName());
            if(file.isDirectory()){
                item.setType("Path");
            }else {
                // 判断文件后缀，是否是视频文件
                String fileName = file.getName().toLowerCase();
                if(fileName.endsWith(".mp4")
                    || fileName.endsWith(".avi")
                    || fileName.endsWith(".mkv")
                    || fileName.endsWith(".rmvb")
                    || fileName.endsWith(".flv")
                    || fileName.endsWith(".m3u8")
                    || fileName.endsWith(".3pg")
                    || fileName.endsWith(".mov")
                    || fileName.endsWith(".rm")){
                    // 视频文件
                    item.setType("Video");
                }else {
                    item.setType("Unknown");
                }
            }
            // 添加到集合
            list.add(item);
        }
        // 设置给适配器并更新
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }
}
