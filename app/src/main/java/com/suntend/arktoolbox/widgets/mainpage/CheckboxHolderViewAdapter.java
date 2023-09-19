package com.suntend.arktoolbox.widgets.mainpage;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.widgets.mainpage.beans.Theme;

import java.util.List;

public class CheckboxHolderViewAdapter extends RecyclerView.Adapter<CheckboxHolderViewAdapter.CheckboxHolder> {

    private final List<Theme> list;

    private final LayoutInflater layoutInflater;

    Context context;

    public CheckboxHolderViewAdapter(Context context) {
        list = DataFactory.getThemes();
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CheckboxHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fragment_mainpage_checkbox_holder,parent,false);
        return new CheckboxHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckboxHolder holder, int position) {
        holder.textView.setText(list.get(position).getText());
        Bitmap bitmap = Bitmap.createBitmap(25,25, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(list.get(position).getColor());
        canvas.drawCircle(12.5f,12.5f,12.5f,paint);
        holder.imageView.setImageBitmap(bitmap);
        holder.radioButton.setChecked(list.get(position).isSelected());
        holder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.forEach(theme -> {
                    theme.setSelected(false);
                });
                list.get(position).setSelected(true);
                notifyDataSetChanged();
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CheckboxHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private final TextView textView;

        private final RadioButton radioButton;

        public CheckboxHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.checkbox_item_image);
            textView = itemView.findViewById(R.id.checkbox_item_text);
            radioButton = itemView.findViewById(R.id.checkbox_item_button);
        }

    }
}
