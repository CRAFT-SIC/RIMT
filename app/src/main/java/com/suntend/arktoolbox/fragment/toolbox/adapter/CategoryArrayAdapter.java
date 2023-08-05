package com.suntend.arktoolbox.fragment.toolbox.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.database.bean.ToolCategoryBean;

import java.util.List;

/**
 * 自定义spinner适配器
 */
public class CategoryArrayAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<ToolCategoryBean> categoryList;
    private Integer selectCategoryId = -1;//当前选择项的id
    private Boolean openStatus = false;

    public CategoryArrayAdapter(Context context, List<ToolCategoryBean> list, String[] s) {
        super(context, R.layout.spinner_dropdown_item_custom, s);
        mContext = context;
        categoryList = list;
    }

    /**
     * 下拉框样式
     */
    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_dropdown_item_custom, parent, false);
        }
        ToolCategoryBean bean = categoryList.get(position);

        TextView tv = convertView.findViewById(R.id.checked_text_dropdown_item);
        tv.setText(bean.getCategoryName());
        //修改文字颜色
        TypedValue color = new TypedValue();
        mContext.getTheme().resolveAttribute(
                bean.getCategoryId().equals(selectCategoryId) ?
                        R.attr.color_category_select : R.attr.color_category_unselect, color, true);
        tv.setTextColor(mContext.getColor(color.resourceId));
        return convertView;
    }

    /**
     * 当前选中框样式
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_select_item_custom, parent, false);
        }
        ToolCategoryBean bean = categoryList.get(position);
        selectCategoryId = bean.getCategoryId();

        TextView tv = convertView.findViewById(R.id.checked_text_select_item);
        tv.setText(bean.getCategoryName());
        convertView.findViewById(R.id.divider_spinner_select)
                .setVisibility(bean.getCategoryId() == 0 ? View.INVISIBLE : View.VISIBLE);
        //设置图标
        ImageView img = convertView.findViewById(R.id.img_spinner_expansion);
        TypedValue drawable = new TypedValue();
        mContext.getTheme().resolveAttribute(openStatus ? R.attr.img_category_list_open : R.attr.img_category_list_close, drawable, true);
        img.setImageDrawable(ContextCompat.getDrawable(mContext, drawable.resourceId));
        return convertView;
    }

    /**
     * 根据列表展开和关闭更新img图片的显示
     */
    public void updateTriangleImg(Boolean openStatus) {
        this.openStatus = openStatus;
        this.notifyDataSetChanged();
    }

    /**
     * 获取当前选中的分类id
     *
     * @return 分类id
     */
    public Integer getSelectCategoryId() {
        return this.selectCategoryId;
    }
}