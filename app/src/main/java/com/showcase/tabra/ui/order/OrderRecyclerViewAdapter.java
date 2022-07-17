package com.showcase.tabra.ui.order;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.showcase.tabra.data.model.PlaceholderContent.PlaceholderItem;
import com.showcase.tabra.databinding.OrderFragmentBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {

    private final List<PlaceholderItem> mValues;

    public OrderRecyclerViewAdapter(List<PlaceholderItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(OrderFragmentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).content);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;

        public PlaceholderItem mItem;

        public ViewHolder(OrderFragmentBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}