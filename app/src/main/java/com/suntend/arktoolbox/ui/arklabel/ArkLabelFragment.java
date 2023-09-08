package com.suntend.arktoolbox.ui.arklabel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.ArkLabelSp;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/1.
 */
public class ArkLabelFragment extends Fragment {
    private RecyclerView mRv;
    private ArkLabelAdapter adapter;
    private CardView mCardTopTip;
    private final ArkLabelSp.OnArkLabelChangeListener arkLabelChangeListener = new ArkLabelSp.OnArkLabelChangeListener() {
        @Override
        public void onChange(ArkLabelDirEntity data) {
            if (adapter != null)
                adapter.setNewData(data);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArkLabelSp.getInstance().addArkLabelChangeListener(arkLabelChangeListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_arklabel, container, false);

        ImageView OpenNav = root.findViewById(R.id.openNav);

        //设置打开侧边栏按钮
        OpenNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取 MainActivity 的引用
                MainActivity mainActivity = (MainActivity) getActivity();

                // 调用 MainActivity 的 openDrawer() 方法
                if (mainActivity != null) {
                    mainActivity.openDrawer();
                }
            }
        });
        mCardTopTip = root.findViewById(R.id.card_top_tip);
        mCardTopTip.setVisibility(ArkLabelSp.getInstance().isTopTipShowing() ? View.VISIBLE : View.GONE);

        root.findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ArkLabelBaseDialog(getContext())
                        .setTitle("新建项目")
                        .setMessage("标签格式符合要求可以自动获取更新")
                        .setData(Arrays.asList("新建标签页", "新建文件夹"))
                        .setOnOptionClickListener(new ArkLabelBaseDialog.OnOptionClickListener() {
                            @Override
                            public void onClick(int index) {
                                if (index == 0) {
                                    Intent intent = new Intent(getContext(), ArkLabelEditActivity.class);
                                    ArkLabelEntity entity = new ArkLabelEntity();
                                    entity.url = "https://";
                                    intent.putExtra("entity", entity);
                                    startActivityForResult(intent, 1);
                                } else {
                                    Intent intent = new Intent(getContext(), ArkLabelDirEditActivity.class);
                                    ArkLabelDirEntity entity = new ArkLabelDirEntity();
                                    intent.putExtra("entity", entity);
                                    startActivityForResult(intent, 2);
                                }
                            }
                        }).show();

            }
        });
        root.findViewById(R.id.tv_tip_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArkLabelSp.getInstance().setTopTipShowing(false);
                mCardTopTip.setVisibility(View.GONE);
            }
        });
        adapter = new ArkLabelAdapter();
        adapter.setNewData(ArkLabelSp.getInstance().getRootEntity());
        adapter.setOnItemClick(new ArkLabelAdapter.OnArkLabelAdapterItemClickListener() {
            @Override
            public void onItemClick(ArkLabelEntity entity) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.url));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "跳转异常，请检查链接是否正确", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onOptionClick(ArkLabelEntity entity) {
                new ArkLabelBaseDialog(getContext())
                        .setTitle("选择操作")
                        .setMessage("长摁标签可更改标签顺序")
                        .setData(Arrays.asList("编辑标签", "删除标签", "移入文件夹"))
                        .setOnOptionClickListener(new ArkLabelBaseDialog.OnOptionClickListener() {
                            @Override
                            public void onClick(int index) {
                                switch (index) {
                                    case 0: {
                                        Intent intent = new Intent(getContext(), ArkLabelEditActivity.class);
                                        intent.putExtra("entity", entity);
                                        startActivityForResult(intent, 1);
                                    }
                                    break;
                                    case 1:
                                        ArkLabelSp.getInstance().deleteLabel(entity);
                                        break;
                                    case 2: {
                                        Intent intent = new Intent(getContext(), ArkLabelDirSelectActivity.class);
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
                if (entity.labels.size() == 0 && entity.dirs.size() == 0) {
                    return;
                }
                Intent intent = new Intent(getContext(), ArkLabelDirSelectActivity.class);
                intent.putExtra("entity", entity);
                startActivityForResult(intent, 3);
            }

            @Override
            public void onDirOptionClick(ArkLabelDirEntity entity) {
                new ArkLabelBaseDialog(getContext())
                        .setTitle("选择操作")
                        .setMessage("删除文件夹会将文件夹内标签一起删除")
                        .setData(Arrays.asList("编辑文件夹", "删除文件夹"))
                        .setOnOptionClickListener(new ArkLabelBaseDialog.OnOptionClickListener() {
                            @Override
                            public void onClick(int index) {
                                switch (index) {
                                    case 0: {
                                        Intent intent = new Intent(getContext(), ArkLabelDirEditActivity.class);
                                        intent.putExtra("entity", entity);
                                        startActivityForResult(intent, 2);
                                    }
                                    break;
                                    case 1:
                                        ArkLabelSp.getInstance().deleteDir(entity);
                                        break;
                                    case 2:

                                        break;
                                }

                            }
                        }).show();
            }
        });
        //查询本地存储的内容
        mRv = root.findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(getContext()));
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
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ArkLabelSp.getInstance().removeArkLabelChangeListener(arkLabelChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 1) {
                ArkLabelEntity entity = (ArkLabelEntity) data.getSerializableExtra("entity");

                if (entity.id == 0) {
                    ArkLabelSp.getInstance().addLabel(entity);
                } else {
                    ArkLabelSp.getInstance().updateLabel(entity);
                }
            } else if (requestCode == 2) {
                ArkLabelDirEntity entity = (ArkLabelDirEntity) data.getSerializableExtra("entity");

                if (entity.id == 0) {
                    //添加
                    ArkLabelSp.getInstance().addDir(entity);
                } else {
                    ArkLabelSp.getInstance().updateDir(entity);
                }
            } else if (requestCode == 3) {
                //刷新数据
            }
        }
    }

    //uid变更通知
    void userChangeEvent(String uid) {

    }

    ///开启线程，自定义逻辑
}
