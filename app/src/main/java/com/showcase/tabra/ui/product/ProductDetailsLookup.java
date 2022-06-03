package com.showcase.tabra.ui.product;

import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;
import com.showcase.tabra.data.model.Product;
import org.jetbrains.annotations.NotNull;

public class ProductDetailsLookup extends ItemDetailsLookup {
    private RecyclerView recyclerView;

    public ProductDetailsLookup(RecyclerView recyclerView) {
        super();
        this.recyclerView = recyclerView;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public ItemDetails<String> getItemDetails(@NonNull @NotNull MotionEvent motionEvent) {

        View view = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (view != null) {
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder instanceof ProductsRecyclerViewAdapter.ProductListHolder) {
                return ((ProductsRecyclerViewAdapter.ProductListHolder) viewHolder).getItemDetails();
            }
        }
        return null;

    }
}
