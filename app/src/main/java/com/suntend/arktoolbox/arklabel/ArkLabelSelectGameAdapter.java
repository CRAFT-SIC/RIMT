package com.suntend.arktoolbox.arklabel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.enums.GameEnum;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
public class ArkLabelSelectGameAdapter extends RecyclerView.Adapter<ArkLabelSelectGameAdapter.ArkLabelGameHolder> {
    private  ArkLabelEntity entity;
    private final View.OnClickListener callBack;
    private final int defaultColor=AttrUtil.getColor(R.attr.ark_label_general_text_color);
    private final int selectColor=AttrUtil.getColor(R.attr.ark_label_accent_text_color);

    public void setEntity(ArkLabelEntity entity) {
        this.entity = entity;
    }

    public ArkLabelSelectGameAdapter(View.OnClickListener callBack) {
        this.callBack = callBack;

    }

    @NonNull
    @Override
    public ArkLabelGameHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArkLabelGameHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ark_label_select_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArkLabelGameHolder holder, int position) {

        GameEnum item = GameEnum.values()[position];
        System.out.println(item);
        System.out.println(entity.game);
        holder.mTvName.setText(item.label);
        holder.mTvName.setTextColor(item == entity.game ?selectColor :defaultColor);
        holder.itemView.setOnClickListener(view -> {
            entity.game = item;
            callBack.onClick(view);
        });
    }

    @Override
    public int getItemCount() {
        return GameEnum.values().length;
    }

    public static class ArkLabelGameHolder extends RecyclerView.ViewHolder {

        TextView mTvName;


        public ArkLabelGameHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }
}


