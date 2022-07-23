package com.showcase.tabra.ui.order;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.OrderListFragmentBinding;
import com.showcase.tabra.ui.common.DataFragment;

import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class OrderListFragment extends DataFragment implements OrderRecyclerViewAdapter.onOrderClickListener {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private @NonNull OrderListFragmentBinding binding;
    private OrderRecyclerViewAdapter adapter;
    private OrderViewModel viewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OrderListFragment newInstance(int columnCount) {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = OrderListFragmentBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // Set the adapter
        RecyclerView orderList = binding.orderList;
        orderList.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new OrderRecyclerViewAdapter();
        adapter.setClickListener(this);

        orderList.setAdapter(adapter);

        //Orders
        viewModel = new ViewModelProvider(requireActivity(), new OrderViewModelFactory(getActivity().getApplication()))
                .get(OrderViewModel.class);

        viewModel.getOrderListLiveData().observe(getViewLifecycleOwner(), new Observer<Result<List<Order>>>() {
            @Override
            public void onChanged(Result<List<Order>> result) {
                if (result == null) {
                    return;
                }
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    adapter.setOrders(((Result.Success<List<Order>>) result).getData());
                }
            }
        });

        // Create Toolbar
        Toolbar toolbar = binding.toolbarOrderList.orderListToolbar;
        toolbar.inflateMenu(R.menu.product_list_menu);
        Menu menu = toolbar.getMenu();


        return root;
    }

    @Override
    public void onOrderClick(View view, int position) {
        Order order = adapter.getItem(position);
        showOrderDetailsFragment(order);
        Toast.makeText(getActivity(), "Edit order", Toast.LENGTH_SHORT).show();

    }

    private void showOrderDetailsFragment(Order order) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Fragment f = fragmentManager.findFragmentByTag("OrderDetailsFragment");
        if (f!=null) {
            fragmentManager.beginTransaction()
                    .remove(f)
                    .commit();
        }

        OrderDetailFragment fragment = OrderDetailFragment.newInstance(order.getId().toString());

        fragmentManager.beginTransaction()
                .replace(R.id.order_list_frame, fragment, "OrderDetailsFragment")
                .addToBackStack(null)
                .commit();
    }

}