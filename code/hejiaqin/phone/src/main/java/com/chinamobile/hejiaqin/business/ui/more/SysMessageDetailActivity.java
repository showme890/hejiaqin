package com.chinamobile.hejiaqin.business.ui.more;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.chinamobile.hejiaqin.R;
import com.chinamobile.hejiaqin.business.ui.basic.BasicActivity;
import com.chinamobile.hejiaqin.business.ui.basic.view.HeaderView;

/**
 * Created by eshaohu on 16/5/28.
 */
public class SysMessageDetailActivity extends BasicActivity implements View.OnClickListener {
    private TextView mTitleTv;
    private TextView mDateTv;
    private TextView mBodyTv;
    private HeaderView headerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_system_message_detail;
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.sys_message_detail_title);
        mDateTv = (TextView) findViewById(R.id.sys_message_detail_date);
        mBodyTv = (TextView) findViewById(R.id.sys_message_detail_body);
        headerView = (HeaderView) findViewById(R.id.system_message_detail_header);
        headerView.title.setText(R.string.more_sys_msg_sys_message);
        headerView.backImageView.setImageResource(R.mipmap.title_icon_back_nor);
        headerView.backImageView.setClickable(true);
        headerView.backImageView.setOnClickListener(this);
    }

    @Override
    protected void initDate() {
        Intent msg = getIntent();
        mTitleTv.setText(msg.getStringExtra("msgTitle"));
        mDateTv.setText(msg.getStringExtra("msgDate"));
        mBodyTv.setText(msg.getStringExtra("msgBody"));
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initLogics() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            default:
                break;
        }
    }
}