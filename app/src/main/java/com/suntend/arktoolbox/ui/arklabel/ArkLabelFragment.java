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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.RIMTUtil.ArkLabelSp;
import com.suntend.arktoolbox.RIMTUtil.AttrUtil;
import com.suntend.arktoolbox.RIMTUtil.ConvertUtils;

import java.util.ArrayList;

/**
 * Class:
 * Other:
 * Create by jsji on  2023/8/1.
 */
public class ArkLabelFragment extends Fragment {
    private RecyclerView mRv;
    private ArrayList<ArkLabelEntity> list = new ArrayList<>();
    private ArkLabelMoreOptionDialog optionDialog;
    private ArkLabelAdapter adapter;
    private final ArkLabelSp.OnArkLabelChangeListener arkLabelChangeListener= new ArkLabelSp.OnArkLabelChangeListener() {
        @Override
        public void onChange(ArrayList<ArkLabelEntity> list) {
            ArkLabelFragment.this.list = list;
            adapter.setNewData(list);
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
        root.findViewById(R.id.iv_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ArkLabelEditActivity.class);
                ArkLabelEntity entity = new ArkLabelEntity();
                entity.url="https://";
                intent.putExtra("entity", entity);
                startActivityForResult(intent, 1);
            }
        });
        adapter = new ArkLabelAdapter(new ArkLabelAdapter.OnArkLabelAdapterItemClickListener() {
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
                optionDialog.show(entity);
            }
        });
        optionDialog = new ArkLabelMoreOptionDialog(getContext(), new ArkLabelMoreOptionDialog.OnArkLabelOptionCallback() {
            @Override
            public void onEdit(ArkLabelEntity entity) {
                Intent intent = new Intent(getContext(), ArkLabelEditActivity.class);
                intent.putExtra("entity", entity);
                startActivityForResult(intent, 1);
            }

            @Override
            public void onDelete(ArkLabelEntity entity) {
                list.remove(entity);
                adapter.notifyDataSetChanged();
                ArkLabelSp.getInstance().saveLabelList(list);
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
        ArkLabelSp.getInstance().loadLabelList();
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
            ArkLabelEntity entity = (ArkLabelEntity) data.getSerializableExtra("entity");

            if (entity.id == 0) {
                //添加
                int nowId = 0;
                for (int i = 0; i < list.size(); i++) {
                    ArkLabelEntity item = list.get(i);
                    if (item.id > nowId) {
                        nowId = item.id;
                    }
                }
                entity.id = nowId + 1;
                list.add(entity);
                adapter.notifyDataSetChanged();
                ArkLabelSp.getInstance().saveLabelList(list);
            } else {
                for (int i = 0; i < list.size(); i++) {
                    ArkLabelEntity item = list.get(i);
                    if (item.id == entity.id) {
                        item.name = entity.name;
                        item.url = entity.url;
                        item.game = entity.game;
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
                ArkLabelSp.getInstance().saveLabelList(list);
            }
        }
    }

    //uid变更通知
    void userChangeEvent(String uid) {

    }

    ///开启线程，自定义逻辑
}
