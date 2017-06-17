package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.coolwin.XYT.Entity.Login;
import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.CompleteUserInfoBinding;
import com.coolwin.XYT.interfaceview.UIEditProfile;
import com.coolwin.XYT.presenter.EditProfilePresenter;
import com.coolwin.XYT.util.GalleryFinalHelper;
import com.coolwin.XYT.util.GetDataUtil;
import com.coolwin.XYT.util.UIUtil;
import com.facebook.fresco.helper.Phoenix;

import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;



public class EditProfileActivity extends BaseActivity<EditProfilePresenter> implements UIEditProfile{

	private enum Write{
		WRITE,
		WRITE_NICKNAME,
		WRITE_SIGN,
		WRITE_COMPANYWEBSITE,
		WRITE_INDUSTRY,
		WRITE_COMPANY,
		WRITE_COMPANYADDRESS,
		WRITE_JOB,
		WRITE_PROVIDE,
		WRITE_DEMAND,
		WRITE_TELEPHONE,
		WRITE_AREACODE,
	}
	CompleteUserInfoBinding binding;
	Login mlogin;
	String imagepath;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.complete_user_info);
		binding.setBehavior(this);
		binding.titleLayout.setBehavior(this);
		binding.titleLayout.title.setText("个人信息");
		ImageView leftbtn = binding.titleLayout.leftIcon;
		leftbtn.setImageResource(R.drawable.back_icon);
		binding.titleLayout.rightTextBtn.setText("保存");
		mlogin = GetDataUtil.getLogin(context);
		binding.setLogin(mlogin);
		binding.sex.setText(mlogin.gender==2?"男":"女");
		Phoenix.with(binding.newHeaderIcon).load(mlogin.headsmall);
	}
	@Override
	public void right_text(View view) {
		mPresenter.editProfile(mlogin,imagepath);
	}

	public void openSign(View view){
		openWindow(mlogin.sign,"个性签名",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openDiqu(View view){
		startActivityForResult(new Intent(context,SelectAreaCodeActivity.class), Write.WRITE_AREACODE.ordinal());
	}
	public void openCompanywebsite(View view){
		openWindow(mlogin.companywebsite,"公司主页",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openIndustry(View view){
		openWindow(mlogin.industry,"行业",WriteNameActivity.SINGLE,false, Write.WRITE_SIGN);
	}
	public void openCompany(View view){
		openWindow(mlogin.company,"公司",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openCompanyaddress(View view){
		openWindow(mlogin.companyaddress,"公司地址",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openJob(View view){
		openWindow(mlogin.job,"职位",WriteNameActivity.SINGLE,false, Write.WRITE_SIGN);
	}
	public void openTelephone(View view){
		openWindow(mlogin.telephone,"电话",WriteNameActivity.SINGLE,false, Write.WRITE_SIGN,EditorInfo.TYPE_CLASS_NUMBER);
	}
	public void openProvide(View view){
		openWindow(mlogin.provide,"可供",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openDemand(View view){
		openWindow(mlogin.demand,"需求",WriteNameActivity.MULTI,false, Write.WRITE_SIGN);
	}
	public void openGallerySingle(View view){
		GalleryFinalHelper.openGallerySingle(0, true, true, new GalleryFinal.OnHanlderResultCallback() {
			@Override
			public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
				if (resultList.size()>0) {
					Phoenix.with(binding.newHeaderIcon).load(resultList.get(0).getPhotoPath());
					imagepath = resultList.get(0).getPhotoPath();
				}
			}

			@Override
			public void onHanlderFailure(int requestCode, String errorMsg) {
				UIUtil.showMessage(context,"相册加载异常");
			}
		});
	}
	public void openWindow(String name,String title,String type,boolean verify,Write backcode){
		openWindow(name,title,type,verify,backcode, EditorInfo.TYPE_CLASS_TEXT);
	}
	public void openWindow(String name,String title,String type,boolean verify,Write backcode,int inputtype){
		Intent intent = new Intent();
		intent.putExtra(WriteNameActivity.DATANAME,name);
		intent.putExtra(WriteNameActivity.DATATITLE,title);
		intent.putExtra(WriteNameActivity.DATATYPE,type);
		intent.putExtra(WriteNameActivity.DATAVERIFY,verify);
		intent.putExtra(WriteNameActivity.DATAINPUTTYPE,inputtype);
		intent.setClass(context, WriteNameActivity.class);
		startActivityForResult(intent,backcode.ordinal());
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("onActivityResult","resultCode="+(resultCode == RESULT_OK));
		if(resultCode == RESULT_OK){
			Write write = Write.values()[requestCode];
			Log.e("onActivityResult","write="+(write == Write.WRITE_AREACODE));
			switch (write){
				case WRITE_SIGN:
					mlogin.sign = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_COMPANYWEBSITE:
					mlogin.sign =  data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_INDUSTRY:
					mlogin.industry =data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_COMPANY:
					mlogin.company = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_COMPANYADDRESS:
					mlogin.companyaddress = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_JOB:
					mlogin.job = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_PROVIDE:
					mlogin.provide = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_DEMAND:
					mlogin.demand = data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_TELEPHONE:
					mlogin.telephone =  data.getStringExtra(WriteNameActivity.BACKNAME);
					break;
				case WRITE_AREACODE:
					mlogin.provinceid =  data.getStringExtra(SelectAreaCodeActivity.DATAPROVINCE);
					mlogin.cityid =  data.getStringExtra(SelectAreaCodeActivity.DATACITY);
					break;
			}
			binding.setLogin(mlogin);
		}
	}
	@Override
	public void showLoading() {
		super.showLoading("提交中...");
	}
}
