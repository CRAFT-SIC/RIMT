package com.suntend.arktoolbox.ui.arklabel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.enums.GameEnum;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
class ArkLabelSelectGameDialog extends Dialog {
    private GameEnum selectGame;
    private RecyclerView mRv;
    private ArkLabelSelectGameAdapter adapter;
    private  ArkLabelSelectGameAdapter.OnGameSelectCallBack onGameSelectCallBack;

    public ArkLabelSelectGameDialog(@NonNull Context context) {
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

        adapter = new ArkLabelSelectGameAdapter(new ArkLabelSelectGameAdapter.OnGameSelectCallBack() {
            @Override
            public void onClick(GameEnum game) {
                if (onGameSelectCallBack!=null){
                    onGameSelectCallBack.onClick(game);
                }
                dismiss();
            }
        });
        adapter.setSelectGame(selectGame);
        mRv.setAdapter(adapter);
    }

    public void setOnGameSelectCallBack(ArkLabelSelectGameAdapter.OnGameSelectCallBack onGameSelectCallBack) {
        this.onGameSelectCallBack = onGameSelectCallBack;
    }

    public void show(GameEnum game) {
        super.show();
        this.selectGame = game;
        if (adapter != null) {
            adapter.setSelectGame(game);
            adapter.notifyDataSetChanged();
        }
    }

}
