package moe.div.moeplayer.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * @author H-Mo
 * @time 17/2/16  16:40
 * @desc Fragment工具类
 */
public class FragmentUtils {

    /**
     * 将指定的容器替换为 Fragment
     * @param layoutId  容器ID
     * @param fragment  碎片
     * @param activity  BaseActivity 的子类
     */
    public static void replaceFragment(int layoutId, Fragment fragment, AppCompatActivity activity){
        // 获取碎片管理器
        FragmentManager manager = activity.getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        // 替换容器
        transaction.replace(layoutId,fragment);
        // 提交事务
        transaction.commit();
    }



    /**
     * 将指定的容器替换为 Fragment,不会抛出异常
     * @param layoutId  容器ID
     * @param fragment  碎片
     * @param activity  BaseActivity 的子类
     */
    public static void replaceFragmentAllowingStateLoss(int layoutId, Fragment fragment, AppCompatActivity activity){
        // 获取碎片管理器
        FragmentManager manager = activity.getSupportFragmentManager();
        // 开启事务
        FragmentTransaction transaction = manager.beginTransaction();
        // 替换容器
        transaction.replace(layoutId,fragment);
        // 提交事务
        transaction.commitAllowingStateLoss();
    }
}
