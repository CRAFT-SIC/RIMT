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
import com.suntend.arktoolbox.enums.PlatformIconEnum;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ArkLabelEditActivity extends AppCompatActivity {
    private ArkLabelEntity entity;
    private EditText mEditName;
    private EditText mEditUrl;
    private ImageView mIvSave;
    private ImageView mIvNameClear;
    private ImageView mIvUrlClear;
    private TextView mTvGameName;
    private ProgressBar mProgressBar;
    private ArkLabelSelectGameDialog selectGameDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ark_label_edit);
        entity = (ArkLabelEntity) getIntent().getSerializableExtra("entity");
        mEditName = findViewById(R.id.edit_name);
        mEditUrl = findViewById(R.id.edit_url);
        mIvNameClear = findViewById(R.id.iv_name_clear);
        mIvNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditName.setText("");
            }
        });
        mIvUrlClear = findViewById(R.id.iv_url_clear);
        mIvUrlClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEditUrl.setText("");
            }
        });
        mTvGameName = findViewById(R.id.tv_game_name);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mProgressBar = findViewById(R.id.progress);
        mIvSave = findViewById(R.id.iv_save);
        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = mEditName.getText().toString();
                String url = mEditUrl.getText().toString();
                if (url.isEmpty()) {
                    Toast.makeText(ArkLabelEditActivity.this, "链接不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    Toast.makeText(ArkLabelEditActivity.this, "链接不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (int i = 0; i < PlatformIconEnum.values().length; i++) {
                    PlatformIconEnum item = PlatformIconEnum.values()[i];
                    if (url.startsWith("http://" + item.website) || url.startsWith("https://" + item.website)) {
                        entity.iconAttr = item.iconAttr;
                        break;
                    }
                }
                if (name.isEmpty()) {
                    //web读取
                    WebView webView = new WebView(ArkLabelEditActivity.this);
                    WebSettings settings = webView.getSettings();
                    settings.setJavaScriptEnabled(true);

                    webView.setWebViewClient(new WebViewClient() {
                        boolean _isLoadError = false;

                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            super.onPageStarted(view, url, favicon);
                            _isLoadError = false;
                            mProgressBar.setVisibility(View.VISIBLE);
                            mIvSave.setVisibility(View.GONE);
                        }

                        @Override
                        public void onPageFinished(WebView view, String u) {
                            super.onPageFinished(view, u);
                            mProgressBar.setVisibility(View.GONE);
                            mIvSave.setVisibility(View.VISIBLE);
                            if (!_isLoadError) {
                                entity.name = webView.getTitle();
                                entity.url = url;
                                Intent intent = new Intent();
                                intent.putExtra("entity", entity);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(ArkLabelEditActivity.this, "读取标题失败，请手动输入", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            super.onReceivedError(view, request, error);
                            error();
                        }

                        @Override
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            super.onReceivedError(view, errorCode, description, failingUrl);
                            error();
                        }

                        @Override
                        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                            super.onReceivedSslError(view, handler, error);
                            error();
                        }

                        @Override
                        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                            super.onReceivedHttpError(view, request, errorResponse);
                            error();
                        }

                        void error() {
                            _isLoadError = true;
                            mProgressBar.setVisibility(View.GONE);
                            mIvSave.setVisibility(View.VISIBLE);
                        }
                    });
                    webView.loadUrl(url);

                } else {
                    entity.name = name;
                    entity.url = url;
                    Intent intent = new Intent();
                    intent.putExtra("entity", entity);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        selectGameDialog = new ArkLabelSelectGameDialog(ArkLabelEditActivity.this);
        selectGameDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mTvGameName.setText(entity.game.label);
            }
        });
        findViewById(R.id.ll_game_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectGameDialog.show(entity);
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
        mEditUrl.addTextChangedListener(new TextWatcher() {
            final int normalSaveIcon = AttrUtil.getResId(R.attr.icon_ark_label_save_normal);
            final int successSaveIcon = AttrUtil.getResId(R.attr.icon_ark_label_save_success);

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String url = editable.toString();
                mIvUrlClear.setVisibility(url.isEmpty() ? View.GONE : View.VISIBLE);
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    mIvSave.setImageResource(normalSaveIcon);
                } else {
                    mIvSave.setImageResource(successSaveIcon);
                }
            }
        });
        mEditName.setText(entity.name);
        mEditUrl.setText(entity.url);
        mTvGameName.setText(entity.game.label);
    }
}
