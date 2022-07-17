package com.showcase.tabra.ui.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;
import com.showcase.tabra.R;
import com.squareup.picasso.Picasso;
import com.showcase.tabra.data.model.Product;

import java.util.ArrayList;
import java.util.List;



public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ProductListHolder> {
    enum EditMode {
        inStore,
        inArchive,
        Selection,
        NONE
    }

    public EditMode editMode = EditMode.NONE;

    public void setEditMode(EditMode editMode) {
        if (!this.editMode.equals(editMode)) {
            this.editMode = editMode;
            notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private List<Product> productList;
    private LayoutInflater mInflater;
    private onProductClickListener mProductClickListener;
    private SelectionTracker<String> tracker = null;



    // data is passed into the constructor
    ProductsRecyclerViewAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.productList = new ArrayList<Product>();

        setHasStableIds(true);

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
        holder.bind(product);
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
        int position = getPosition(product.getId().toString());
        if (position!=-1) {
            this.productList.set(position, product);
            notifyItemChanged(position);
        }
    }

    public void removeProduct(Product product) {
        int position = this.productList.indexOf(product);
        this.productList.remove(position);
        notifyDataSetChanged();
    }

    public void setTracker(SelectionTracker<String> tracker) {
        this.tracker = tracker;
    }



    // stores and recycles views as they are scrolled off screen
    public class ProductListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView productNameTextView;
        TextView productPriceTextView;
        ImageView productImageView, selectedImageView, activeImageView, instoreImageView;

        ProductListHolder(View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productName);
            productPriceTextView = itemView.findViewById(R.id.productPrice);
            productImageView = itemView.findViewById(R.id.productImage);
            selectedImageView = itemView.findViewById(R.id.selectedImageView);
            activeImageView = itemView.findViewById(R.id.activeImageView);
            instoreImageView = itemView.findViewById(R.id.instoreImageView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            if (mProductClickListener != null) {
                mProductClickListener.onProductClick(view, getBindingAdapterPosition());
//                selectedImageView.setVisibility(isSelectionMode ? View.VISIBLE : View.GONE);

            }
        }

        public void bind(Product product) {
            this.productNameTextView.setText(product.getName()+ ( product.getCategoryName() != null ? " ("+product.getCategoryName()+")": ""));
            if (product.getPrice()!=null) {
                this.productPriceTextView.setText(product.getPrice().toString()
                        +(product.getPrice_currency()!=null ? " "+product.getPrice_currency().getSymbol(): ""));
            }
            Picasso.get().load(product.getImage()).fit().centerCrop()
                    .placeholder(R.drawable.product_list_placeholder)
                    //               .error(R.drawable.user_placeholder_error)
                    .into(this.productImageView);

            activeImageView.setVisibility(!product.isActive() ? View.VISIBLE : View.GONE);
            if (product.isActive()) {
                instoreImageView.setVisibility(product.isInStore() ? View.VISIBLE : View.GONE);
             } else {
                instoreImageView.setVisibility(View.GONE);
            }


            selectedImageView.setVisibility(EditMode.NONE.equals(editMode) ? View.GONE : View.VISIBLE);

            switch (editMode){
                case Selection:
                    selectedImageView.setActivated(tracker.isSelected(product.getId().toString()));
                    break;
                case inStore:
                    selectedImageView.setActivated(product.isInStore());
                    break;
                case inArchive:
                    selectedImageView.setActivated(product.isActive());
                    break;
            }
        }

        public ItemDetailsLookup.ItemDetails<String> getItemDetails() {
            return new ProductItemDetails(getBindingAdapterPosition(), productList.get(getBindingAdapterPosition()).getId().toString());
        }

    }

    // convenience method for getting data at click position
    Product getItem(int id) {
        return productList.get(id);
    }


    public int getPosition(Product product) {
        if (product!=null){
            return productList.indexOf(product);
        }
        return -1;
    }

    public int getPosition(String key) {
        Product product = getProduct(key);
        if (product!=null){
            return getPosition(product);
        }
        return -1;
    }

    public Product getProduct(String key) {
        for (Product p : productList) {
            if (p.getId().toString().equals(key)) {
                return p;
            }
        }
        return null;
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
