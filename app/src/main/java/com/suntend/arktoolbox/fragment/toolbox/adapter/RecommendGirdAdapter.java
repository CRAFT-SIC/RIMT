package com.suntend.arktoolbox.fragment.toolbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suntend.arktoolbox.R;

import java.util.List;

public class RecommendGirdAdapter extends BaseAdapter {
    private List<String> dataList;
    private Context mContext;
    private LayoutInflater inflater;

    public RecommendGirdAdapter(Context mContext, List<String> dataList) {
        super();
        this.mContext = mContext;
        this.dataList = dataList;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public String getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup viewGroup) {
        v = inflater.inflate(R.layout.item_grid_recommend, null);
        TextView tv = v.findViewById(R.id.text_recommend_item);
        tv.setText(dataList.get(position));
        return v;
    }
}
