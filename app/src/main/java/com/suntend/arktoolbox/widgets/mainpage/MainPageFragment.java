package com.suntend.arktoolbox.widgets.mainpage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.ArkToolBoxFragment;
import com.suntend.arktoolbox.MainActivity;
import com.suntend.arktoolbox.R;

//created by nooly
public class MainPageFragment extends Fragment {

    private boolean checkboxShow;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mainpage,container,false);
        ImageView OpenNav = root.findViewById(R.id.openNav);
        ImageView changeTheme = root.findViewById(R.id.changeTheme);
        RecyclerView checkboxRecyclerView = root.findViewById(R.id.checkbox_recycler_view);
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        RelativeLayout checkboxLayout = root.findViewById(R.id.checkbox);

        LinearLayoutManager checkLayoutManager = new LinearLayoutManager(getContext());
        checkLayoutManager.setOrientation(RecyclerView.VERTICAL);
        checkboxRecyclerView.setLayoutManager(checkLayoutManager);
        checkboxRecyclerView.setAdapter(new CheckboxHolderViewAdapter(getContext()));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new HolderViewAdapter(getContext()));

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.ark_label_card_background_color, typedValue, true);
        ImageView triangle = root.findViewById(R.id.checkbox_triangle);
        Bitmap bitmap = Bitmap.createBitmap(30,15, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(typedValue.data);
        Path path = new Path();
        path.moveTo(15,0);
        path.lineTo(0,15);
        path.lineTo(30,15);
        path.lineTo(15,0);
        canvas.drawPath(path,paint);
        triangle.setImageBitmap(bitmap);

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

        changeTheme.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(checkboxShow){
                    checkboxLayout.setVisibility(View.INVISIBLE);
                    checkboxShow = false;
                }
                else{
                    checkboxLayout.setVisibility(View.VISIBLE);
                    checkboxShow = true;
                }
            }
        });

        return root;
    }
}
