package com.showcase.tabra.ui.order;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.OrderItem;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.OrderDetailFragmentBinding;
import com.showcase.tabra.ui.common.DataFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderDetailFragment extends DataFragment implements OrderItemsRecyclerViewAdapter.onItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private OrderDetailFragmentBinding binding;
    private OrderViewModel viewModel;
    private OrderItemsRecyclerViewAdapter adapter;
    private TextView orderDetailsItemsClient;
    private TextView orderDetailsStep;
    private TextView orderDetailsPriceSum;
    private TextView orderDetailsItemsDate;
    private Order order;
    private String id;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderDetailFragment newInstance(String id) {
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString("id");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = OrderDetailFragmentBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        //set adapter
        RecyclerView orderItemsList = binding.orderItemsList;
        orderItemsList.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new OrderItemsRecyclerViewAdapter();
        adapter.setClickListener(this);

        orderItemsList.setAdapter(adapter);

        //Orders
        viewModel = new ViewModelProvider(requireActivity(), new OrderViewModelFactory(getActivity().getApplication()))
                .get(OrderViewModel.class);
        //View for edit product
        viewModel.getEditOrder(id).observe(getViewLifecycleOwner(), new Observer<Result<Order>>() {
            @Override
            public void onChanged(Result<Order> result) {
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    fillOrder(((Result.Success<Order>) result).getData());
                }
            }
        });
        viewModel.getEditOrderItem().observe(getViewLifecycleOwner(), new Observer<Result<OrderItem> >() {
            @Override
            public void onChanged(Result<OrderItem> result) {
                if (result instanceof Result.Error) {
                    //    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    OrderItem item = ((Result.Success<OrderItem>) result).getData();
                    viewModel.getEditOrder(id);
                }
            }
        });


        // Create Toolbar
        Toolbar toolbar = binding.toolbarOrderDetails.orderDetailsToolbar;
        toolbar.inflateMenu(R.menu.order_details_menu);
        Menu menu = toolbar.getMenu();


        ImageButton cancelButton = binding.toolbarOrderDetails.toolbarCancelButton;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        //binding
        this.orderDetailsItemsDate = binding.orderDetailsItemsDate;
        this.orderDetailsItemsClient = binding.orderDetailsItemsClient;
        this.orderDetailsPriceSum = binding.orderDetailsPriceSum;
        this.orderDetailsStep = binding.orderDetailsStep;


        return root;
    }

    private void fillOrder(Order order) {
        this.order = order;

        this.orderDetailsItemsClient.setText("Клиент: "+order.getName()+" "+order.getPhone());
        this.orderDetailsPriceSum.setText("Сумма: "+order.getSum_price()+" "+order.getSum_price_currency());


        this.orderDetailsStep.setText("Статус: "+(order.getStep()!=null? order.getStep().getName(): ""));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
        try {
            Date date = format.parse(order.getCreated_at());
            this.orderDetailsItemsDate.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        adapter.setOrderItemList(order.getItems());

    }

    @Override
    public void onItemClick(View view, int position) {
        OrderItem item = order.getItems().get(position);
        OrderItemBottomSheetFragment blankFragment = OrderItemBottomSheetFragment.newInstance(item);
        blankFragment.show(getActivity().getSupportFragmentManager(),blankFragment.getTag());
    }

}