package com.cose.easywu.home.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cose.easywu.R;
import com.cose.easywu.home.bean.CommentDetailBean;
import com.cose.easywu.home.bean.ReplyDetailBean;
import com.cose.easywu.utils.Constant;
import com.cose.easywu.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentExpandAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "CommentExpandAdapter";
    private List<CommentDetailBean> commentBeanList;
    private Context mContext;

    public CommentExpandAdapter(Context context, List<CommentDetailBean> commentBeanList)               {
        this.mContext = context;
        this.commentBeanList = commentBeanList;
    }

    @Override
    public int getGroupCount() {
        return commentBeanList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(commentBeanList.get(groupPosition).getReplyList() == null){
            return 0;
        } else {
            return commentBeanList.get(groupPosition).getReplyList().size() > 0 ?
                    commentBeanList.get(groupPosition).getReplyList().size() : 0;
        }
    }

    @Override
    public Object getGroup(int groupPosition) {
        return commentBeanList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return commentBeanList.get(groupPosition).getReplyList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getCombinedChildId(groupPosition, childPosition);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder groupHolder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, parent, false);
            groupHolder = new GroupHolder(convertView);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        Glide.with(mContext).load(Constant.BASE_PHOTO_URL + commentBeanList.get(groupPosition).getUserPhoto())
                .apply(new RequestOptions().placeholder(R.drawable.nav_icon).error(R.drawable.ic_error_goods))
                .into(groupHolder.iv_photo);
        groupHolder.tv_name.setText(commentBeanList.get(groupPosition).getNickName());
        groupHolder.tv_time.setText(DateUtil.getDatePoor(commentBeanList.get(groupPosition).getCreateTime(), new Date()));
        groupHolder.tv_content.setText(commentBeanList.get(groupPosition).getContent());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder childHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_reply_item_layout, parent, false);
            childHolder = new ChildHolder(convertView);
            convertView.setTag(childHolder);
        } else {
            childHolder = (ChildHolder) convertView.getTag();
        }

        String replyUser = commentBeanList.get(groupPosition).getReplyList().get(childPosition).getNickName();
        if(!TextUtils.isEmpty(replyUser)){
            childHolder.tv_name.setText(replyUser + ":");
        }

        childHolder.tv_content.setText(commentBeanList.get(groupPosition).getReplyList().get(childPosition).getContent());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addTheCommentData(CommentDetailBean commentDetailBean) {
        if(commentDetailBean != null) {
            commentBeanList.add(commentDetailBean);
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("评论数据为空!");
        }
    }

    public void addTheReplyData(ReplyDetailBean replyDetailBean, int groupPosition){
        if(replyDetailBean != null) {
            if(commentBeanList.get(groupPosition).getReplyList() != null) {
                commentBeanList.get(groupPosition).getReplyList().add(replyDetailBean);
            } else {
                List<ReplyDetailBean> replyList = new ArrayList<>();
                replyList.add(replyDetailBean);
                commentBeanList.get(groupPosition).setReplyList(replyList);
            }
            notifyDataSetChanged();
        } else {
            throw new IllegalArgumentException("回复数据为空!");
        }

    }

    private class GroupHolder{
        private ImageView iv_photo;
        private TextView tv_name, tv_content, tv_time;
        public GroupHolder(View view) {
            iv_photo =  view.findViewById(R.id.comment_item_userPhoto);
            tv_content = view.findViewById(R.id.comment_item_content);
            tv_name = view.findViewById(R.id.comment_item_userName);
            tv_time = view.findViewById(R.id.comment_item_time);
        }
    }

    private class ChildHolder{
        private TextView tv_name, tv_content;
        public ChildHolder(View view) {
            tv_name = view.findViewById(R.id.reply_item_userName);
            tv_content = view.findViewById(R.id.reply_item_content);
        }
    }
}
