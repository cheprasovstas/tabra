package com.showcase.tabra.ui.product;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private final RecyclerView.Adapter adapter;
    private View productView,productEmptyView;

    public RVEmptyObserver(ViewGroup productView, ViewGroup productEmptyView, ProductsRecyclerViewAdapter adapter) {
        this.adapter = adapter;
        this.productView = productView;
        this.productEmptyView = productEmptyView;
    }

    @Override
    public void onChanged() {
        super.onChanged();
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        super.onItemRangeRemoved(positionStart, itemCount);
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (productEmptyView != null && adapter != null) {
            boolean emptyViewVisible = adapter.getItemCount() == 0;
            productEmptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            productView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }
}
