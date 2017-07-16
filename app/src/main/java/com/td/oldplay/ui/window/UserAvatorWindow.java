package com.td.oldplay.ui.window;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.td.oldplay.R;
import com.td.oldplay.bean.UserBean;
import com.td.oldplay.utils.GlideUtils;
import com.td.oldplay.widget.CircleImageView;

import cn.tee3.avd.User;

/**
 * Created by my on 2017/7/14.
 */

public class UserAvatorWindow extends PopupWindow {


    private UserBean user;
    private CircleImageView userIv;
    private TextView name;
    private TextView score;

    public UserAvatorWindow(Context context, UserBean user) {
        super(context);
        this.user = user;
        View view = LayoutInflater.from(context).inflate(R.layout.pop_user, null);
        setContentView(view);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        setOutsideTouchable(true);
        setFocusable(true);
        userIv = (CircleImageView) view.findViewById(R.id.pop_user_iv);
        name = (TextView) view.findViewById(R.id.pop_user_name);
        score = (TextView) view.findViewById(R.id.pop_user_score);
        if(user!=null){
            GlideUtils.setAvatorImage(context,user.avatar,userIv);
            name.setText(user.nickName);
            score.setText(user.score);
        }


    }


    public void showPopup(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else {
            this.dismiss();
        }
    }

}