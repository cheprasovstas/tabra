package com.showcase.tabra.ui.order;

import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.databinding.OrderListElementBinding;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Order}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OrderRecyclerViewAdapter extends RecyclerView.Adapter<OrderRecyclerViewAdapter.ViewHolder> {

    private List<Order> orderList = new ArrayList<Order>();
    private OrderRecyclerViewAdapter.onOrderClickListener mOrderClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(OrderListElementBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<Order> orderList) {
        this.orderList = orderList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView orderName;
        private final TextView orderPhone;
        private final TextView orderCreatedAt;
        private final TextView orderSum;
        private final TextView orderStep;

        public ViewHolder(OrderListElementBinding binding) {
            super(binding.getRoot());
            this.orderName = binding.orderName;
            this.orderPhone = binding.orderPhone;
            this.orderCreatedAt = binding.orderCreatedAt;
            this.orderSum = binding.orderSum;
            this.orderStep = binding.orderStep;

            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOrderClickListener != null) {
                mOrderClickListener.onOrderClick(view, getBindingAdapterPosition());
            }
        }

        public void bind(Order order) {
            this.orderName.setText(order.getName());
            this.orderPhone.setText(order.getPhone());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
            try {
                Date date = format.parse(order.getCreated_at());
                this.orderCreatedAt.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            this.orderSum.setText(order.getSum_price()+ " " + order.getSum_price_currency());
            if (order.getStep()!=null){

                this.orderStep.setText(order.getStep().getName());
                this.orderStep.setBackgroundColor(Color.parseColor(order.getStep().getColor()));

            }
        }
    }

    // convenience method for getting data at click position
    Order getItem(int id) {
        return orderList.get(id);
    }


    // allows clicks events to be caught
    void setClickListener(OrderRecyclerViewAdapter.onOrderClickListener itemClickListener) {
        this.mOrderClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface onOrderClickListener {
        void onOrderClick(View view, int position);
    }
}