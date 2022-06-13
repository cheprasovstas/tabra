package com.showcase.tabra.ui.product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import com.showcase.tabra.data.model.Product;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class ProductKeyProvider  extends ItemKeyProvider<String> {


    private final ProductsRecyclerViewAdapter adapter;

    protected ProductKeyProvider(ProductsRecyclerViewAdapter adapter) {
        super(ItemKeyProvider.SCOPE_CACHED);
        this.adapter = adapter;
    }

    @Nullable
    @Override
    public String getKey(int position) {
            return adapter.getItem(position).getId().toString();
    }

    @Override
    public int getPosition(@NonNull @NotNull String key) {
        return adapter.getPosition(key);
    }

}
