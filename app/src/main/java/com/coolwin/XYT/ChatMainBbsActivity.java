package com.coolwin.XYT;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.coolwin.XYT.fragment.ChatFragment;

/**
 * Created by yf on 2017/2/16.
 */

public class ChatMainBbsActivity extends BaseActivity implements View.OnClickListener {
    private ChatFragment chatFragment;
    private TextView mTitleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bbschat_main);
//        chatFragment = ChatFragment.newInstance(true);
//        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatFragment)
//                .show(chatFragment).commit();
        setTitleContent(R.drawable.back_btn,0, "");
        mTitleView = (TextView)findViewById(R.id.title);
        mTitleView.setText("社群消息");
        mLeftBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.left_btn:
                ChatMainBbsActivity.this.finish();
                break;
            default:
                break;
        }
    }
}
