package com.suntend.arktoolbox.ui.arklabel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;


import androidx.annotation.NonNull;

import com.suntend.arktoolbox.R;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/4.
 */
class ArkLabelMoreOptionDialog extends Dialog {
    private OnArkLabelOptionCallback callback;
    private ArkLabelEntity entity;

    public ArkLabelMoreOptionDialog(@NonNull Context context, OnArkLabelOptionCallback callback) {
        super(context, R.style.TransDialog);
        this.callback = callback;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_ark_label_option);
        getWindow().setBackgroundDrawableResource(R.color.transparent);


        findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onEdit(entity);
                dismiss();
            }
        });
        findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDelete(entity);
                dismiss();
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void show(ArkLabelEntity entity) {
        super.show();
        this.entity = entity;
    }

    interface OnArkLabelOptionCallback {
        void onEdit(ArkLabelEntity entity);

        void onDelete(ArkLabelEntity entity);
    }

}
