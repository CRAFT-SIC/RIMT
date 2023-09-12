package com.suntend.arktoolbox.widgets.mainpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suntend.arktoolbox.R;
import com.suntend.arktoolbox.widgets.mainpage.beans.Card;

import java.util.ArrayList;


/**
 * Created By nooly,
 * 主页IP衍生的RecyclerView Adapter,
 * class:HolderViewAdapter,
 * package:com.suntend.arktoolbox.widgets.mainpage
 * */
public class HolderViewAdapter extends RecyclerView.Adapter<HolderViewAdapter.MainPageCardHolder>

{

    private LayoutInflater layoutInflater;

    private ArrayList<Card> cardList;

    public HolderViewAdapter(Context context){
        cardList = DataFactory.getCardsData();
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MainPageCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fragment_mainpage_cardview_holder,parent,false);
        return new MainPageCardHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainPageCardHolder holder, int position) {
        holder.holderContent.setText(cardList.get(position).getContent());
        holder.holderTitle.setText(cardList.get(position).getTitle());
        holder.holderTitleEN.setText(cardList.get(position).getTitleEN());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class MainPageCardHolder extends RecyclerView.ViewHolder {
        private TextView holderTitle;
        private ImageView holderImage;
        private TextView holderTitleEN;
        private TextView holderContent;
        public MainPageCardHolder(@NonNull View itemView) {
            super(itemView);
            holderImage = itemView.findViewById(R.id.holder_image);
            holderContent = itemView.findViewById(R.id.holder_content);
            holderTitle = itemView.findViewById(R.id.holder_title);
            holderTitleEN = itemView.findViewById(R.id.holder_title_en);
        }

    }
}