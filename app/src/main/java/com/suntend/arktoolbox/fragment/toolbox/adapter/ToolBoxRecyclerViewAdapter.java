package com.suntend.arktoolbox.fragment.toolbox.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.suntend.arktoolbox.fragment.toolbox.PlaceholderContent.PlaceholderItem;
import com.suntend.arktoolbox.databinding.FragmentToolBoxBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ToolBoxRecyclerViewAdapter extends RecyclerView.Adapter<ToolBoxRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public ToolBoxRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentToolBoxBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView = null;
        public final TextView mContentView = null;
        public PlaceholderItem mItem;

        public ViewHolder(FragmentToolBoxBinding binding) {
            super(binding.getRoot());
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}