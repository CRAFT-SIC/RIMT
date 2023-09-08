package com.suntend.arktoolbox.ui.arklabel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
class ArkLabelBaseDialog extends Dialog {
    private List<Object> data = new ArrayList<>();
    private RecyclerView mRv;
    private ArkLabelBaseAdapter adapter;
    private OnOptionClickListener onOptionClickListener;
    private String title;
    private String message;

    public ArkLabelBaseDialog(@NonNull Context context) {
        super(context, R.style.TransDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ark_label_select_game);
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(title);
        ((TextView)findViewById(R.id.tv_message)).setText(message);
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            final Paint paint = new Paint();
            final int dividerColor = AttrUtil.getColor(R.attr.ark_label_divider_color);

            {
                paint.setColor(dividerColor);
                paint.setStrokeWidth(1);
                paint.setAntiAlias(true);

            }

            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.onDraw(c, parent, state);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View child = parent.getChildAt(i);
                    int position = parent.getChildAdapterPosition(child);
                    if (position != adapter.getItemCount() - 1) {
                        c.drawLine(child.getLeft(), child.getBottom(), child.getRight(), child.getBottom(), paint);
                    }
                }
            }

            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (state.getItemCount() != adapter.getItemCount()) {
                    outRect.bottom = 1;
                }
            }
        });

        adapter = new ArkLabelBaseAdapter(new OnOptionClickListener() {
            @Override
            public void onClick(int index) {
                if (onOptionClickListener != null) {
                    onOptionClickListener.onClick(index);
                }
                dismiss();
            }
        });
        adapter.setData(data);
        mRv.setAdapter(adapter);
    }

    public ArkLabelBaseDialog setOnOptionClickListener(OnOptionClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
        return this;
    }

    public ArkLabelBaseDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public ArkLabelBaseDialog setMessage(String message) {
        this.message = message;
        return this;
    }

    public ArkLabelBaseDialog setData(List<Object> data) {
        this.data = data;
        if (adapter != null) {
            adapter.setData(data);
        }
        return this;
    }

    public interface OnOptionClickListener {
        void onClick(int index);
    }

}

public class ArkLabelBaseAdapter extends RecyclerView.Adapter<ArkLabelBaseAdapter.ArkLabelBaseHolder> {
    private List<Object> data = new ArrayList<>();
    private final ArkLabelBaseDialog.OnOptionClickListener callBack;

    private final int defaultColor = AttrUtil.getColor(R.attr.ark_label_accent_text_color);
    private final int cancelColor = AttrUtil.getColor(R.attr.ark_label_hint_text_color);

    public void setData(List<Object> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public ArkLabelBaseAdapter(ArkLabelBaseDialog.OnOptionClickListener callBack) {
        this.callBack = callBack;

    }

    @NonNull
    @Override
    public ArkLabelBaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ArkLabelBaseHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ark_label_select_game, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArkLabelBaseHolder holder, int position) {
        String label = data.get(position).toString();
        holder.mTvName.setText(label);
        holder.mTvName.setTextColor(label.equals("取消") ? cancelColor : defaultColor);
        holder.itemView.setOnClickListener(view -> {

            callBack.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ArkLabelBaseHolder extends RecyclerView.ViewHolder {

        TextView mTvName;


        public ArkLabelBaseHolder(@NonNull View itemView) {
            super(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
        }
    }
}

