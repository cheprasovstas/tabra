package com.showcase.tabra.ui.product;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.showcase.tabra.BuildConfig;
import com.showcase.tabra.R;
import com.showcase.tabra.data.MyException;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.ProductListBinding;
import com.showcase.tabra.databinding.ProductListEmptyBinding;
import com.showcase.tabra.ui.login.LoginActivity;

import java.util.Iterator;
import java.util.List;


public class ProductListActivity extends AppCompatActivity implements ProductsRecyclerViewAdapter.onProductClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductsRecyclerViewAdapter adapter;
    private ProductViewModel viewModel;

    private SelectionTracker tracker = null;
    private ProductListBinding binding;
    
    private ViewGroup productView;
    private ViewGroup productEmptyView;
    private ActionMode actionMode;
    private MenuItem selectedItemCount;
    private RecyclerView recyclerView;

//    private FloatingActionButton fab;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.product_list);
        binding = ProductListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productView = binding.productView;
        productEmptyView = binding.productsEmptyView.getRoot();

        FloatingActionButton fab = binding.productsEmptyView.floatingActionButton;
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        //Create Toolbar
        Toolbar toolbar = binding.productListToolbar;
        setSupportActionBar(toolbar);

        //Getting reference of swipeRefreshLayout and recyclerView
        swipeRefreshLayout = binding.refreshProducts;
        recyclerView = binding.products;
//        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        //Create adapter
        adapter = new ProductsRecyclerViewAdapter(this);
        adapter.setClickListener(this);
        adapter.registerAdapterDataObserver(new RVEmptyObserver(productView, productEmptyView, adapter));
        recyclerView.setAdapter(adapter);


        // Create viewModel
        viewModel = new ViewModelProvider(this, new ProductViewModelFactory(getApplication()))
                .get(ProductViewModel.class);
        viewModel.getProductListLiveData().observe(this, new Observer<Result<List<Product>>>() {
            @Override
            public void onChanged(Result<List<Product>> productsResult) {
                if (productsResult instanceof Result.Error) {
                    if (((Result.Error) productsResult).getError() instanceof MyException.LoginFailed401ReasonException) {
                        login();
                    }
                }
                if (productsResult instanceof Result.Success) {
                    adapter.setProducts(((Result.Success<List<Product>>) productsResult).getData());
                }
            }
        });



        viewModel.getNewProductLiveData().observe(this, new Observer<Result<Product>>() {
            @Override
            public void onChanged(Result<Product> result) {
                if (result instanceof Result.Error) {
                    if (((Result.Error) result).getError() instanceof MyException.LoginFailed401ReasonException) {
                        login();
                    }
                }
                if (result instanceof Result.Success) {
                    adapter.addProduct(((Result.Success<Product>) result).getData());

                }
            }
        });
        viewModel.getEditedProductLiveData().observe(this, new Observer<Result<Product>>() {
            @Override
            public void onChanged(Result<Product> result) {
                if (result instanceof Result.Error) {
                    if (((Result.Error) result).getError() instanceof MyException.LoginFailed401ReasonException) {
                        login();
                    }
                }
                if (result instanceof Result.Success) {
                    adapter.updateProduct(((Result.Success<Product>) result).getData());
                }
            }
        });

        // SetOnRefreshListener on SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        });

        getSupportFragmentManager().setFragmentResultListener("add_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
//                adapter.addProduct(viewModel.getProduct().getValue());
            }
        });
        getSupportFragmentManager().setFragmentResultListener("edit_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
//                String id = bundle.getString("id", "");
//                adapter.updateProduct(viewModel.getProduct().getValue());
            }
        });
        getSupportFragmentManager().setFragmentResultListener("delete_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                adapter.removeProduct(viewModel.getProduct().getValue());
            }
        });
    }

    private void login() {
        startActivity(new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_list_menu, menu);
        selectedItemCount = menu.findItem(R.id.action_item_count);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_product_list_add) {
            addProduct();

        }
        if(item.getItemId() == R.id.action_product_list_share) {
            setupSelectionTracker();


            //shareAllProducts();
        }
        return true;
    }

    private void setupSelectionTracker() {
        tracker = new SelectionTracker.Builder<String>(
                "share-selection",
                recyclerView,
                new ProductKeyProvider(adapter),
                new ProductDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .withOnItemActivatedListener(new OnItemActivatedListener<String>() {
                    @Override
                    public boolean onItemActivated(@NonNull ProductDetailsLookup.ItemDetails item, @NonNull MotionEvent e) {
                        Log.d("TAG", "Selected ItemId: " + item.toString());
                        return true;
                    }
                })
//                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
//                    @Override
//                    public boolean onDragInitiated(@NonNull MotionEvent e) {
//                        Log.d("TAG", "onDragInitiated");
//                        return true;
//                    }
//                })
                .build();
        adapter.setTracker(tracker);
        tracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (tracker.hasSelection() && actionMode == null) {
                    actionMode = startSupportActionMode(new ActionModeController(getApplicationContext(), tracker));
                    setMenuItemTitle(tracker.getSelection().size());
                } else if (!tracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    setMenuItemTitle(tracker.getSelection().size());

                }
                Iterator<String> itemIterable = tracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i("TAG", itemIterable.next());
                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });
    }

    public void setMenuItemTitle(int selectedItemSize) {
        selectedItemCount.setTitle("" + selectedItemSize);
    }

    private void shareAllProducts() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, BuildConfig.API_URL+"showcase/79685952835");
        //startActivity(sendIntent);
        startActivity(Intent.createChooser(sendIntent, "Title"));
    }

    private void addProduct() {
        viewModel.setProduct(new Product());

        Toast.makeText(this, "Add new product", Toast.LENGTH_SHORT).show();
        // Create new fragment and transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.rec, ProductDetailsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();

    }

    @Override
    public void onProductClick(View view, int position) {
        Product product = adapter.getItem(position);
        viewModel.setProduct(adapter.getItem(position));

        //If Selection Share Selection
//        product.setSelected(!product.isSelected());
//        adapter.notifyItemChanged(position);

        //If Edit Item
        Toast.makeText(this, "Edit product", Toast.LENGTH_SHORT).show();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.rec, ProductDetailsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }



}