package com.suntend.arktoolbox.fragment.toolbox.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
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
    private String listType = "list";//列表模式：list  网格模式：grid
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
        void onItemClick(View view, int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建并返回 ViewHolder 对象
        if (viewType == 1) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tool_recycler_view_category, null);
            return new ViewHolderCategory(mView);
        } else if (viewType == 2) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(listType.equals("list") ?
                    R.layout.item_tool_recycler_view_info : R.layout.item_tool_grid_view_info, null);
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
            Integer imageResId = null;
            if (imgResMap.containsKey(bean.getIcon()))
                imageResId = imgResMap.get(bean.getIcon());
            if (imageResId == null) {
                String packageName = mContext.getPackageName();
                imageResId = mContext.getResources().getIdentifier(bean.getIcon(), "mipmap", packageName);
                imgResMap.put(bean.getIcon(), imageResId);
            }
            ((ViewHolderInfo) holder).linearInfo.setOnClickListener(view -> onItemClickListener.onItemClick(view, position));
            try {
                ((ViewHolderInfo) holder).imgIcon.setImageDrawable(ContextCompat.getDrawable(mContext, imageResId));
            } catch (Exception e) {
                Log.e("ToolBoxRecyclerViewAdapter", e.getMessage() + ":" + bean.getIcon());
            }
            //如果是网格，切换icon的外圈背景显示
            if (listType.equals("grid"))
                ((ViewHolderInfo) holder).imgIcon.setBackground(ContextCompat.getDrawable(mContext, bean.getFollow() ?
                        R.drawable.bg_grid_icon_select : R.drawable.bg_grid_icon_unselect));
            //关注按钮的显示逻辑
            ImageView checkFollow = ((ViewHolderInfo) holder).imgFollow;
            ImageView infoIcon = ((ViewHolderInfo) holder).imgIcon;

            int resource_img_follow_checked = listType.equals("list") ? R.attr.img_follow_checked : R.attr.img_follow_grid_checked;
            int resource_img_follow_unchecked = listType.equals("list") ? R.attr.img_follow_unchecked : R.attr.img_follow_grid_unchecked;
            int resource_img_follow_cancel = listType.equals("list") ? R.mipmap.icon_common_cancel_night : R.mipmap.icon_common_cancel;
            //视图长按以及点击按钮都修改收藏状态
            ((ViewHolderInfo) holder).linearInfo.setOnLongClickListener(view -> {
                changeFollowStatus(bean, checkFollow, infoIcon);
                return true;
            });
            checkFollow.setOnClickListener(view -> {
                changeFollowStatus(bean, checkFollow, infoIcon);
            });
            checkFollow.setOnLongClickListener(view -> {
                changeFollowStatus(bean, checkFollow, infoIcon);
                return true;
            });
            TypedValue drawable = new TypedValue();
            mContext.getTheme().resolveAttribute(bean.getFollow() ? resource_img_follow_checked : resource_img_follow_unchecked, drawable, true);
            checkFollow.setImageDrawable(ContextCompat.getDrawable(mContext, followChecked ? resource_img_follow_cancel : drawable.resourceId));
        }
    }

    /**
     * 统一修改收藏改变后的效果监听
     *
     * @param bean        item数据对象
     * @param checkFollow 点击收藏的按钮
     * @param infoIcon    工具图标
     */
    private void changeFollowStatus(ToolboxBean bean, ImageView checkFollow, ImageView infoIcon) {
        int resource_img_follow_checked = listType.equals("list") ? R.attr.img_follow_checked : R.attr.img_follow_grid_checked;
        int resource_img_follow_unchecked = listType.equals("list") ? R.attr.img_follow_unchecked : R.attr.img_follow_grid_unchecked;
        int resource_img_follow_cancel = listType.equals("list") ? R.mipmap.icon_common_cancel_night : R.mipmap.icon_common_cancel;

        helper.updateFollowStatus(bean.getId(), !bean.getFollow());
        bean.setFollow(!bean.getFollow());
        RIMTUtil.ShowToast(mContext, bean.getFollow() ? "功能收藏成功" : "取消收藏成功");
        TypedValue drawable = new TypedValue();
        mContext.getTheme().resolveAttribute(bean.getFollow() ? resource_img_follow_checked : resource_img_follow_unchecked, drawable, true);
        checkFollow.setImageDrawable(ContextCompat.getDrawable(mContext, followChecked ? (bean.getFollow() ? resource_img_follow_cancel : drawable.resourceId) : drawable.resourceId));
        //如果是网格，切换icon的外圈背景显示
        if (!followChecked && listType.equals("grid"))
            infoIcon.setBackground(ContextCompat.getDrawable(mContext, bean.getFollow() ?
                    R.drawable.bg_grid_icon_select : R.drawable.bg_grid_icon_unselect));
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

    public void setNewData(List<ToolboxBean> dataList, String searchContent, String listType) {
        this.dataList = dataList;
        this.searchContent = searchContent;
        this.listType = listType;
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