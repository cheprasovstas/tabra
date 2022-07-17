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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Category;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.ProductListBinding;
import com.showcase.tabra.ui.common.DataFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ProductListFragment extends DataFragment implements ProductsRecyclerViewAdapter.onProductClickListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProductsRecyclerViewAdapter adapter;
    private ProductViewModel viewModel;

    private MutableLiveData<ProductFormFilter> productFormFilterLiveData = new MutableLiveData<ProductFormFilter>();

    private SelectionTracker selectionTracker = null;
    private ProductListBinding binding;

    private ViewGroup productView;
    private ViewGroup productEmptyView;
    private MenuItem action_product_list_show_hide;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddProduct;
    private ProgressBar loading;
    private MenuItem action_item_search;
    private ChipGroup chipGroup;
    private FloatingActionButton fabShareProducts;
    private ConstraintLayout toolbarSelectorPanel;
    private TextView selectorPanelTitle;


    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ProductListBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();

        // Create Toolbar
        Toolbar toolbar = binding.toolbarProductList.productListToolbar;
        toolbar.inflateMenu(R.menu.product_list_menu);
        Menu menu = toolbar.getMenu();

        toolbarSelectorPanel = binding.toolbarProductList.selectorPanel;
        ImageButton toolbarBackButton = binding.toolbarProductList.backButton;
        toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.editMode.equals(ProductsRecyclerViewAdapter.EditMode.Selection)) {
                    selectionTracker.clearSelection();
                    return;
                }
                adapter.setEditMode(ProductsRecyclerViewAdapter.EditMode.NONE);
                updateToolbarView();
            }
        });
        selectorPanelTitle = binding.toolbarProductList.selectorPanelTitle;

        action_item_search = menu.findItem(R.id.action_item_search);
        SearchView mSearchView = (SearchView) action_item_search.getActionView();
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                ProductFormFilter productFormFilter = productFormFilterLiveData.getValue();
                productFormFilter.setName(s);
                productFormFilterLiveData.postValue(productFormFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    ProductFormFilter productFormFilter = productFormFilterLiveData.getValue();
                    productFormFilterLiveData.getValue().setName(null);
                    productFormFilterLiveData.postValue(productFormFilter);
                }
                return true;
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId()==R.id.action_product_list_select) {
                    adapter.setEditMode(ProductsRecyclerViewAdapter.EditMode.Selection);
                    updateToolbarView();
                }
                if (item.getItemId()==R.id.action_product_list_in_store) {
                    adapter.setEditMode(ProductsRecyclerViewAdapter.EditMode.inStore);
                    updateToolbarView();
                }
                if (item.getItemId()==R.id.action_product_list_show_hide) {
                    adapter.setEditMode(ProductsRecyclerViewAdapter.EditMode.inArchive);
                    updateToolbarView();
                }
                return true;

            }
        });

        //productView and productEmptyView
        productView = binding.products;
        productEmptyView = binding.productsEmptyView.getRoot();
        loading = binding.loading;

        //Getting reference of swipeRefreshLayout and recyclerView
        swipeRefreshLayout = binding.refreshProducts;
        recyclerView = binding.products;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Create adapter
        adapter = new ProductsRecyclerViewAdapter(getActivity());
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
                adapter.setEditMode(selectionTracker.hasSelection() ? ProductsRecyclerViewAdapter.EditMode.Selection : ProductsRecyclerViewAdapter.EditMode.NONE);
                updateToolbarView();

            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });


        //Groups
        viewModel = new ViewModelProvider(requireActivity(), new ProductViewModelFactory(getActivity().getApplication()))
                .get(ProductViewModel.class);
        loading.setVisibility(View.VISIBLE);


        //Filter
        Chip chipAvailableFilter = binding.chipAvailableFilter;
        chipAvailableFilter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ProductFormFilter productFormFilter = productFormFilterLiveData.getValue();
                if (b) {
                    productFormFilter.setAvailable(true);
                } else {
                    productFormFilter.setAvailable(null);
                }

                productFormFilterLiveData.postValue(productFormFilter);

            }
        });


        //Groups
        chipGroup = binding.chipGroup;

        viewModel.getCategoryListLiveData().observe(getViewLifecycleOwner(), new Observer<Result<List<Category>>>() {
            @Override
            public void onChanged(Result<List<Category>> result) {
                if (result == null) {
                    return;
                }
                loading.setVisibility(View.GONE);
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    fillChipGroup(((Result.Success<List<Category>>) result).getData());
                }
            }
        });


        //Products List
        productFormFilterLiveData.postValue(new ProductFormFilter());
        productFormFilterLiveData.observe(getViewLifecycleOwner(), new Observer<ProductFormFilter>() {
            @Override
            public void onChanged(ProductFormFilter productFormFilter) {
                viewModel.searchProducts(productFormFilter.getName(),
                        productFormFilter.getCategory()!=null ? productFormFilter.getCategory().getId().toString(): null,
                        productFormFilter.getAvailable(),
                        null);
            }
        });


        viewModel.getProductListLiveData().observe(getViewLifecycleOwner(), new Observer<Result<List<Product>>>() {
            @Override
            public void onChanged(Result<List<Product>> result) {
                if (result == null) {
                    return;
                }
                loading.setVisibility(View.GONE);
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    adapter.setProducts(((Result.Success<List<Product>>) result).getData());
                }
            }
        });

        //Products New
        viewModel.getNewProductLiveData().observe(getViewLifecycleOwner(), new Observer<Result<Product>>() {
            @Override
            public void onChanged(Result<Product> result) {
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
                }
                if (result instanceof Result.Success) {
                    adapter.addProduct(((Result.Success<Product>) result).getData());
                }
            }
        });

        //Products Edit
        viewModel.getEditedProductLiveData().observe(getViewLifecycleOwner(), new Observer<Result<Product>>() {
            @Override
            public void onChanged(Result<Product> result) {
                if (result instanceof Result.Error) {
                    failResult((Result.Error) result);
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

        getActivity().getSupportFragmentManager().setFragmentResultListener("add_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
//                adapter.addProduct(viewModel.getProduct().getValue());
            }
        });
        getActivity().getSupportFragmentManager().setFragmentResultListener("edit_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
//                String id = bundle.getString("id", "");
//                adapter.updateProduct(viewModel.getProduct().getValue());
            }
        });
        getActivity().getSupportFragmentManager().setFragmentResultListener("delete_product", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                adapter.removeProduct(viewModel.getEditProduct().getValue());
            }
        });


        fabAddProduct = binding.fabAddProducts;
        fabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        fabShareProducts = binding.fabShareProducts;
        fabShareProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectionTracker.hasSelection()) {
                    shareSelectedProducts(selectionTracker.getSelection());
                }
            }
        });

        
        Button button = binding.productsEmptyView.button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProduct();
            }
        });

        return root;
    }


    private void fillChipGroup(List<Category> categoryList) {

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {

                ProductFormFilter productFormFilter = productFormFilterLiveData.getValue();
                if (checkedId != -1) {
                    Category category = categoryList.get(checkedId);
                    productFormFilter.setCategory(category);
                } else {
                    productFormFilter.setCategory(null);
                }

                productFormFilterLiveData.postValue(productFormFilter);
            }
        });

        chipGroup.removeAllViews();
        for (int i = 0; i < categoryList.size(); i++) {
            Category item = categoryList.get(i);
            Chip chip = new Chip(getContext());
            chip.setId(i);
            chip.setText(item.getName());
            chip.setCheckable(true);
            chipGroup.addView(chip);
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
        List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            for (Uri uri : files) {
                getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        intent.setType("image/*");

//        String text = getResources().getString(R.string.share_message_products_link) +" "+ getProductsLink();
//        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(chooser);
    }

    private void shareProduct(Product product) {
        Picasso.get().load(product.getImage()).into(new Target() {
            @Override public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Intent i = new Intent(Intent.ACTION_SEND);
                String text = (product.isInStore() ? "В Наличии: ": "Под заказ: ") +
                        product.getName() + "\n" +
                        getResources().getString(R.string.share_message_price) +" "+ product.getPrice() + " " + product.getPrice_currency()+ " " + product.getUnitPrice() + "\n" +
                        product.getDescription() + "\n" +
                        getResources().getString(R.string.share_message_product_link) +" "+ product.getProductUrl()
                       ;
                i.putExtra(Intent.EXTRA_TEXT, text);

                Uri uri = getLocalBitmapUri(bitmap);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                i.setType("image/*");

                Intent chooser = Intent.createChooser(i, "Share Image");
                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(chooser);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
            @Override public void onPrepareLoad(Drawable placeHolderDrawable) { }
        });
    }

    private Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
             bmpUri = FileProvider.getUriForFile(getActivity().getApplicationContext(), getActivity().getApplicationContext().getPackageName() + ".provider", file);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    public void updateToolbarView() {

        switch (adapter.editMode){
            case Selection:
                toolbarSelectorPanel.setVisibility(View.VISIBLE);
                selectorPanelTitle.setText("");

                fabAddProduct.setVisibility(View.GONE);
                fabShareProducts.setVisibility(View.VISIBLE);
                break;
            case inStore:
                toolbarSelectorPanel.setVisibility(View.VISIBLE);
                selectorPanelTitle.setText(R.string.product_list_action_instore_title);

                fabAddProduct.setVisibility(View.GONE);
                fabShareProducts.setVisibility(View.GONE);
                break;
            case inArchive:
                toolbarSelectorPanel.setVisibility(View.VISIBLE);
                selectorPanelTitle.setText(R.string.product_list_action_show_title);

                fabAddProduct.setVisibility(View.GONE);
                fabShareProducts.setVisibility(View.GONE);
                break;
            case NONE:
                toolbarSelectorPanel.setVisibility(View.GONE);
                fabAddProduct.setVisibility(View.VISIBLE);
                fabShareProducts.setVisibility(View.GONE);
                break;

        }



//        action_item_search.setVisible(!(selectedItemSize > 0));
    }

    private void addProduct() {
        viewModel.setEditProduct(new Product());
        Toast.makeText(getActivity(), getString(R.string.toast_add_new_product), Toast.LENGTH_SHORT).show();
        showProductDetailsFragment();

    }


    @Override
    public void onProductClick(View view, int position) {
        Product product = adapter.getItem(position);
        viewModel.setEditProduct(product);

        switch (adapter.editMode) {
            case inStore:
                product.setInStore(!product.isInStore());
                viewModel.updateProduct(product);
                return;
            case inArchive:
                product.setActive(!product.isActive());
                viewModel.updateProduct(product);
                return;
            case Selection:
                selectionTracker.select(product.getId().toString());
                return;
        }

        Toast.makeText(getActivity(), getString(R.string.toast_edit_product), Toast.LENGTH_SHORT).show();
        showProductDetailsFragment();

    }

    private void showProductDetailsFragment() {
        // Create new fragment and transaction
//        fab.setVisibility(View.GONE);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        Fragment f = fragmentManager.findFragmentByTag("ProductDetailsFragment");
        if (f!=null) {
            fragmentManager.beginTransaction()
                    .remove(f)
                    .commit();
        }
        fragmentManager.beginTransaction()
                    .replace(R.id.details, ProductDetailsFragment.class, null, "ProductDetailsFragment")
//                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
    }
}