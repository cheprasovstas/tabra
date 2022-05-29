package com.showcase.tabra.ui.product;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.showcase.tabra.R;
import com.showcase.tabra.databinding.ProductDetailsBinding;
import com.showcase.tabra.databinding.ProductListRowBinding;
import com.squareup.picasso.Picasso;
import com.showcase.tabra.data.model.Product;

import java.util.ArrayList;
import java.util.List;



public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductListHolder> {

    private List<Product> productList;
    private LayoutInflater mInflater;
    private onProductClickListener mProductClickListener;
    private ProductListRowBinding binding;

    // data is passed into the constructor
    ProductsRecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.productList = new ArrayList<Product>();

    }

    // inflates the row layout from xml when needed
    @Override
    public ProductListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.product_list_row, parent, false);
        return (new ProductListHolder(itemView));
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ProductListHolder holder, int position) {
        Product product = productList.get(position);
        holder.productNameTextView.setText(product.getName());
        if (product.getPrice()!=null) {
            holder.productPriceTextView.setText(product.getPrice().toString());
        }
        Picasso.get().load(product.getImage()).fit().centerCrop()
                .placeholder(R.drawable.product_list_placeholder)
 //               .error(R.drawable.user_placeholder_error)
                .into(holder.productImageView);
//        Picasso.get().load(R.drawable.product_example).into(viewHolder.productImageView);
//        holder.itemView.setBackgroundColor(product.isSelected() ? Color.CYAN : Color.WHITE);


    }

    // total number of rows
    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void setProducts(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();

    }

    public void addProduct(Product product) {
        this.productList.add(product);
        notifyItemInserted(productList.size());
    }

    public void updateProduct(Product product) {
        if (product == null) return; // we cannot update the value because it is null

        for (Product item : this.productList) {
            // search by id
            if (item.getId().equals(product.getId())) {
                int position = this.productList.indexOf(item);
                this.productList.set(position, product);
                notifyItemChanged(position);
                break;
            }
        }
    }

    public void removeProduct(Product product) {
        int position = this.productList.indexOf(product);
        this.productList.remove(position);
        notifyItemRemoved(position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ProductListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView;

        ProductListHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productImageView = itemView.findViewById(R.id.productImage);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mProductClickListener != null) {
                mProductClickListener.onProductClick(view, getBindingAdapterPosition());
            }
        }

    }

    // convenience method for getting data at click position
    Product getItem(int id) {
        return productList.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(onProductClickListener itemClickListener) {
        this.mProductClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface onProductClickListener {
        void onProductClick(View view, int position);
    }

}
