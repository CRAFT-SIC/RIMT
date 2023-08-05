package com.suntend.arktoolbox.arklabel;

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
public class ArkLabelAdapter extends RecyclerView.Adapter<ArkLabelAdapter.ArkLabelHolder> {
    private ArrayList<ArkLabelEntity> list=new ArrayList<>();
    private final OnArkLabelAdapterItemClickListener callback;


    public ArkLabelAdapter( OnArkLabelAdapterItemClickListener callback) {

        this.callback = callback;
    }

    public void setNewData(ArrayList<ArkLabelEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArkLabelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArkLabelHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ark_label_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArkLabelHolder holder, int position) {
        ArkLabelEntity item = list.get(position);
        holder.mTvName.setText(item.name);
        holder.mTvUrl.setText(item.url);
        holder.mIvIcon.setImageResource(AttrUtil.getResId(item.iconAttr));
        holder.mIvMore.setOnClickListener(view -> {
            callback.onOptionClick(item);
        });
        holder.itemView.setOnClickListener(view -> {
            callback.onItemClick(item);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
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

    interface OnArkLabelAdapterItemClickListener {
        void onItemClick(ArkLabelEntity entity);

        void onOptionClick(ArkLabelEntity entity);
    }
}


