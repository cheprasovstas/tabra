package com.showcase.tabra.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.OrderItem;
import com.showcase.tabra.databinding.OrderDetailItemListElementBinding;


import java.util.ArrayList;
import java.util.List;

public class OrderItemsRecyclerViewAdapter extends RecyclerView.Adapter<OrderItemsRecyclerViewAdapter.ViewHolder> {

    private List<OrderItem> orderItemList = new ArrayList<OrderItem>();
    private OrderItemsRecyclerViewAdapter.onItemClickListener mItemClickListener;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OrderItemsRecyclerViewAdapter.ViewHolder(OrderDetailItemListElementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        OrderItem orderItem = orderItemList.get(position);
        holder.bind(orderItem);
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
        notifyDataSetChanged();
    }


    OrderItem getItem(int id) {
        return orderItemList.get(id);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView orderItemName;
        private final TextView orderItemPrice;
        private final TextView orderItemSum;
        private final TextView orderItemQuantity;

        public ViewHolder(OrderDetailItemListElementBinding binding) {
            super(binding.getRoot());
            this.orderItemName = binding.orderItemName;
            this.orderItemPrice = binding.orderItemPrice;
            this.orderItemSum = binding.orderItemSum;
            this.orderItemQuantity = binding.orderItemQuantity;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        }

        public void bind(OrderItem item) {
            this.orderItemName.setText(item.getProduct_name());
            if (item.getQuantity() != null) {
                this.orderItemQuantity.setText(item.getQuantity().toString());
            }
            if (item.getPrice() != null) {
                this.orderItemPrice.setText(" x " + item.getPrice().toString());
            }
            this.orderItemSum.setText(item.getSum_price());
        }
    }

    // allows clicks events to be caught
    void setClickListener(OrderItemsRecyclerViewAdapter.onItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface onItemClickListener {
        void onItemClick(View view, int position);
    }

}
