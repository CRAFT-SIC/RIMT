package com.suntend.arktoolbox.arklabel;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.ArkLabelSp;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/5.
 */
public class ArkLabelDirSelectActivity extends AppCompatActivity {
    private ArkLabelDirEntity dirEntity;
    private ArkLabelEntity labelEntity;
    private ArkLabelAdapter adapter;
    private RecyclerView mRv;
    private TextView mTvTitle;
    private final ArkLabelSp.OnArkLabelChangeListener arkLabelChangeListener = new ArkLabelSp.OnArkLabelChangeListener() {
        @Override
        public void onChange(ArkLabelDirEntity data) {
            if (dirEntity != null && adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ArkLabelSp.getInstance().removeArkLabelChangeListener(arkLabelChangeListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArkLabelSp.getInstance().addArkLabelChangeListener(arkLabelChangeListener);
        setContentView(R.layout.activity_ark_label_dir_select);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mRv = findViewById(R.id.rv);
        mTvTitle = findViewById(R.id.tv_title);
        adapter = new ArkLabelAdapter();
        Serializable serializable = getIntent().getSerializableExtra("entity");
        // 这里读取到的数据是copy的，内存地址不同
        if (serializable instanceof ArkLabelDirEntity) {
            //查看内页-
            dirEntity = (ArkLabelDirEntity) serializable;
            Log.e("dirEntity", dirEntity.toString());
            dirEntity = ArkLabelSp.getInstance().queryDirEntity(dirEntity.id);
            Log.e("dirEntity", dirEntity.toString());
            mTvTitle.setText(dirEntity.name);
            adapter.setNewData(dirEntity);
        } else {
            //移入文件夹
            labelEntity = (ArkLabelEntity) serializable;
            mTvTitle.setText("选择文件夹");
            adapter.setOnlyReadDir(true);
            adapter.setNewData(ArkLabelSp.getInstance().getRootEntity());
        }

        //查询本地存储的内容

        adapter.setOnItemClick(new ArkLabelAdapter.OnArkLabelAdapterItemClickListener() {
            @Override
            public void onItemClick(ArkLabelEntity entity) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ArkLabelDirSelectActivity.this, "跳转异常，请检查链接是否正确", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onOptionClick(ArkLabelEntity entity) {
                new ArkLabelBaseDialog(ArkLabelDirSelectActivity.this)
                        .setTitle("选择操作")
                        .setMessage("长摁标签可更改标签顺序")
                        .setData(Arrays.asList("编辑标签", "删除标签", "移出到根目录", "移动到其他文件夹"))
                        .setOnOptionClickListener(new ArkLabelBaseDialog.OnOptionClickListener() {
                            @Override
                            public void onClick(int index) {
                                switch (index) {
                                    case 0: {
                                        Intent intent = new Intent(ArkLabelDirSelectActivity.this, ArkLabelEditActivity.class);
                                        intent.putExtra("entity", entity);
                                        startActivityForResult(intent, 1);
                                    }
                                    break;
                                    case 1:
                                        ArkLabelSp.getInstance().deleteLabel(entity);
                                        break;
                                    case 2: {
                                        ArkLabelSp.getInstance().change2RootDir(entity);
                                    }
                                    break;
                                    case 3: {
                                        Intent intent = new Intent(ArkLabelDirSelectActivity.this, ArkLabelDirSelectActivity.class);
                                        intent.putExtra("entity", entity);
                                        startActivityForResult(intent, 3);
                                    }
                                    break;
                                }
                            }
                        }).show();
            }

            @Override
            public void onDirItemClick(ArkLabelDirEntity entity) {
                if (dirEntity != null) {
                    Intent intent = new Intent(ArkLabelDirSelectActivity.this, ArkLabelDirSelectActivity.class);
                    intent.putExtra("entity", entity);
                    startActivity(intent);
                } else {
                    ArkLabelSp.getInstance().changeSuperDir(labelEntity, entity.id);
                    finish();
                }
            }

            @Override
            public void onDirOptionClick(ArkLabelDirEntity entity) {
                new ArkLabelBaseDialog(ArkLabelDirSelectActivity.this)
                        .setTitle("选择操作")
                        .setMessage("删除文件夹会将文件夹内标签一起删除")
                        .setData(Arrays.asList("编辑文件夹", "删除文件夹"))
                        .setOnOptionClickListener(new ArkLabelBaseDialog.OnOptionClickListener() {
                            @Override
                            public void onClick(int index) {
                                if (index == 0) {
                                    Intent intent = new Intent(ArkLabelDirSelectActivity.this, ArkLabelDirEditActivity.class);
                                    intent.putExtra("entity", entity);
                                    startActivityForResult(intent, 2);
                                } else {

                                    ArkLabelSp.getInstance().deleteDir(entity);
                                }
                            }
                        }).show();
            }
        });
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            final Paint paint = new Paint();
            final int dividerColor = AttrUtil.getColor(R.attr.ark_label_divider_color);
            final int padding = ConvertUtils.dp2px(50);

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
                        c.drawLine(child.getLeft() + padding, child.getBottom(), child.getRight() - padding, child.getBottom(), paint);
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
        mRv.setAdapter(adapter);
    }
}
