package moe.hmo.mobase.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import moe.hmo.mobase.R;
import moe.hmo.mobase.weiget.LoadingDialog;


/**
 * @author 林墨
 * Fragment 基类，通用方法写在这里
 */
public abstract class BaseFragment extends Fragment {

	protected LoadingDialog mProgressDialog;
	protected Toast mSucceedToast;
	protected TextView mSucceedToastHint;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return initView(inflater,container,savedInstanceState);
	}
	
	/**
	 * 初始化布局
	 * @param inflater 				打气筒
	 * @param container				父容器
	 * @param savedInstanceState	保存的状态实例
	 * @return 需要显示的布局View
	 */
	protected abstract View initView(LayoutInflater inflater, ViewGroup container,
									 Bundle savedInstanceState);


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initEvent();
	}

	/**
	 * 初始化数据
	 */
	protected void initData() {
		// 空实现
	}
	
	/**
	 * 初始化事件
	 */
	protected void initEvent() {
		// 空实现
	}

	/**
	 * 显示吐司
	 * @param text
	 */
	public void showToast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 弹出错误提示对话框
	 * @param title			标题
	 * @param message		消息
	 * @param positive		确认按钮文本
	 * @param confirmCall	确认回调事件
	 */
	public void showErrorDialog(String title, String message, String positive,
								DialogInterface.OnClickListener confirmCall){
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
			.setTitle(title)
			.setMessage(message)
			.setPositiveButton(positive, confirmCall)
			.setCancelable(false)
			.create();
		alertDialog.show();
	}

	public void showErrorDialog(String message){
		showErrorDialog("提示", message, "确定", null);
	}

	/**
	 * 弹出二次确认对话框
	 * @param title         标题
	 * @param message       信息
	 * @param confirmCall   确认回调
	 * @param cancelCall    取消回调
	 */
	public void showTwiceConfirmDialog(String title, String message,
									   DialogInterface.OnClickListener confirmCall,
									   DialogInterface.OnClickListener cancelCall){
		AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton("确定", confirmCall)
				.setNegativeButton("取消", cancelCall)
				.create();
		alertDialog.show();

	}

	/**
	 * 显示等待对话框
	 */
	public void showProgressDialog(){
		if(mProgressDialog != null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
		// 初始化等待对话框
		mProgressDialog = new LoadingDialog(getActivity());
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}

	/**
	 * 隐藏等待对话框
	 */
	public void hideProgressDialog(){
		if(mProgressDialog != null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}

	//// 显示成功 Toast
	//public void showSucceedToast(){
	//	showSucceedToast(null);
	//}

	//// 显示成功 Toast
	//public void showSucceedToast(String hint){
	//	mSucceedToast = Toast.makeText(UIUtils.getContext(), "Normarl toast", Toast.LENGTH_SHORT);
	//	mSucceedToast.setGravity(Gravity.CENTER, 0, 0);
	//	LayoutInflater inflater = LayoutInflater.from(getActivity());
	//	View view = inflater.inflate(R.layout.toast_vote_succeed_layout, null);
	//	mSucceedToastHint = (TextView) view.findViewById(R.id.hint_text);
	//	mSucceedToast.setView(view);
	//	if(hint != null){
	//		mSucceedToastHint.setText(hint);
	//	}
	//	mSucceedToast.show();
	//}

	/**
	 * 统一的成功吐司
	 * @param text
	 */
	public void showMoSucceedToast(String text){
		showMoToast(R.mipmap.toast_success, text);
	}

	/**
	 * 统一的失败吐司
	 * @param text
	 */
	public void showMoErrorToast(String text){
		showMoToast(R.mipmap.toast_error, text);
	}

	// 显示提示吐司
	public void showMoToast(@DrawableRes int resId, String hint){
		mSucceedToast = Toast.makeText(getActivity().getApplicationContext(), "Normarl toast", Toast.LENGTH_SHORT);
		mSucceedToast.setGravity(Gravity.CENTER, 0, 0);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.toast_info_layout, null);
		ImageView hintImage = (ImageView) view.findViewById(R.id.hint_image);
		TextView hintText = (TextView) view.findViewById(R.id.hint_text);
		mSucceedToast.setView(view);
		hintImage.setImageResource(resId);
		if(hint != null){
			hintText.setText(hint);
		}
		mSucceedToast.show();
	}

}
