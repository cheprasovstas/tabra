package com.showcase.tabra.ui.product;

import androidx.recyclerview.selection.ItemDetailsLookup;
import org.jetbrains.annotations.Nullable;

public class ProductItemDetails extends ItemDetailsLookup.ItemDetails<String> {
    private final int position;
    private final String key;
    public ProductItemDetails(int position, String key) {
        this.position = position;
        this.key = key;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String  getSelectionKey() {
        return key;
    }
}