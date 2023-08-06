package com.suntend.arktoolbox.fragment.toolbox.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.RIMTUtil;
import com.suntend.arktoolbox.database.helper.ArkToolDatabaseHelper;
import com.suntend.arktoolbox.fragment.toolbox.bean.ToolboxBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolBoxRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<ToolboxBean> dataList;
    private Map<String, Integer> imgResMap;//暂存图片资源名字与id的map，提高效率
    private String searchContent = "";//高亮匹配的字符串片段
    private Boolean followChecked = false;//外部是否点击了仅查看收藏
    private ArkToolDatabaseHelper helper;
    private OnItemClickListener onItemClickListener;

    public ToolBoxRecyclerViewAdapter(Context context, List<ToolboxBean> dataList, ArkToolDatabaseHelper helper, OnItemClickListener onItemClickListener) {
        //初始化适配器
        this.mContext = context;
        this.dataList = dataList;
        this.helper = helper;
        this.imgResMap = new HashMap<>();
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemOnClick(View view, int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建并返回 ViewHolder 对象
        if (viewType == 1) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool_recycler_view_category, null);
            return new ViewHolderCategory(mView);
        } else if (viewType == 2) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool_recycler_view_info, null);
            return new ViewHolderInfo(mView);
        } else {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool_recycler_view_bottom, null);
            return new ViewHolderBottom(mView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // 为ViewHolder的视图组件设置数据以及显示逻辑
        ToolboxBean bean = dataList.get(position);
        if (holder instanceof ViewHolderCategory) {
            ((ViewHolderCategory) holder).viewDivider.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
            ((ViewHolderCategory) holder).textCategoryName.setText(bean.getName());
            if (!searchContent.equals(""))
                setTextHighlight(((ViewHolderCategory) holder).textCategoryName, bean.getName());
        }
        if (holder instanceof ViewHolderBottom) {
            ((ViewHolderBottom) holder).textBottom.setText(getItemCount() > 11 ? "已经到底啦~" : "没有更多啦");
        }
        if (holder instanceof ViewHolderInfo) {
            ((ViewHolderInfo) holder).textInfoName.setText(bean.getName());
            if (!searchContent.equals(""))
                setTextHighlight(((ViewHolderInfo) holder).textInfoName, bean.getName());
            //显示对应icon
            int imageResId;
            if (imgResMap.containsKey(bean.getIcon())) imageResId = imgResMap.get(bean.getIcon());
            else {
                String packageName = mContext.getPackageName();
                imageResId = mContext.getResources().getIdentifier(bean.getIcon(), "mipmap", packageName);
                imgResMap.put(bean.getIcon(), imageResId);
            }
            ((ViewHolderInfo) holder).linearInfo.setOnClickListener(view -> onItemClickListener.onItemOnClick(view, position));
            ((ViewHolderInfo) holder).imgIcon.setImageDrawable(ContextCompat.getDrawable(mContext, imageResId));
            //关注按钮的显示逻辑
            ImageView checkFollow = ((ViewHolderInfo) holder).imgFollow;
            if (followChecked) {
                //显示无法点击的按钮
                checkFollow.setOnClickListener(null);
                TypedValue drawable = new TypedValue();
                mContext.getTheme().resolveAttribute(R.attr.img_follow_cant_check, drawable, true);
                checkFollow.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
            } else {
                checkFollow.setOnClickListener(view -> {
                    //修改数据库，并修改本项显示
                    helper.updateFollowStatus(bean.getId(), !bean.getFollow());
                    bean.setFollow(!bean.getFollow());
                    RIMTUtil.ShowToast(mContext, bean.getFollow() ? "功能收藏成功" : "取消收藏成功");
                    TypedValue drawable = new TypedValue();
                    mContext.getTheme().resolveAttribute(bean.getFollow() ? R.attr.img_follow_checked : R.attr.img_follow_unchecked, drawable, true);
                    checkFollow.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
                });
                TypedValue drawable = new TypedValue();
                mContext.getTheme().resolveAttribute(bean.getFollow() ? R.attr.img_follow_checked : R.attr.img_follow_unchecked, drawable, true);
                checkFollow.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataList.get(position).getType().equals(ToolboxBean.ToolInfoType.TYPE_CATEGORY))
            return 1;
        if (dataList.get(position).getType().equals(ToolboxBean.ToolInfoType.TYPE_INFO))
            return 2;
        if (dataList.get(position).getType().equals(ToolboxBean.ToolInfoType.TYPE_BOTTOM))
            return 3;
        return -1;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public ToolboxBean getItemByPosition(int position) {
        return dataList.get(position);
    }

    public void setNewData(List<ToolboxBean> dataList, String searchContent) {
        this.dataList = dataList;
        this.searchContent = searchContent;
        this.notifyDataSetChanged();
    }

    /**
     * 与外部保持一致
     */
    public void setFollowChecked(Boolean followChecked) {
        this.followChecked = followChecked;
    }

    public static class ViewHolderCategory extends RecyclerView.ViewHolder {
        public final View viewDivider;
        public final TextView textCategoryName;

        public ViewHolderCategory(View view) {
            super(view);
            viewDivider = view.findViewById(R.id.divider_list_category);
            textCategoryName = view.findViewById(R.id.text_list_category_name);
        }
    }

    public static class ViewHolderInfo extends RecyclerView.ViewHolder {
        public final ImageView imgIcon, imgFollow;
        public final TextView textInfoName;
        public final LinearLayout linearInfo;

        public ViewHolderInfo(View view) {
            super(view);
            imgIcon = view.findViewById(R.id.img_tool_info_icon);
            imgFollow = view.findViewById(R.id.img_follow_check);
            textInfoName = view.findViewById(R.id.text_list_info_name);
            linearInfo = view.findViewById(R.id.linear_toolbox_info);
        }
    }

    public static class ViewHolderBottom extends RecyclerView.ViewHolder {
        public final TextView textBottom;

        public ViewHolderBottom(View view) {
            super(view);
            textBottom = view.findViewById(R.id.text_list_bottom_name);
        }
    }

    /**
     * 给搜索到的字符串片段高亮
     *
     * @param tv 文本视图
     */
    private void setTextHighlight(TextView tv, String originalText) {
        int start = originalText.toLowerCase().indexOf(searchContent.toLowerCase());
        int end = start + searchContent.length();
        if (start < 0) return;//无搜索字符
        TypedValue color = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.color_search_text_highlight, color, true);
        ForegroundColorSpan highlightSpan = new ForegroundColorSpan(mContext.getColor(color.resourceId));
        SpannableStringBuilder builder = new SpannableStringBuilder(originalText);
        builder.setSpan(highlightSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(builder);
    }
}