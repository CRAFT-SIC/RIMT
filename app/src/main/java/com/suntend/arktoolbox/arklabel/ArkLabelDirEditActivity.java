package com.suntend.arktoolbox.arklabel;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.enums.GameEnum;
import com.suntend.arktoolbox.enums.PlatformIconEnum;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ArkLabelDirEditActivity extends AppCompatActivity {
    private ArkLabelDirEntity entity;
    private EditText mEditName;
    private ImageView mIvSave;
    private ImageView mIvNameClear;
    private TextView mTvGameName;
    private ArkLabelSelectGameDialog selectGameDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ark_label_dir_edit);
        entity = (ArkLabelDirEntity) getIntent().getSerializableExtra("entity");
        mEditName = findViewById(R.id.edit_name);
        mIvNameClear = findViewById(R.id.iv_name_clear);
        mIvNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditName.setText("");
            }
        });

        mTvGameName = findViewById(R.id.tv_game_name);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mIvSave = findViewById(R.id.iv_save);
        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(ArkLabelDirEditActivity.this, "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                entity.name = name;
                Intent intent = new Intent();
                intent.putExtra("entity", entity);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        selectGameDialog = new ArkLabelSelectGameDialog(ArkLabelDirEditActivity.this);
        selectGameDialog.setOnGameSelectCallBack(new ArkLabelSelectGameAdapter.OnGameSelectCallBack() {
            @Override
            public void onClick(GameEnum game) {
                entity.game = game;
            }
        });
        selectGameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mTvGameName.setText(entity.game.label);
            }
        });
        findViewById(R.id.ll_game_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectGameDialog.show(entity.game);
            }
        });
        mEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mIvNameClear.setVisibility(editable.toString().isEmpty() ? View.GONE : View.VISIBLE);
            }
        });
        mEditName.setText(entity.name);
        mTvGameName.setText(entity.game.label);
    }
}
