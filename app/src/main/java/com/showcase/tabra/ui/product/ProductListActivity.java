package com.showcase.tabra.ui.product;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.showcase.tabra.BuildConfig;
import com.showcase.tabra.R;
import com.showcase.tabra.data.MyException;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.ui.login.LoginActivity;

import java.util.List;


public class ProductListActivity extends AppCompatActivity implements ProductsRecyclerViewAdapter.onProductClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductsRecyclerViewAdapter adapter;
    private ProductViewModel viewModel;
    private SelectionTracker<Long> tracker = null;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);

        //Create Toolbar
        Toolbar toolbar = findViewById(R.id.product_list_toolbar);
        setSupportActionBar(toolbar);

        // Getting reference of swipeRefreshLayout and recyclerView
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_products);
        RecyclerView recyclerView = findViewById(R.id.products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Create adapter
        adapter = new ProductsRecyclerViewAdapter(this);
        adapter.setClickListener(this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_product_list_add) {
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
        if(item.getItemId() == R.id.action_product_list_share) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, BuildConfig.API_URL+"showcase/79685952835");
            //startActivity(sendIntent);
            startActivity(Intent.createChooser(sendIntent, "Title"));
        }
        return true;
    }

    @Override
    public void onProductClick(View view, int position) {
        Toast.makeText(this, "Edit product", Toast.LENGTH_SHORT).show();
        Product product = adapter.getItem(position);
        viewModel.setProduct(adapter.getItem(position));

        //Selection Multy
//        product.setSelected(!product.isSelected());
//        view.setBackgroundColor(product.isSelected() ? Color.CYAN : Color.WHITE);

        // Create new fragment and transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.rec, ProductDetailsFragment.class, null)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }
}