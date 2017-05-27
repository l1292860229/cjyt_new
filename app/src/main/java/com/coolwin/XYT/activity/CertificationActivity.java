package com.coolwin.XYT.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ab.fragment.AbSampleDialogFragment;
import com.ab.util.AbDialogUtil;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.CertificationPageBinding;
import com.coolwin.XYT.databinding.DialogPictureBinding;
import com.coolwin.XYT.interfaceview.UICertification;
import com.coolwin.XYT.presenter.CertificationPresenter;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.UIUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 关于我们页面
 * @author dongli
 *
 */
public class CertificationActivity extends BaseActivity<CertificationPresenter> implements UICertification {
	public static final int REQUEST_CODE_CAMERA = 1;
	public static final int REQUEST_CODE_GALLERY = 2;
	CertificationPageBinding binding;
	AbSampleDialogFragment abSampleDialogFragment;
	public SimpleDraweeView clickImageView;
	public String businesscard,signboard,idcard;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding =  DataBindingUtil.setContentView(this, R.layout.certification_page);
		binding.titleLayout.setBehavior(this);
		binding.setBehavior(this);
		initComponent();
	}

	/**
	 * 实例化控件
	 */
	private void initComponent(){
		binding.titleLayout.title.setText("个人认证");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
	}

	public void uploadUserAttext(View view){
		mPresenter.uploadUserAttext(businesscard,signboard,idcard);
	}
	/**
	 * 显示认证页面
	 * @param view
	 */
	public void openRenZhen(View view){
		binding.titleLayout.title.setText("认证信息");
		binding.layout1.setVisibility(View.GONE);
		binding.layout2.setVisibility(View.VISIBLE);
	}

	/**
	 * 选择上传图片
	 * @param view
	 */
	public void openCard(View view){
		DialogPictureBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(this),R.layout.dialog_picture,null,false);
		binding.setBehavior(this);
		if(view instanceof  SimpleDraweeView){
			clickImageView = (SimpleDraweeView) view;
		}else {
			clickImageView = new SimpleDraweeView(context);
		}
		switch (view.getId()){
			case R.id.mingpian:
				//binding.picture.setImageResource(R.drawable.mingpian_pic);
				break;
			case R.id.gongpai:
				binding.picture.setImageResource(R.drawable.gongpai);
				break;
			case R.id.shenfenzheng:
				binding.picture.setImageResource(R.drawable.shenfenzheng);
				break;
		}
		abSampleDialogFragment = AbDialogUtil.showDialog(binding.getRoot());
	}

	/**
	 * 取消弹出框
	 * @param view
	 */
	public void cannelDialog(View view){
		abSampleDialogFragment.dismiss();
	}

	/**
	 * 打开相机
	 * @param view
	 */
	public void openCamera(View view){
		GalleryFinalHelper.openCamera(REQUEST_CODE_GALLERY,false, mOnHanlderResultCallback);
		abSampleDialogFragment.dismiss();
	}

	/**
	 * 打开相册
	 * @param view
	 */
	public void openGallery(View view){
		GalleryFinalHelper.openGallerySingle(REQUEST_CODE_CAMERA,false,false,mOnHanlderResultCallback);
		abSampleDialogFragment.dismiss();
	}
	//图片的回调涵数
	private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback(){

		@Override
		public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
			if(resultList.size()>0){
				Phoenix.with(clickImageView).load(resultList.get(0).getPhotoPath());
				switch (clickImageView.getId()){
					case R.id.mingpian:
						businesscard = resultList.get(0).getPhotoPath();
						break;
					case R.id.gongpai:
						signboard = resultList.get(0).getPhotoPath();
						break;
					case R.id.shenfenzheng:
						idcard = resultList.get(0).getPhotoPath();
						break;
				}
			}
		}

		@Override
		public void onHanlderFailure(int requestCode, String errorMsg) {
			UIUtil.showMessage(context,"加载图片失败,原因:"+errorMsg);
		}
	};

	@Override
	public void uploadsuccess() {
		this.finish();
		UIUtil.showMessage(context,"上传成功等待审核");
	}

	@Override
	public void showLoading() {
		super.showLoading("正在上传...");
	}
}
