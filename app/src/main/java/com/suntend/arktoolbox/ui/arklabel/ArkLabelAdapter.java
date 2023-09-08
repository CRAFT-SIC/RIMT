package com.suntend.arktoolbox.ui.arklabel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;

import java.util.ArrayList;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
public class ArkLabelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArkLabelDirEntity data = new ArkLabelDirEntity();
    private OnArkLabelAdapterItemClickListener onItemClick;
    private boolean onlyReadDir = false;


    public ArkLabelAdapter() {

    }

    public void setOnItemClick(OnArkLabelAdapterItemClickListener onItemClick) {
        this.onItemClick = onItemClick;
    }

    public void setNewData(ArkLabelDirEntity data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnlyReadDir(boolean onlyReadDir) {
        this.onlyReadDir = onlyReadDir;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position < data.dirs.size() ? 1 : 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ArkLabelDirHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ark_label_list_dir, parent, false));

        } else {
            return new ArkLabelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ark_label_list, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (position < data.dirs.size()) {
            //第一个不算，所以所有position+1提取数据
            ArkLabelDirEntity item = data.dirs.get(position);
            ArkLabelDirHolder holder = (ArkLabelDirHolder) vh;
            holder.mTvName.setText(item.name);
            holder.mTvCount.setText("共" + item.labels.size() + "个标签");

            //holder.mIvIcon.setImageResource(AttrUtil.getResId(item.iconAttr));
            holder.mIvMore.setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onDirOptionClick(item);
                }
            });
            holder.mIvMore.setVisibility(onlyReadDir ? View.GONE : View.VISIBLE);
            holder.itemView.setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onDirItemClick(item);
                }
            });
        } else {
            ArkLabelEntity item = data.labels.get(position - (data.dirs.size()));
            ArkLabelHolder holder = (ArkLabelHolder) vh;
            holder.mTvName.setText(item.name);
            holder.mTvUrl.setText(item.url);
            holder.mIvIcon.setImageResource(AttrUtil.getResId(item.iconAttr));
            holder.mIvMore.setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onOptionClick(item);
                }
            });
            holder.mIvMore.setVisibility(onlyReadDir ? View.GONE : View.VISIBLE);
            holder.itemView.setOnClickListener(view -> {
                if (onItemClick != null) {
                    onItemClick.onItemClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        //不计第一个文件夹，但记第0个默认文件内的标签数量
        if (onlyReadDir) {
            return data.dirs.size();
        } else {
            return data.dirs.size() + data.labels.size();
        }
    }

    public static class ArkLabelHolder extends RecyclerView.ViewHolder {
        ImageView mIvIcon;
        ImageView mIvMore;
        TextView mTvName;
        TextView mTvUrl;

        public ArkLabelHolder(@NonNull View itemView) {
            super(itemView);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mIvMore = itemView.findViewById(R.id.iv_more);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvUrl = itemView.findViewById(R.id.tv_url);
        }
    }

    public static class ArkLabelDirHolder extends RecyclerView.ViewHolder {
        ImageView mIvIcon;
        ImageView mIvMore;
        TextView mTvName;
        TextView mTvCount;


        public ArkLabelDirHolder(@NonNull View itemView) {
            super(itemView);
            mIvIcon = itemView.findViewById(R.id.iv_icon);
            mIvMore = itemView.findViewById(R.id.iv_more);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvCount = itemView.findViewById(R.id.tv_count);

        }
    }


    interface OnArkLabelAdapterItemClickListener {
        void onItemClick(ArkLabelEntity entity);

        void onOptionClick(ArkLabelEntity entity);

        void onDirItemClick(ArkLabelDirEntity entity);

        void onDirOptionClick(ArkLabelDirEntity entity);
    }
}


