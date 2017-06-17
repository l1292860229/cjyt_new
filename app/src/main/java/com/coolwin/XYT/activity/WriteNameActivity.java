package com.coolwin.XYT.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolwin.XYT.R;
import com.coolwin.XYT.databinding.WriteNameBinding;
import com.coolwin.XYT.interfaceview.UIWriteName;
import com.coolwin.XYT.presenter.WriteNamePresenter;
import com.coolwin.XYT.util.StringUtil;
import com.coolwin.XYT.util.UIUtil;

/**
 * Created by Administrator on 2017/1/24.
 */

public class WriteNameActivity extends BaseActivity<WriteNamePresenter>  implements UIWriteName {
    public static final String SINGLE = "single";
    public static final String MULTI = "multi";
    public static final String DATANAME = "name";
    public static final String DATATITLE = "title";
    public static final String DATATYPE = "type";
    public static final String DATAVERIFY = "verify";
    public static final String DATAINPUTTYPE = "inputtype";
    public static final String BACKNAME = "name";
    private boolean verify=true;
    WriteNameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  DataBindingUtil.setContentView(this, R.layout.write_name);
        binding.setBehavior(this);
        ImageView leftbtn = binding.titleLayout.leftIcon;
        leftbtn.setImageResource(R.drawable.back_icon);
        mPresenter.init();
    }
    public void clearname(View view){
        binding.setUsername("");
    }
    @Override
    public void setSingle(int inputtype){
        binding.markname.setLines(1);
        binding.markname.setMaxLines(1);
        binding.markname.setInputType(inputtype);
    }
    @Override
    public void setMulti(int inputtype){
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, StringUtil.dip2px(WriteNameActivity.this,110));
        int margin = StringUtil.dip2px(WriteNameActivity.this, 10);
        param.setMargins(margin, margin,margin, 0);
        binding.markname.setGravity(Gravity.TOP| Gravity.LEFT);
        binding.markname.setPadding(10,10,10,10);
        binding.markname.setLayoutParams(param);
        binding.markname.setLines(5);
        binding.markname.setMaxLines(5);
        binding.markname.setInputType(inputtype);
    }
    @Override
    public void init(String name, final String title, final boolean verify) {
        binding.setUsername(name);
        binding.markname.setHint(title);
        binding.setBehavior(this);
        binding.titleLayout.setBehavior(this);
        //设置标题
        binding.titleLayout.title.setText(title);
        TextView tvright = binding.titleLayout.rightTextBtn;
        tvright.setText("确定");
        this.verify  = verify;
    }
    @Override
    public void right_text(View view) {
        String title = binding.titleLayout.title.getText().toString();
        String name = binding.markname.getText().toString();
        if(verify&&StringUtil.isNull(name)){
            UIUtil.showMessage(WriteNameActivity.this,title+"不能为空");
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(BACKNAME,name);
        setResult(RESULT_OK,intent);
        WriteNameActivity.this.finish();
    }
}
