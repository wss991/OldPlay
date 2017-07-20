package com.td.oldplay.ui.forum.adapter;

import android.content.Context;
import android.view.View;

import com.td.oldplay.R;
import com.td.oldplay.base.adapter.recyclerview.CommonAdapter;
import com.td.oldplay.base.adapter.recyclerview.base.ViewHolder;
import com.td.oldplay.bean.ForumBean;
import com.td.oldplay.utils.TimeMangerUtil;

import java.util.List;

/**
 * Created by my on 2017/7/10.
 */

public class ForumListAdapter extends CommonAdapter<ForumBean> {
    public ForumListAdapter(Context context, int layoutId, List<ForumBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, ForumBean forumBean, int position) {
        holder.setText(R.id.item_title,forumBean.title);
        holder.setText(R.id.item_content,forumBean.content);
        holder.setText(R.id.item_name,forumBean.userName);
        holder.setText(R.id.item_comment,forumBean.replyCount+"评论");
        holder.setText(R.id.item_time, TimeMangerUtil.friendsCirclePastDate(forumBean.formatTime));
        if(forumBean.label==1){
            holder.setVisible(R.id.item_sence,true);
        }else{
            holder.setVisible(R.id.item_sence,false);
        }


    }
}
