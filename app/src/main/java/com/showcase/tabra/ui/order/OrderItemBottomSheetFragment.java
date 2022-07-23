package com.showcase.tabra.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.showcase.tabra.data.model.Order;
import com.showcase.tabra.data.model.OrderItem;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.OrderDetailFragmentBinding;
import com.showcase.tabra.databinding.OrderItemDetailsBottomSheetBinding;

public class OrderItemBottomSheetFragment extends BottomSheetDialogFragment {
    private OrderItemDetailsBottomSheetBinding binding;
    private OrderItem item;
    private OrderViewModel viewModel;
    private TextInputLayout textInputLayoutOrderItemPrice, textInputLayoutOrderItemQuantity;


    public static OrderItemBottomSheetFragment newInstance(OrderItem item) {
        OrderItemBottomSheetFragment fragment = new OrderItemBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.item = (OrderItem) getArguments().getSerializable("item");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = OrderItemDetailsBottomSheetBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        //Orders
        this.viewModel = new ViewModelProvider(requireActivity(), new OrderViewModelFactory(getActivity().getApplication()))
                .get(OrderViewModel.class);

        //fill form
        textInputLayoutOrderItemPrice = binding.textInputLayoutOrderItemPrice;
        textInputLayoutOrderItemQuantity = binding.textInputLayoutOrderItemQuantity;
        fillItem(this.item);

        Button orderItemSave = binding.orderItemSave;
        orderItemSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sQuantity = textInputLayoutOrderItemQuantity.getEditText().getText().toString();
                if (!"".equals(sQuantity)) {
                    item.setQuantity(Integer.parseInt(textInputLayoutOrderItemQuantity.getEditText().getText().toString()));
                } else {
                    item.setQuantity(0);
                }

                String sPrice = textInputLayoutOrderItemPrice.getEditText().getText().toString();
                if (!"".equals(sPrice)) {
                    item.setPrice(Double.parseDouble(textInputLayoutOrderItemPrice.getEditText().getText().toString()));
                } else {
                    item.setPrice(0);
                }
                viewModel.updateOrderItem(item);
                dismiss();
            }
        });

        return root;
    }

    private void fillItem(OrderItem item) {
        this.textInputLayoutOrderItemPrice.getEditText().setText(item.getPrice().toString());
        this.textInputLayoutOrderItemQuantity.getEditText().setText(item.getQuantity().toString());
    }
}
