package com.suntend.arktoolbox.widgets.mainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.widgets.mainpage.beans.Theme;

import java.util.List;

public class CheckboxHolderViewAdapter extends RecyclerView.Adapter<CheckboxHolderViewAdapter.CheckboxHolder> {

    private List<Theme> list;


    private LayoutInflater layoutInflater;

    Context context;

    public CheckboxHolderViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CheckboxHolderViewAdapter.CheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fragment_mainpage_checkbox_holder,parent,false);
        return new CheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxHolderViewAdapter.CheckboxHolder holder, int position) {
        holder.textView.setText(list.get(position).getText());
//        holder.imageView.setBackground();
//        holder.imageView.set
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class CheckboxHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        private TextView textView;

        private RadioButton radioButton;

        public CheckboxHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.checkbox_item_image);
            textView = itemView.findViewById(R.id.checkbox_item_text);
            radioButton = itemView.findViewById(R.id.checkbox_item_button);
        }

    }
}
