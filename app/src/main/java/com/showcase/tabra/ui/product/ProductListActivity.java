package com.showcase.tabra.ui.product;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
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
import com.showcase.tabra.ui.login.LoginActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProductListActivity extends AppCompatActivity implements ProductsRecyclerViewAdapter.onProductClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductsRecyclerViewAdapter adapter;
    private ProductViewModel viewModel;

    private SelectionTracker selectionTracker = null;
    private ProductListBinding binding;
    
    private ViewGroup productView;
    private ViewGroup productEmptyView;
    private ActionMode actionMode;
    private MenuItem selectedItemCount, action_product_list_show, action_product_list_hide, action_product_list_add;
    private RecyclerView recyclerView;

//    private FloatingActionButton fab;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Create adapter
        adapter = new ProductsRecyclerViewAdapter(this);
        adapter.setClickListener(this);
        adapter.registerAdapterDataObserver(new RVEmptyObserver(productView, productEmptyView, adapter));
        recyclerView.setAdapter(adapter);

        //Create tracer
        selectionTracker = new SelectionTracker.Builder<String>(
                "share-selection",
                recyclerView,
                new ProductKeyProvider(adapter),
                new ProductDetailsLookup(recyclerView),
                StorageStrategy.createStringStorage())
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
//                .withOnItemActivatedListener(new OnItemActivatedListener<String>() {
//                    @Override
//                    public boolean onItemActivated(@NonNull ProductDetailsLookup.ItemDetails item, @NonNull MotionEvent e) {
//                        Log.d("TAG", "Selected ItemId: " + item.toString());
//                        return true;
//                    }
//                })
                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
                    @Override
                    public boolean onDragInitiated(@NonNull MotionEvent e) {
                        Log.d("TAG", "onDragInitiated");
                        return true;
                    }
                })
                .build();
        adapter.setTracker(selectionTracker);
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
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
                if (selectionTracker.hasSelection() && actionMode == null) {
                    actionMode = startSupportActionMode(new ActionModeController(getApplicationContext(), selectionTracker));
                    setMenuItemTitle(selectionTracker.getSelection().size());

                } else if (!selectionTracker.hasSelection() && actionMode != null) {
                    actionMode.finish();
                    actionMode = null;
                } else {
                    setMenuItemTitle(selectionTracker.getSelection().size());

                }
                adapter.setSelectionMode(selectionTracker.hasSelection());

                Iterator<String> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i("TAG", itemIterable.next());
                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });


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
        selectedItemCount.setVisible(false);
        action_product_list_show = menu.findItem(R.id.action_product_list_show);
        action_product_list_show.setVisible(false);
        action_product_list_hide = menu.findItem(R.id.action_product_list_hide);
        action_product_list_hide.setVisible(false);
        action_product_list_add = menu.findItem(R.id.action_product_list_add);
        action_product_list_add.setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.action_product_list_add) {
            addProduct();
        }
        if (item.getItemId()==R.id.action_product_list_share) {
            if (selectionTracker.hasSelection()) {
                shareSelectedProducts(selectionTracker.getSelection());
            } else {
                shareAllProducts();
            }
        }
        if (item.getItemId()==R.id.action_item_count) {
            selectionTracker.clearSelection();
        }
        if (item.getItemId()==R.id.action_product_list_show) {
            if (selectionTracker.hasSelection()) {
                showSelectedProducts(selectionTracker.getSelection());
            }
            selectionTracker.clearSelection();
        }
        if (item.getItemId()==R.id.action_product_list_hide) {
            if (selectionTracker.hasSelection()) {
                hideSelectedProducts(selectionTracker.getSelection());
            }
            selectionTracker.clearSelection();
        }
        return true;
    }

    private void hideSelectedProducts(Selection selection) {
        Iterator<String> itemIterable = selection.iterator();
        while (itemIterable.hasNext()) {
            Product product = adapter.getProduct(itemIterable.next());
            product.setActive(false);
            viewModel.editProduct(product);
        }
    }

    private void showSelectedProducts(Selection selection) {
        Iterator<String> itemIterable = selection.iterator();
        while (itemIterable.hasNext()) {
            Product product = adapter.getProduct(itemIterable.next());
            product.setActive(true);
            viewModel.editProduct(product);
        }
    }

    private void shareSelectedProducts(Selection selection) {

        Iterator<String> itemIterable = selection.iterator();

        if (selection.size()>1) {
            final ArrayList<Uri>[] files = new ArrayList[]{new ArrayList<Uri>()};
            final int[] counter = {0};
            while (itemIterable.hasNext()) {
                Product product = adapter.getProduct(itemIterable.next());
                Picasso.get().load(product.getImage()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        files[0].add(getLocalBitmapUri(bitmap));
                        if (counter[0] <= files[0].size()) {
                            shareProducts(files[0]);
                        }
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        counter[0]++;
                    }
                });
            }
        } else {
            String key = null;
            while (itemIterable.hasNext()) {
                key = itemIterable.next();
            }
            Product product = adapter.getProduct(key);
            shareProduct(product);
        }
    }

    private void shareProducts(ArrayList<Uri> files) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        Intent chooser = Intent.createChooser(intent, "Share Image");
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            for (Uri uri : files) {
                grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        intent.setType("image/*");

        String text = getResources().getString(R.string.share_message_products_link) +" "+ getProductsLink();
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(chooser);
    }

    private void shareProduct(Product product) {
        Picasso.get().load(product.getImage()).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                String text = product.getName() + "\n" +
                        getResources().getString(R.string.share_message_price) +" "+ product.getPrice() + " " + product.getPrice_currency()+ " " + product.getUnitPrice() + "\n" +
                        product.getDescription() + "\n" +
                        getResources().getString(R.string.share_message_product_link) +" "+ getProductLink(product)
                       ;
                i.putExtra(Intent.EXTRA_TEXT, text);

                Uri uri = getLocalBitmapUri(bitmap);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.setType("image/*");

                Intent chooser = Intent.createChooser(i, "Share Image");
                List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    private String getProductLink(Product product) {
        return BuildConfig.API_URL+"products/"+product.getId().toString();
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
             bmpUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void setMenuItemTitle(int selectedItemSize) {
        action_product_list_show.setVisible(selectedItemSize > 0);
        action_product_list_hide.setVisible(selectedItemSize > 0);
        selectedItemCount.setVisible(selectedItemSize > 0);
        action_product_list_add.setVisible(!(selectedItemSize > 0));
        selectedItemCount.setTitle("Clean " + selectedItemSize);
    }

    private void shareAllProducts() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, getProductsLink());
        startActivity(Intent.createChooser(sendIntent, "Share URL"));
    }

    private String getProductsLink() {
        return BuildConfig.API_URL+"showcase/79685952835";
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